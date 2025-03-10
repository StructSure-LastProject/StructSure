import {createSignal, Show, createEffect, createMemo} from "solid-js";
import { Plus } from 'lucide-solid';
import ModalAddPlan from '../Plan/ModalAddPlan';
import ModalEditPlan from '../Plan/ModalEditPlan';
import DropdownsSection from "../Plan/DropdownsSection.jsx";
import StructureDetailCanvas from "./StructureDetailCanvas";
import useFetch from "../../hooks/useFetch.js";
import {useNavigate, useSearchParams, useLocation} from "@solidjs/router";
import {sensorsWithoutLimitAndOffsetFetchRequest} from "./StructureDetailBody.jsx";


/**
 * Will fetch the plan image
 */
export const planImageFetchRequest = async (planId, setPlan, navigate) => {
    const requestData = {
        method: "GET",
        headers: {
            "Content-Type": "application/json"
        }
    };

    const { fetchImage, image, statusCode } = useFetch();
    await fetchImage(navigate, `/api/structures/plans/${planId()}/image`, requestData);
    if (statusCode() === 200) {
        setPlan(image());
    } else {
        setPlan(null);
    }
};

/**
 * Shows the plans part
 * @returns the component for the plans part
 */
function StructureDetailPlans(props) {
    const navigate = useNavigate();
    const location = useLocation();
    const [searchParams] = useSearchParams();

    // Plans and modals state management
    const [plans, setPlans] = createSignal([]);
    const [sensors, setSensors] = createSignal([]);
    const [isAddModalOpen, setIsAddModalOpen] = createSignal(false);
    const [isEditModalOpen, setIsEditModalOpen] = createSignal(false);
    const [selectedPlan, setSelectedPlan] = createSignal(null);

    const [isAuthorized, setIsAuthorized] = createSignal(false);
    const [plan, setPlan] = createSignal(null);

    /**
     * Tell whether the page is in scan mode
     * @return the page is in scan mode or not
     */
    const isInScanMode = createMemo(() => {
        const scanParam = searchParams.selectedScan;
        return scanParam !== undefined && scanParam !== "-1";
    });

    /**
     * Opens the add plan modal
     */
    const openAddModal = () => setIsAddModalOpen(true);

    /**
     * Closes the add plan modal
     */
    const closeAddModal = () => setIsAddModalOpen(false);

    /**
     * Closes the edit plan modal and clears selected plan
     */
    const closeEditModal = () => {
        setIsEditModalOpen(false);
        setSelectedPlan(null);
    };

    /**
     * Generates the URL for a plan's image
     * @param {number|string} planId Plan identifier
     * @returns {string} Complete URL for the plan image
     */
    const getImageUrl = (planId) => {
        return `/api/structures/plans/${planId}/image`;
    };

    createEffect(() => {
        if (props.selectedPlanId()) {
            planImageFetchRequest(props.selectedPlanId, setPlan, navigate);
        }
    });

    /**
     * Handles the edit action for a plan
     * @param {number|string} planId Identifier of the plan to edit
     */
    const handleEdit = (planId) => {
        const pl = plans().find(p => p.id === planId);
        if (pl) {
            setSelectedPlan({
                ...pl,
                imageUrl: getImageUrl(pl.id)
            });
            setIsEditModalOpen(true);
        }
    };

    /**
     * Handles saving the edited plan data
     * @param {Object} formData Form data containing the edited plan information
     */
    const handleEditSave = (formData) => {
        setPlans(prev => prev.map(p =>
          p.id === formData.id
            ? {
                ...p,
                name: formData.metadata.name,
                section: formData.metadata.section,
                type: p.archived ? "archived" : (isAuthorized() ? "edit" : "plan")
            }
            : p
        ));
        closeEditModal();
    };

    /**
     * Handles saving a newly added plan
     * @param {Object} formData Form data containing the new plan information
     */
    const handleAddSave = (formData) => {
        const newPlan = {
            id: formData.id,
            name: formData.metadata.name,
            section: formData.metadata.section || "",
            type: isAuthorized() ? "edit" : "plan",
            archived: false
        };

        setPlans(prev => [...prev, newPlan]);
        closeAddModal();
    };

    /**
     * Handles restoring a plan locally
     * @param planId The plan id
     */
    const handlePlanRestore = (planId) => {
        setPlans(prevPlans =>
          prevPlans.map(p =>
            p.id === planId
              ? { ...p, archived: false, type: isAuthorized() ? "edit" : "plan" }
              : p
          )
        );
    }

    /**
     * Handles archive a plan locally
     * @param planId The plan id
     */
    const handlePlanArchive = (planId) => {
        setPlans(prevPlans =>
          prevPlans.map(p =>
            p.id === planId
              ? { ...p, archived: true, type: "archived" }
              : p
          )
        );

        if (props.selectedPlanId() === planId) {
            const nonArchivedPlans = plans().filter(p => !p.archived && p.id !== planId);

            if (nonArchivedPlans.length > 0) {
                props.setSelectedPlanId(nonArchivedPlans[0].id);
                const newSearchParams = { ...searchParams };
                newSearchParams.selectedPlanId = nonArchivedPlans[0].id;
                const searchParamsString = new URLSearchParams(newSearchParams).toString();
                navigate(`${location.pathname}${searchParamsString ? '?' + searchParamsString : ''}`, { replace: true });
            } else {
                props.setSelectedPlanId(null);
                const newSearchParams = { ...searchParams };
                delete newSearchParams.selectedPlanId;
                const searchParamsString = new URLSearchParams(newSearchParams).toString();
                navigate(`${location.pathname}${searchParamsString ? '?' + searchParamsString : ''}`, { replace: true });
            }
            setPlan(null);
        }
        sensorsWithoutLimitAndOffsetFetchRequest(
          props.structureId,
          setSensors,
          () => {/*We don't need to set total items here*/},
          navigate
        )
        closeEditModal();
    }

    /**
     * Effect that updates plans based on props and user role
     */
    createEffect(() => {
        const userRole = localStorage.getItem("role");
        const isOperator = userRole === "OPERATEUR";
        setIsAuthorized(userRole === "ADMIN" || userRole === "RESPONSABLE");

        if (props.structureDetails().sensors) {
            setSensors(props.structureDetails().sensors);
        }

        if (props.structureDetails().plans) {
            let plansToDisplay = isOperator
              ? props.structureDetails().plans.filter(p => !p.archived)
              : props.structureDetails().plans;

            const newPlans = plansToDisplay.map(p => {
                if (p.archived) {
                    return {
                        ...p,
                        type: "archived",
                        section: p.section || ""
                    };
                }
                return {
                    ...p,
                    type: isAuthorized() ? "edit" : "plan",
                    section: p.section || ""
                };
            });
            setPlans(newPlans);

            if (plansToDisplay.length <= 0) {
                setPlan(null)
            }
        }
    });


    return (
        <>
            <div class="flex flex-col lg:flex-row rounded-[20px] bg-E9E9EB">
                <div class="flex flex-col gap-y-[15px] lg:w-[25%] m-5 max-h-[350px] lg:max-h-[436px]">
                    <div class="flex items-center justify-between">
                        <p class="title">Plans</p>
                        <Show when={isAuthorized() && !isInScanMode()}>
                            <button
                            title="Ajouter un plan"
                            onClick={openAddModal}
                            class="bg-white rounded-[50px] h-[40px] w-[40px] flex items-center justify-center"
                            >
                                <Plus color="black"/>
                            </button>
                        </Show>
                    </div>
                    <div
                    class="flex flex-col gap-y-[5px] overflow-y-auto [&::-webkit-scrollbar]:hidden [-ms-overflow-style:none] [scrollbar-width:none]">
                        <DropdownsSection
                            data={plans()}
                            selectedPlanId={props.selectedPlanId}
                            setSelectedPlanId={props.setSelectedPlanId}
                            onEdit={handleEdit}
                            onPlanRestore={handlePlanRestore}
                            structureId={props.structureId}
                            isInScanMode={isInScanMode}
                        />
                    </div>
                    <Show when={isAddModalOpen()}>
                        <ModalAddPlan
                            isOpen={isAddModalOpen()}
                            onSave={handleAddSave}
                            onClose={closeAddModal}
                            structureId={props.structureId}
                        />
                    </Show>
                    <Show when={isEditModalOpen() && selectedPlan()}>
                        <ModalEditPlan
                            onSave={handleEditSave}
                            onClose={closeEditModal}
                            onPlanArchive={handlePlanArchive}
                            structureId={props.structureId}
                            plan={selectedPlan()}
                            setPlan={setPlan}
                            selectedPlanId={props.selectedPlanId}
                        />
                    </Show>
                </div>

              <div class="lg:w-[75%] rounded-[20px] bg-white">
                  <div class="w-full p-[20px]">
                      <div class="w-full relative">
                          <Show when={plan() !== null}>
                              <StructureDetailCanvas
                                structureId={props.structureId}
                                plan={plan}
                                interactiveMode={true}
                                planSensors={props.planSensors}
                                structureDetails={props.structureDetails}
                                localSensors={sensors}
                                setLocalSensors={setSensors}
                                setPlanSensors={props.setPlanSensors}
                                sensors={props.sensors}
                                setSensors={props.setSensors}
                                setSensorsDetail={props.setSensorsDetail}
                                selectedPlanId={props.selectedPlanId}
                              />
                          </Show>
                      </div>
                  </div>
              </div>
          </div>
      </>
    );
}

export default StructureDetailPlans