import { ChevronDown, SortAsc, SortDesc, MoveRight } from 'lucide-solid';
import { createSignal } from 'solid-js';
import SensorFieldComponent from "../components/SensorPanel/SensorFieldComponent";

/**
 * Custom drop down menu
 * @param {String} dropDownTitle The drop down title
 * @param {Array} dropDownValues The array of values
 * @param {JSX} children The children component
 * @param {String} styles The tailwind css
 * @returns The drop down component
 */
const CustomDropDown = ({dropDownTitle, dropDownValues , children, styles}) => {
    return (
        <div class="flex flex-col gap-[5px] w-[100%] min-w-[140px]">
            <p className="font-poppins HeadLineMedium text-[#181818] opacity-[75%]">{dropDownTitle}</p>
            <div class="flex">
                <select name="sort" id="sort" class={`bg-[#F2F2F4] w-[100%] rounded-l-[10px] px-[16px] py-[8px] appearance-none ${styles}`}>
                    <For each={dropDownValues}>
                        {
                            item => <option value={item}>{item}</option>
                        }
                    </For>
                </select>
                <div class="flex">
                    <div class="relative">
                        <div className="absolute right-4 top-1/2 transform -translate-y-1/2 pointer-events-none">
                            <ChevronDown size={20} strokeWidth={2.2} />
                        </div>
                    </div>
                    {children}
                </div>
            </div>
        </div>
    );
}

/**
 * Sort filter field
 * @param {Array} dropDownValues The array of drop down values
 * @returns The component for the sort filter field
 */
const SortFilterField = ({dropDownValues}) => {
    const [sortOrder, setSortOrder] = createSignal(false);

    /**
     * Handle the sort button
     */
    const handleSortOrder = () => {
        setSortOrder(!sortOrder());
    }


    return (
        <CustomDropDown
            dropDownTitle={"Trier"}
            dropDownValues={dropDownValues}  
            children={
                <button onClick={handleSortOrder} class="flex rounded-r-[10px] justify-center items-center bg-[#181818] min-w-[56px]">
                    {sortOrder() ? <SortAsc color="white" /> : <SortDesc color="white"/>}
                </button>
            }
        />
            
        
    );
}

/**
 * Date filter field component
 * @param {String} startDate The start date 
 * @param {Function} setStartDate The setter function for the start date
 * @param {String} endDate The end date 
 * @param {Function} setEndDate The setter function for the end date
 * @returns The date filter component
 */
const DateFilterField = ({startDate, setStartDate, endDate, setEndDate}) => {
    return (
        <div class="flex lg:flex-row lg:justify-evenly lg:items-center justify-between gap-[15px] lg:gap-[20px] min-w-[140px] w-full">
            <SensorFieldComponent 
                title={"Installation du"} 
                value={startDate}
                editMode={() => true} 
                type={"date"}
                isRequired={true} 
                setter={setStartDate}
                styles={"bg-lightgray rounded-[10px] py-[8px] px-[16px] flex gap-[10px] lg:max-w-[271px] normal"}
                parentStyles={"flex flex-col gap-[5px] min-w-[140px] w-full"}
            />
            <SensorFieldComponent 
                title={"Au"} 
                value={endDate}
                editMode={() => true} 
                type={"date"}
                isRequired={true} 
                setter={setEndDate}
                styles={"bg-lightgray rounded-[10px] py-[8px] px-[16px] flex gap-[10px] lg:max-w-[271px] normal"}
                parentStyles={"flex flex-col gap-[5px] min-w-[140px] w-full"}
            />
        </div>
    );
}

/**
 * The checkbox component
 * @param {String} description The description of the component
 * @param {Boolean} isChecked The description of the component
 * @param {Function} setIsChecked The setter of the checkbox component
 * @returns The check box component
 */
const CheckBoxComponent = ({description, isChecked, setIsChecked}) => {
    return (
        <div class="flex gap-[10px] w-full items-center rounded-[10px] py-[8px] h-fit">
            <input 
                type="checkbox" 
                name="display_sensors_from_selected_image" 
                id="displat_sensors_image" 
                class="accent-[#181818]  min-w-[14px] min-h-[14px] rounded-[3px]"
                checked={isChecked()}
            />
            <button 
                onClick={() => setIsChecked(!isChecked())} 
                class="font-poppins font-[400] text-[14px] leading-[21px] tracking-[0%] opacity-[75%] text-[#181818]"
            >
                {description}
            </button>
        </div>
    );
}

/**
 * Sensor filter the compoenent
 * @returns The component
 */
const SensorFilter = () => {
    const SORT_VALUES = ["Nom"];
    const FILTER_VALUES = ["Tout"];

    const [isChecked, setIsChecked] = createSignal(false);
    const [startDate, setStartDate] = createSignal("");
    const [endDate, setEndDate] = createSignal("");

    return (
        <div class="flex flex-col rounded-[20px] px-[20px] py-[15px] gap-[20px] bg-[#FFFFFF]">
            <div class="lg:flex lg:flex-row lg:gap-[20px] flex flex-col gap-[20px]">
                <SortFilterField 
                    dropDownValues={SORT_VALUES}
                />
                <DateFilterField 
                    startDate={startDate}
                    setStartDate={setStartDate}
                    endDate={endDate}
                    setEndDate={setEndDate}
                />
            </div>
            <div class="lg:flex lg:flex-row lg:gap-[20px] flex flex-col gap-[10px] items-end">
                <CustomDropDown 
                    dropDownTitle={"Filtrer"}
                    dropDownValues={FILTER_VALUES}
                    styles={"rounded-[10px]"}
                />
                <CheckBoxComponent 
                    description={"Capteurs du plan sélectionné uniquement"}
                    isChecked={isChecked}
                    setIsChecked={setIsChecked}
                />
            </div>
        </div>
    );
}

export default SensorFilter