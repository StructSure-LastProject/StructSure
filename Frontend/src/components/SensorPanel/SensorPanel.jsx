import { Pencil, X, Check } from 'lucide-solid';
import getSensorStatusColor from "../SensorStatusColorGen";
import { createResource, createSignal, Show } from 'solid-js';
import SensorFieldComponent from './SensorFieldComponent';
import { loginValidator } from '../../hooks/vaildateUserAccountForm';
import Canvas from './Canvas';
import useFetch from '../../hooks/useFetch';
import {sensorsFetchRequest} from "../StructureDetail/StructureDetailBody"
import plan_not_found from '/src/assets/plan_not_found.png';

/**
 * The panel header
 * @param {String} sensorState The sensor state
 * @param {String} sensorName The sensor name
 * @param {Function} closeSensorPanel The function to close the panel
 * @param {Boolean} editMode Thde mode
 * @param {Function} setEditMode The function to set the mode
 * @param {Function} handleSubmit The submit function
 * @param {Function} setSensorName The setter function for the sensor name
 * @returns The panel header component
 */
const PanelHeader = ({sensorState, sensorName, closeSensorPanel, editMode, setEditMode, handleSubmit, setSensorName, validationError}) => {

  /**
   * Change edit mode and vice versa
   */
  const changeMode = () => {
    if (editMode() && !handleSubmit()) {
      setEditMode(!editMode());
    }
    if (editMode() && validationError() !== "") {
      return;
    }
    setEditMode(!editMode());
  }

  return (
    <div class="flex justify-between rounded-[20px]">
      <div class="flex flex-wrap justify-center items-center">
        <div class="w-[39px] h-[39px] flex items-center justify-center">
          <div class={`w-[20px] h-[20px] rounded-[50px] border-[3px] ${getSensorStatusColor(sensorState)}`}></div>
        </div>
        <input class="font-poppins font-[600] max-w-[150px] lg:max-w-[714px] text-[25px] leading-[37.5px] tracking-[0%] text-[#181818]"
          type="text"
          value={sensorName()}
          onChange={(e) => setSensorName(e.target.value)}
          minLength="1"
          maxLength="32"
          required
          disabled={!editMode()}
        />
      </div>
      <div class="flex flex-wrap gap-[10px]">
        <Show when={editMode()} fallback={
          <button onClick={changeMode} class="flex justify-center items-center w-[40px] h-[40px] sm:w-[40px] sm:h-[40px] rounded-[50px] bg-[#F2F2F4]">
            <Pencil color="#181818" size={20} width={16.67} top={1.67} left={1.67} strokeWidth={2} />
          </button>
        }>
          <button onClick={changeMode} class="bg-black rounded-[50px] h-[40px] w-[40px] flex items-center justify-center">
              <Check class="w-5 h-5 text-white" />
          </button>
        </Show>
        <button onClick={closeSensorPanel} class="flex justify-center items-center w-[40px] h-[40px] sm:w-[40px] sm:h-[40px] rounded-[50px] bg-[#F2F2F4]">
          <X color="#181818" size={20} width={16.67} top={1.67} left={1.67} strokeWidth={2} />
        </button>
      </div>
    </div>
  );
}

/**
 * The sensor plan 
 * @returns The component contains a canva with sensor on a image
 */
const SensorPlan = ({sensorMap, selectedPlanId, sensorDetails, structureId}) => {  
  const planId = selectedPlanId() === null ? 1 : selectedPlanId();
  const { fetchImage, image, loading } = useFetch();
  const token = localStorage.getItem("token");
  const endpoint = `/api/structures/plans/${structureId}/${sensorDetails.controlChip}/${sensorDetails.measureChip}/image`;

  const requestData = {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
      "Authorization": `Bearer ${token}`
    },
  };
  
  
  createResource(async () => {
    await fetchImage(endpoint, requestData);    
  })
  
  return (
    <div class="lg:flex lg:flex-col lg:gap-[10px]">
      <h1 class="font-poppins font-[600] text-[16px] leading-[24px] tracking-[0%] text-[#181818] pl-1">OA/Zone</h1>
      <Show when={!loading() && image() !== null} fallback={
        <img
          class={"w-full h-[156px] lg:min-w-[549px] lg:min-h-[299px] object-cover"} 
          src={plan_not_found} 
          alt="plan not found"
        />
      }>
        <Canvas
          styles={"w-full h-[156px] lg:min-w-[549px] lg:min-h-[299px]"} 
          plan={image()} 
          interactiveMode={false} 
          planSensors={Array.of(sensorDetails)}
        />
      </Show>
    </div>
  );
}

/**
 * The sensor comment section
 * @param {String} comment The comment200is required or not
 * @returns The component contains the comment of the sensor
 */
const SensorCommentSection = ({
  note,
  setNote, 
  editMode,
  minLength, 
  maxLength,
  isRequired 
}) => {
  return (
    <div class="flex flex-col gap-[5px] lg:gap-[10px] w-full">
      <p class="opacity-[75%] font-poppins HeadLineMedium text-[#181818]">Note</p>
      <textarea required={isRequired} minLength={minLength} maxLength={maxLength} disabled={!editMode()} onChange={(e) => setNote(e.target.value)} class="rounded-[18px] w-full px-[16px] py-[8px] flex gap-[10px] bg-[#F2F2F4] font-poppins font-[400] text-[14px] leading-[21px] text-[#181818]"
        value={note()}
      >
      </textarea>
    </div>
  );
}


/**
 * Shows the sensor panel with extra details of the clicked sensor
 * @param {String} structureId The structure id
 * @param {Array} sensors The sensors array
 * @param {Function} setSensors The set sonsors function
 * @param {String} selectedPlanId The selected plan id
 * @param {Object} sensorDetails contains all the information about the clickded sensor 
 * @param {Function} closeSensorPanel Function that close the sensor panel
 * @returns The sensor panel component
 */
const SensorPanel = ({structureId, sensors, setSensors, selectedPlanId, sensorDetails, closeSensorPanel, setTotalItems}) => {

  const [sensorName, setSensorName] = createSignal(sensorDetails.name);
  const [installationDate, setInstallationDate] = createSignal(sensorDetails.installationDate === null ? "" : sensorDetails.installationDate.split('T')[0]);
  const [note, setNote] = createSignal(sensorDetails?.note);
  const [editMode, setEditMode] = createSignal(false);
  

  const [validationError, setvalidationError] = createSignal("");
  
  /**
   * Handle the submit
   * @returns true or false
   */
  const handleSubmit = async () => {
    if (sensorName() === "") {
      setvalidationError("Le nom du capteur est obligatoire");
      return false;
    }
    if(!loginValidator(sensorName())){
      setvalidationError("Le nom doit contenir uniquement des lettres, des chiffres, des underscores et des @");
      return false;
    }
    
    if (installationDate() === "") {
      setvalidationError("La date d'installation du capteur est obligatoire");
      return false;
    }

    const { fetchData, error, statusCode } = useFetch();
    const token = localStorage.getItem("token");
    
    const requestBody = {
      controlChip: sensorDetails.controlChip,
      measureChip: sensorDetails.measureChip,
      name: sensorName(),
      installationDate: installationDate(),
      note: note() === null ? "" : note()
    }

    const requestData = {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${token}`
      },
      body: JSON.stringify(requestBody)
    };

    await fetchData("/api/sensors/edit", requestData);

    if (statusCode() === 200) {
      setvalidationError("");
      sensorsFetchRequest(structureId, setSensors, setTotalItems);
      closeSensorPanel();
      return true;
    }    
    setvalidationError(error().errorData)
    return false;
  }


  const sensorMap = new Map();
  sensors().forEach(sensor => {
    sensorMap.set(sensor.controlChip, sensor);
  });
    
  
  return (
    <div class="min-h-[100vh] bg-[#F2F2F403] backdrop-blur-[25px] z-[100] flex items-end justify-center align-middle w-[100vw] fixed top-0 left-0">
      <div class="max-w-[963px] max-h-[calc(100vh-7rem)] justify-center flex flex-col gap-[25px] w-[100%] rounded-t-[35px] p-[25px] lg:p-[50px] bg-[#FFFFFF] shadow-[0px_4px_100px_0px_rgba(151,151,167,0.50)] mx-auto">
        <PanelHeader 
          sensorName={sensorName} 
          sensorState={sensorDetails.state} 
          closeSensorPanel={closeSensorPanel} 
          editMode={editMode} 
          setEditMode={setEditMode}
          handleSubmit={handleSubmit}
          setSensorName={setSensorName}
          validationError={validationError}
        />
        <Show when={validationError() !== ""}>
          <p class="text-[#F13327] font-poppins HeadLineMedium">{validationError()}</p>
        </Show>
        <div class="overflow-auto overflow-x-hidden flex flex-col gap-[25px] rounded-[18px]">
          <div class="lg:flex lg:flex-row lg:gap-[25px] flex flex-col gap-[25px]">
            <SensorPlan
              selectedPlanId={selectedPlanId}
              sensorDetails={sensorDetails}
              sensorMap={sensorMap}
              structureId={structureId}
            />
            <SensorCommentSection 
              note={note} 
              setNote={setNote} 
              editMode={editMode}
              minLength={"0"}
              maxLength={"1000"}
              isRequired={true} 
            />
          </div>
          <div class="lg:flex lg:flex-row lg:gap-[25px] lg:min-w-[863px] lg:min-h-[63px] flex flex-col gap-[25px]">
            <SensorFieldComponent 
              title={"Tag témoin"} 
              value={sensorDetails.controlChip} 
            />
            <SensorFieldComponent 
              title={"Tag mesure"} 
              value={sensorDetails.measureChip}
            />
            <SensorFieldComponent 
              title={"Date d’installation"} 
              value={installationDate}
              editMode={editMode} 
              type={"date"}
              isRequired={true} 
              setter={setInstallationDate}
            />
          </div>
        </div>
      </div>
    </div>
  )

}

export default SensorPanel

