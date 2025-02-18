import { ChevronDown, Dot, ChevronRight, Pencil, FolderSync } from 'lucide-solid';
import {createSignal, Show} from "solid-js";
import ModalEditPlan from "./Plan/ModalEditPlan.jsx";

const API_BASE_URL = import.meta.env.VITE_API_URL || '';

/**
 * Show the section part component
 * @returns the component for the section part 
 */
function StructureDetailSection() {
    const [isEditModalOpen, setIsEditModalOpen] = createSignal(false);
    const [selectedPlan, setSelectedPlan] = createSignal(null);

    /**
     * Build the image url
     * @param planId id of the actual plan
     * @returns {`${any}/api/structures/plans/${string}/image`|`http://localhost:8080/api/structures/plans/${string}/image`}
     */
    const getImageUrl = (planId) => {
        return `${API_BASE_URL}/api/structures/plans/${planId}/image`;
    };

    /**
     * Build the actual plan object with the image url
     * @param plan actual plan object
     */
    const openEditModal = (plan) => {
        setSelectedPlan({
            ...plan,
            imageUrl: getImageUrl(plan.id)
        });
        setIsEditModalOpen(true);
    };

    /**
     * Close the edit modal and reset the selected plan
     */
    const closeEditModal = () => {
        setIsEditModalOpen(false);
        setSelectedPlan(null);
    };

    /**
     * Close and log the result of editing the actual plan
     * todo made it dynamic and not static and remove console.log
     * @param updatedPlan result of the edition
     */
    const handleSavePlan = (updatedPlan) => {
        console.log('Plan updated:', updatedPlan);
        closeEditModal();
    };

    return (
        <div class="flex flex-col gap-y-[5px]">
            <div class="flex flex-col gap-y-[5px]">
                <div class="bg-white px-[8px] py-[9px] flex gap-x-[10px] rounded-[10px]">
                    <div class="w-4 h-4">
                        <ChevronDown />
                    </div>
                    <p class="prose font-poppins poppins text-base font-semibold">Section OA</p>
                </div>
                <div class="px-[8px] py-[9px] rounded-[10px] ml-4 flex gap-x-[10px] justify-between">
                    <div class="flex gap-x-[10px]">
                        <div class="w-4 h-4">
                            <Dot />
                        </div>
                        <p class="prose font-poppins poppins text-base font-semibold">Plan 01</p>
                    </div>
                </div>
                <div class="px-[8px] py-[9px] rounded-[10px] ml-4 flex gap-x-[10px] justify-between">
                    <div class="flex gap-x-[10px]">
                        <div class="w-4 h-4">
                            <Dot />
                        </div>
                        <p class="prose font-poppins poppins text-base font-semibold">Plan 02</p>
                    </div>
                </div>
                <div class="px-[8px] py-[9px] rounded-[10px] ml-4 flex gap-x-[10px] bg-[#F2F2F4] justify-between">
                    <div class="flex gap-x-[10px]">
                        <div class="w-4 h-4">
                            <Dot />
                        </div>
                        <p class="prose font-poppins poppins text-base font-semibold">Plan 03</p>
                    </div>
                    <div class="w-5 h-5">
                        <button
                          title="Editer un plan"
                          onclick={() => openEditModal({
                              id: 8,
                              name: "Plan 03",
                              section: "OA"
                          })}
                        >
                            <Pencil size={20}/>
                        </button>
                    </div>
                </div>
                <div class="px-[8px] py-[9px] rounded-[10px] ml-4 flex gap-x-[10px] justify-between">
                    <div class="flex gap-x-[10px]">
                        <div class="w-4 h-4">
                            <Dot />
                        </div>
                        <p class="prose font-poppins poppins text-base font-semibold">Plan 04</p>
                    </div>
                    <div class="w-5 h-5">
                        <FolderSync size={20} />
                    </div>
                </div>
            </div>
            <div class="flex flex-col gap-y-[5px]">
                <div class="bg-white px-[8px] py-[9px] flex gap-x-[10px] rounded-[10px]">
                    <div class="w-4 h-4">
                        <ChevronRight />
                    </div>
                    <p class="prose font-poppins poppins text-base font-semibold">Section OB</p>
                </div>
            </div>

            <Show when={isEditModalOpen() && selectedPlan()}>
                <ModalEditPlan
                  isOpen={isEditModalOpen()}
                  onSave={handleSavePlan}
                  onClose={closeEditModal}
                  structureId={1}
                  plan={selectedPlan()}
                />
            </Show>
        </div>
    );
}

export default StructureDetailSection

