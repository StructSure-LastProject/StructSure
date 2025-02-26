import {Download, Pencil, Plus, Trash2} from 'lucide-solid';
import {createEffect, createSignal, For, Show} from "solid-js";
import {
  planSensorsFetchRequest,
  planSensorsScanFetchRequest,
  sensorsFetchRequest
} from "./StructureDetailBody.jsx";

/**
 * Show the head part of the structure detail
 * @returns the component for the head part
 */
function StructureDetailHead({setScanChanged, structureId, selectedPlanId, structureDetails, setPlanSensors, setSensors, setNote, setTotalItems}) {
  const [isAuthorized, setIsAuthorized] = createSignal(false);
  const [name, setName] = createSignal("");
  const [date, setDate] = createSignal("");

  /**
   * Effect that updates authorized roles
   */
  createEffect(() => {
    const userRole = localStorage.getItem("role");
    setIsAuthorized(userRole === "ADMIN" || userRole === "RESPONSABLE");
  })

  /**
   * Handle scan selection change
   * @param {Event} event - The change event
   */
  const handleScanChange = (event) => {
    const selectedValue = event.target.value;
    if (selectedValue >= 0) {
      const selectedScan = structureDetails().scans.find(scan => String.valueOf(scan.id) === String.valueOf(selectedValue));
      if (selectedScan) {
        setName(selectedScan.name || "");
        setDate(selectedScan.date || "");
        setNote(selectedScan.note || "");
        setScanChanged(true);
        sensorsFetchRequest(structureId, setSensors, setTotalItems, {scanFilter: selectedValue});
        planSensorsScanFetchRequest(structureId, selectedValue, selectedPlanId(), setPlanSensors);
      }
    } else {
      setDate("");
      setName("");
      setScanChanged(false);
      sensorsFetchRequest(structureId, setSensors, setTotalItems, {});
      planSensorsFetchRequest(structureId, setPlanSensors, selectedPlanId())
      setNote(structureDetails().note)
    }
  };

  return (
    <div class="flex flex-col gap-y-2.5">
      <p class="title">Viaduc de Sylans</p>
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
            <button class="bg-white rounded-[50px] h-[40px] w-[40px] flex items-center justify-center">
              <Pencil size={20}/>
            </button>
            <button class="bg-[#F133271A] rounded-[50px] h-[40px] w-[40px] flex items-center justify-center">
              <Trash2 color="red" size={20}/>
            </button>
          </Show>
          <button class="bg-white rounded-[50px] h-[40px] w-[40px] flex items-center justify-center">
            <Download size={20}/>
          </button>
          <button class="bg-black rounded-[50px] h-[40px] w-[40px] flex items-center justify-center">
            <Plus color="white" size={20}/>
          </button>
        </div>
      </div>

      <div class="flex justify-between px-[10px]">
        <p class="normal">{name()}</p>
        <p class="normal opacity-50">{date()}</p>
      </div>
    </div>
  );
}

export default StructureDetailHead

