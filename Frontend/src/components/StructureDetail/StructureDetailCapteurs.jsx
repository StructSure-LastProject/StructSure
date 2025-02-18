import { ArrowDownNarrowWide, Filter, Plus, Trash2 } from 'lucide-solid';
import { createEffect, createSignal } from 'solid-js';

/**
 * Show the sensors part of the structure detail page
 * @returns the component for the sensors part
 */
function StructureDetailCapteurs({sensors}) {
    const [error, setError] = createSignal("");

    /**
     * Returns the sensor color (div) corresponding for its state
     * @param {Object} sensor the sensor
     * @returns the div with the corresponding color
     */
    const getSensorStatusColor = (sensor) => {
        let colorsClasses = "";
        switch(sensor.state) {
            case "OK":
                colorsClasses = "bg-[#25B61F] border-green-200";
                break;
            case "NOK":
                colorsClasses = "bg-[#F13327] border-red-200";
                break;
            case "UNKNOWN":
                colorsClasses = "bg-[#6A6A6A] border-grey-200";
                break;
            case "DEFECTIVE":
                colorsClasses = "bg-[#F19327] border-yellow-200";
                break;
            default:
                setError("L'etat du sensor inconnu");
                break;
        }
        return <div class={`w-[12px] h-[12px] rounded-[50px] border-2 ${colorsClasses}`}></div>;
    };


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
                        <div class="flex justify-between gap-x-[15px] rounded-[50px] px-[25px] py-[10px] bg-white items-center">
                            {getSensorStatusColor(sensor)}
                            <p class="prose font-poppins poppins text-base font-semibold w-[138px]">{sensor.name}</p>
                            <div class="w-5 h-5 rounded-[50px] flex justify-center items-center">
                                <Trash2 size={20} />
                            </div>
                        </div>
                    )}
                </For>
            </div>
        </div>
    );
}

export default StructureDetailCapteurs

