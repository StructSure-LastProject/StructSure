import {Download, Pencil, Plus, Trash2} from 'lucide-solid';
import {createEffect, createSignal, For, Show} from 'solid-js';
import StructureDetailEdit from './StructureDetailEdit';
import {planSensorsFetchRequest, planSensorsScanFetchRequest, sensorsFetchRequest} from "./StructureDetailBody.jsx";
import {useNavigate} from "@solidjs/router";
import useFetch from "../../hooks/useFetch.js";
import ArchiveModal from "../ArchiveModal.jsx";

/**
 * Show the head part of the structure detail
 * @returns the component for the head part
 */
function StructureDetailHead({setTotalItems, setSensors, setNote, selectedPlan, setPlanSensors, selectedScan, setSelectedScan, structureDetails, setStructureDetails}) {
    const [isModalVisible, setModalVisible] = createSignal(false);

    const [isAuthorized, setIsAuthorized] = createSignal(false);
    const [name, setName] = createSignal("");
    const [date, setDate] = createSignal("");

    const navigate = useNavigate();

    /**
     * Effect that updates authorized roles
     */
    createEffect(() => {
        const userRole = localStorage.getItem("role");
        setIsAuthorized((userRole === "ADMIN" || userRole === "RESPONSABLE") && selectedScan() <= -1);
    })

    /**
     * Will Open the modal that edits the strucutre
     */
    const openModal = () => setModalVisible(true);
    /**
     * Handles close modal hat edits the strucutre
     */
    const closeModal = () => setModalVisible(false);

    /**
     * Handle scan selection change
     * @param {Event} event - The change event
     */
    const handleScanChange = (event) => {
        const selectedValue = event.target.value;
        if (selectedValue > -1) {
            const selectedScan = structureDetails().scans.find(scan => scan.id.toString() === selectedValue);
            if (selectedScan) {
                setName(selectedScan.name || "");
                setDate(selectedScan.date || "");
                setNote(selectedScan.note || "");
                setSelectedScan(selectedValue);
                planSensorsScanFetchRequest(structureDetails().id, selectedValue, selectedPlan(), setPlanSensors, navigate);
                sensorsFetchRequest(structureDetails().id, setSensors, setTotalItems, navigate, {scanFilter: selectedValue});
            }
        } else {
            setName("");
            setDate("");
            setNote(structureDetails().note);
            setSelectedScan(selectedValue);
            planSensorsFetchRequest(structureDetails().id, setPlanSensors, selectedPlan(), setPlanSensors, navigate);
            sensorsFetchRequest(structureDetails().id, setSensors, setTotalItems, navigate);
        }
    };

    const [showArchiveModal, setShowArchiveModal] = createSignal(false);

    /**
     * Handle click on an active structure
     */
    const handleArchiveClick = () => {
        setShowArchiveModal(true);
    };

    /**
     * Close the archive modal
     */
    const closeArchiveModal = () => {
        setShowArchiveModal(false);
        setErrorMsgArchiveStructure("");
    };

    const [errorMsgArchiveStructure, setErrorMsgArchiveStructure] = createSignal("");

    /**
     * Handle successful structure archiving
     * @param {Object} archivedStructure The archived structure data from the API
     */
    const handleArchiveSuccess = (archivedStructure) => {
        closeArchiveModal();
        navigate("/");
    };

    return (
        <>
            <div class="flex flex-col gap-y-2.5">
                <p class="title">{structureDetails().name}</p>
                <div class="flex gap-x-[10px]">
                    <select class="px-4 py-2 w-full h-10 rounded-[20px] subtitle bg-white"
                            onChange={handleScanChange}
                    >
                        <option value="-1">Aucun Scan Sélectionné</option>
                        <For each={structureDetails().scans}>
                            {(scan) => (
                              <option value={scan.id}>{scan.dataRow}</option>
                            )}
                        </For>
                    </select>
                    <div class="flex gap-x-[10px]">
                        <Show when={isAuthorized()}>
                            <button
                              class="bg-white rounded-[50px] h-[40px] w-[40px] flex items-center justify-center"
                              onclick={openModal}
                            >
                                <Pencil size={20} />
                            </button>
                            <button
                              class="bg-[#F133271A] rounded-[50px] h-[40px] w-[40px] flex items-center justify-center"
                              onclick={handleArchiveClick}
                            >
                                <Trash2 color="red" size={20} />
                            </button>
                        </Show>
                        <button class="bg-white rounded-[50px] h-[40px] w-[40px] flex items-center justify-center">
                            <Download size={20} />
                        </button>
                        <button class="bg-black rounded-[50px] h-[40px] w-[40px] flex items-center justify-center">
                            <Plus color="white" size={20} />
                        </button>
                    </div>
                </div>
                <div class="flex justify-between px-[10px]">
                    <p class="normal">{name()}</p>
                    <p class="normal opacity-50">{date()}</p>
                </div>
                <StructureDetailEdit
                  setNote={setNote}
                  setStructureDetails={setStructureDetails}
                  isModalVisible={isModalVisible}
                  closeModal={closeModal}
                  selectedScan={selectedScan}
                  structureDetails={structureDetails}
                />
            </div>
            <Show when={showArchiveModal()}>
                <ArchiveModal
                  structure={structureDetails()}
                  onClose={closeArchiveModal}
                  onArchive={handleArchiveSuccess}
                  errorMsgArchiveStructure={errorMsgArchiveStructure}
                  setErrorMsgArchiveStructure={setErrorMsgArchiveStructure}
                />
            </Show>
        </>
    );
}

export default StructureDetailHead

