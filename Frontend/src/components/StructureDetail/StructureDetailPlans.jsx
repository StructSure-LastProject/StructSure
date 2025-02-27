import { createSignal, Show, createEffect } from "solid-js";
import { Plus } from 'lucide-solid';
import ModalAddPlan from '../Plan/ModalAddPlan';
import ModalEditPlan from '../Plan/ModalEditPlan';
import DropdownsSection from "../Plan/DropdownsSection.jsx";
import StructureDetailCanvas from "./StructureDetailCanvas";
import useFetch from "../../hooks/useFetch.js";
import { useNavigate } from "@solidjs/router";


/**
 * Will fetch the plan image
 */
export const planImageFetchRequest = async (planId, setPlan) => {
    const navigate = useNavigate();
    const requestData = {
        method: "GET",
        headers: {
            "Content-Type": "application/json"
        }
    };

    const { fetchImage, image, statusCode } = useFetch();
    await fetchImage(`/api/structures/plans/${planId()}/image`, requestData);
    if (statusCode() === 200) {
        setPlan(image());
    } else if (statusCode() === 401) {
        navigate("/login");
    }
};

/**
 * Shows the plans part
 * @returns the component for the plans part
 */
function StructureDetailPlans(props) {
 

    // Plans and modals state management
    const [plans, setPlans] = createSignal([]);
    const [isAddModalOpen, setIsAddModalOpen] = createSignal(false);
    const [isEditModalOpen, setIsEditModalOpen] = createSignal(false);
    const [selectedPlan, setSelectedPlan] = createSignal(null);

    const [isAuthorized, setIsAuthorized] = createSignal(false);
    const [plan, setPlan] = createSignal(null);

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
            planImageFetchRequest(props.selectedPlanId, setPlan);
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
        const userRole = localStorage.getItem("role");
        const canEdit = userRole === "ADMIN" || userRole === "RESPONSABLE";

        setPlans(prev => prev.map(p =>
          p.id === formData.id
            ? {
                ...p,
                name: formData.metadata.name,
                section: formData.metadata.section,
                type: p.archived ? "archived" : (canEdit ? "edit" : "plan")
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
     * Effect that updates plans based on props and user role
     */
    createEffect(() => {
        const userRole = localStorage.getItem("role");
        setIsAuthorized(userRole === "ADMIN" || userRole === "RESPONSABLE")
        if (props.structureDetails().plans) {
            const newPlans = props.structureDetails().plans.map(p => {
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
        }
    });


    return (
        <>
            <div class="flex flex-col lg:flex-row rounded-[20px] bg-E9E9EB">
                <div class="flex flex-col gap-y-[15px] lg:w-[25%] m-5 max-h-[350px] lg:max-h-[436px]">
                    <div class="flex items-center justify-between">
                        <p class="title">Plans</p>
                        <Show when={isAuthorized()}>
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
                            onPlanEdit={handleEditSave}
                            structureId={props.structureId}
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
                        isOpen={isEditModalOpen()}
                        onSave={handleEditSave}
                        onClose={() => {
                            setIsEditModalOpen(false);
                            setSelectedPlan(null);
                        }}
                        structureId={props.structureId}
                        plan={selectedPlan()}
                        />
                    </Show>
                </div>

                <div class="lg:w-[75%] rounded-[20px] bg-white">
                    <div class="w-full p-[20px]">
                        <div class="w-full relative">
                            <Show when={plan() !== null}>
                                <StructureDetailCanvas structureId={props.structureId} plan={plan} interactiveMode={true} planSensors={props.planSensors} structureDetails={props.structureDetails} 
                                setPlanSensors={props.setPlanSensors} setSensors={props.setSensors} selectedPlanId={props.selectedPlanId}/>
                            </Show>
                        </div>
                    </div>
                </div>
            </div>
        </>
    );
}

export default StructureDetailPlans
