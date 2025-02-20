import { ArrowDownNarrowWide, Filter, Plus, Trash2 } from 'lucide-solid';
import { createEffect, createSignal } from 'solid-js';
import SensorPanel from '../SensorPanel/SensorPanel';
import getSensorStatusColor from "../SensorStatusColorGen"

/**
 * Show the sensors part of the structure detail page
 * @returns the component for the sensors part
 */
function StructureDetailCapteurs({sensors}) {
    const [openSensorPanel, setOpenSensorPanel] = createSignal(false);
    const [clickedSensor, setClickedSensor] = createSignal({});


    /**
     * Open the sensor panel
     * @param {Object} sensor Sensor object that contains all the details about the clicked sensor
     */
    const openSensorPanelHandler = (sensor) => {
        setClickedSensor(sensor);
        setOpenSensorPanel(true);
        document.body.style.overflow = 'hidden';
    }

    /**
     * Close the sensor panel
     */
    const closeSensorPanelHandler = () => {
        setClickedSensor({});
        setOpenSensorPanel(false);
        document.body.style.overflow = "auto";
    }


    return (
        <div class="w-full flex flex-col gap-y-[15px]">
            <div class="flex justify-between">
                <p class="prose font-poppins title">Capteurs</p>
                <div class="flex justify-between gap-x-[10px]">
                    <div class="w-10 h-10 rounded-[50px] bg-white flex justify-center items-center">
                        <ArrowDownNarrowWide size={20}/>
                    </div>
                    <div class="w-10 h-10 rounded-[50px] bg-white flex justify-center items-center">
                        <Filter size={20}/>
                    </div>
                    <div class="w-10 h-10 rounded-[50px] bg-black flex justify-center items-center">
                        <Plus size={20} color='white'/>
                    </div>
                </div>
            </div>
            <div class="flex flex-col lg:grid lg:grid-cols-3 rounded-[20px] gap-4">
                <For each={sensors()}>
                    {(sensor) => (
                        <button onClick={() => openSensorPanelHandler(sensor)} class="cursor-pointer flex justify-between gap-x-[15px] rounded-[50px] px-[25px] py-[10px] bg-white items-center">
                            <div class={`w-[12px] h-[12px] rounded-[50px] border-2 ${getSensorStatusColor(sensor.state)}`}></div>
                            <p class="prose font-poppins poppins text-base font-semibold w-[138px]">{sensor.name}</p>
                            <div class="w-5 h-5 rounded-[50px] flex justify-center items-center">
                                <Trash2 size={20} />
                            </div>
                        </button>
                    )}
                </For>
            </div>
            {
                openSensorPanel() && (
                    <SensorPanel sensorDetails={clickedSensor()} closeSensorPanel={closeSensorPanelHandler} />
                )
            }
        </div>
    );
}

export default StructureDetailCapteurs

