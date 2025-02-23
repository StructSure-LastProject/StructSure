import { createSignal, Show, createEffect } from "solid-js";
import { Plus } from 'lucide-solid';
import plan from '/src/assets/plan.png';
import ModalAddPlan from '../Plan/ModalAddPlan';
import ModalEditPlan from '../Plan/ModalEditPlan';
import DropdownsSection from "../Plan/DropdownsSection.jsx";
import StructureDetailCanvas from "./StructureDetailCanvas";

/**
 * Shows the plans part
 * @returns the component for the plans part
 */
function StructureDetailPlans(props) {
 

    // Plans and modals state management
    const [plans, setPlans] = createSignal([]);
    const [selectedPlanId, setSelectedPlanId] = createSignal(null);
    const [isAddModalOpen, setIsAddModalOpen] = createSignal(false);
    const [isEditModalOpen, setIsEditModalOpen] = createSignal(false);
    const [selectedPlan, setSelectedPlan] = createSignal(null);

    const [isAuthorized, setIsAuthorized] = createSignal(false);


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
        // todo remove the part you don't need
        const API_BASE_URL = window.location.origin || 'http://localhost:8080';
        return `${API_BASE_URL}/api/structures/plans/${planId}/image`;
    };


    
    /**
     * Handles the edit action for a plan
     * @param {number|string} planId Identifier of the plan to edit
     */
    const handleEdit = (planId) => {
        const plan = plans().find(p => p.id === planId);
        if (plan) {
            setSelectedPlan({
                ...plan,
                imageUrl: getImageUrl(plan.id)
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

        setPlans(prev => prev.map(plan =>
          plan.id === formData.id
            ? {
                ...plan,
                name: formData.metadata.name,
                section: formData.metadata.section,
                type: plan.archived ? "archived" : (canEdit ? "edit" : "plan")
            }
            : plan
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
        if (props.plans) {
            const newPlans = props.plans.map(plan => {
                if (plan.archived) {
                    return {
                        ...plan,
                        type: "archived",
                        section: plan.section || ""
                    };
                }
                return {
                    ...plan,
                    type: isAuthorized() ? "edit" : "plan",
                    section: plan.section || ""
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
                        selectedPlanId={selectedPlanId()}
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
                            <StructureDetailCanvas plan={plan} interactiveMode={true} planSensors={props.planSensors} />
                        
                        </div>
                    </div>
                </div>
            </div>
        </>
    );
}


export default StructureDetailPlans
