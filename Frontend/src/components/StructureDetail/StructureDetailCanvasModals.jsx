import { createSignal, Show } from "solid-js";
import { Check, ChevronDown, Trash2 } from 'lucide-solid';
import { openSensorPanelHandler } from './StructureDetailCapteurs';

/**
 * Popup of the canvas displayed when a point is clicked
 * @param {Function} top offset from the top to place the popup in the canvas
 * @param {Function} left offset from the left to place the popup in the canvas
 * @param {Function} sensor the data of the sensor
 * @param {Function} onClick action to run when the delete button is clicked
 * @returns the popup
 */
export function PointTooltip({ top, left, sensor, onClick }) {
    return (
        <div class="absolute z-10 w-fit rounded-r-[28px] rounded-bl-[25px] flex flex-col gap-5 bg-white p-[8px] shadow-[0_0_100px_0_rgba(151,151,167,0.5)]"
            style={`top: ${top()}px; left: ${left()}px`}>
            <div class="w-fit flex gap-[25px] items-center pl-[10px]">
                <button class="subtitle" onClick={() => openSensorPanelHandler(sensor)}>{sensor().name}</button>
                <button class="bg-[#F133271A] rounded-[50px] h-[40px] w-[40px] flex items-center justify-center" onClick={onClick}>
                    <Trash2 color="red" stroke-width="2.5" width="20px" height="20px"/>
                </button>
            </div>
        </div>
    );
};

/**
 * Popup of the canvas displayed to place a new point
 * @param {Function} top offset from the top to place the popup in the canvas
 * @param {Function} left offset from the left to place the popup in the canvas
 * @param {Function} onSubmit action to run when the submit button is clicked
 * @param {Function} inputValue getter of the search field
 * @param {Function} setInputValue setter of the search field
 * @param {Function} filteredOptions options to display in the search field
 * @param {Function} setSelectedSensor setter for when an option is clicked
 * @returns the popup
 */
export function PlacePoint({ top, left, onSubmit, inputValue, setInputValue, filteredOptions, setSelectedSensor }) {
    const [isOpen, setIsOpen] = createSignal(false);

    return (
        <>
            <div class="absolute z-20 border-4 border-black w-5 h-5 bg-white rounded-[50px]"
                style={{
                    top: `${top()-10}px`,
                    left: `${left()-10}px`,
                }}>
            </div>
            <div class="absolute z-10 w-[351px] rounded-tr-[20px] rounded-b-[20px] flex flex-col gap-5 bg-white px-5 py-[15px] shadow-[0_0_100px_0_rgba(151,151,167,0.5)]"
                style={`top: ${top()}px; left: ${left()}px`}>
                <div class="w-full flex justify-between items-center">
                    <h1 class="title">Nouveau point</h1>
                    <div class="flex gap-x-[10px]">
                        <button class="bg-lightgray rounded-[50px] h-[40px] w-[40px] flex items-center justify-center" onClick={onSubmit}>
                            <Check color="black" stroke-width="2.5" width="20px" height="20px"/>
                        </button>
                    </div>
                </div>
                <div class="flex flex-col gap-y-[5px]">
                    <p class="normal opacity-50">Capteur</p>

                    <div class="bg-lightgray px-[16px] py-[8px] rounded-[20px] flex justify-between items-center">
                        <input
                            type="text"
                            class="bg-transparent subtitle w-full"
                            value={inputValue()}
                            onInput={(e) => setInputValue(e.currentTarget.value)}
                            onFocus={() => setIsOpen(true)} // Open dropdown on focus
                        />
                        <button
                            class="rounded-[50px] h-[24px] w-[24px] flex items-center justify-center"
                            onClick={() => setIsOpen(!isOpen())}
                        >
                            <ChevronDown class={`transition-transform ${isOpen() ? "rotate-180" : ""}`} color="black" />
                        </button>
                    </div>
                    <Show when={isOpen() && filteredOptions().length > 0}>
                        <div class="rounded-[10px] py-[10px] px-[20px] flex flex-col gap-y-[10px]">
                            {filteredOptions().map((option, index) => (
                            <>
                                <p
                                    class="normal cursor-pointer"
                                    onClick={(event) => {
                                        event.stopImmediatePropagation();
                                        setInputValue(option.name);
                                        setSelectedSensor(option);
                                        setIsOpen(false);
                                    }}
                                >
                                {option.name}
                                </p>
                                <Show when={index < filteredOptions().length - 1}>
                                    <div class="w-full h-[1px] bg-lightgray"></div>
                                </Show>
                            </>
                            ))}
                        </div>
                    </Show>
                </div>
            </div>
        </>
    );
};