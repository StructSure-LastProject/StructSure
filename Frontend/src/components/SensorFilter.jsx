import { ChevronDown, SortAsc, SortDesc, MoveRight } from 'lucide-solid';
import { createEffect, createSignal } from 'solid-js';
import SensorFieldComponent from "../components/SensorPanel/SensorFieldComponent";
import { sensorsFetchRequest } from './StructureDetail/StructureDetailBody';
import { useNavigate } from '@solidjs/router';

/**
 * Custom drop down menu
 * @param {String} dropDownTitle The drop down title
 * @param {Array} dropDownValues The array of values
 * @param {JSX} children The children component
 * @param {String} styles The tailwind css
 * @param {Function} setter The setter function to update the create signal value
 * @returns The drop down component
 */
const CustomDropDown = ({dropDownTitle, dropDownValues , children, styles, setter}) => {
    return (
        <div class="flex flex-col gap-[5px] w-[100%] min-w-[140px]">
            <p className="font-poppins HeadLineMedium text-[#181818] opacity-[75%]">{dropDownTitle}</p>
            <div class="flex">
                <select onChange={(e) => setter(e.target.value)} name="sort" id="sort" class={`bg-[#F2F2F4] w-[100%] rounded-l-[10px] px-[16px] py-[8px] appearance-none ${styles}`}>
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
 * @param {Function} setOrderByColumn The setter function to update the order by column value
 * @param {String} orderType The order type value
 * @param {Function} The setter function to update the order type value
 * @returns The component for the sort filter field
 */
const SortFilterField = ({dropDownValues, setOrderByColumn, orderType, setOrderType}) => {

    /**
     * Handle the sort button
     */
    const handleOrderType = () => {
        setOrderType(!orderType());
    }

    return (
        <CustomDropDown
            dropDownTitle={"Trier"}
            dropDownValues={dropDownValues}
            setter={setOrderByColumn}  
            children={
                <button onClick={handleOrderType} class="flex rounded-r-[10px] justify-center items-center bg-[#181818] min-w-[56px]">
                    {orderType() ? <SortAsc color="white" /> : <SortDesc color="white"/>}
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
 * @param {Boolean} value The value filter of checkbox
 * @param {Function} setter The setter of the checkbox
 * @returns The check box component
 */
const CheckBoxComponent = ({description, value, setter}) => {
    return (
        <div class="flex gap-[10px] w-full items-center rounded-[10px] py-[8px] h-fit">
            <input 
                type="checkbox" 
                name="display_sensors_from_selected_image" 
                id="displat_sensors_image" 
                class="accent-[#181818]  min-w-[14px] min-h-[14px] rounded-[3px]"
                checked={value()}
            />
            <button 
                onClick={() => setter(!value())} 
                class="font-poppins font-[400] text-[14px] leading-[21px] tracking-[0%] opacity-[75%] text-[#181818]"
            >
                {description}
            </button>
        </div>
    );
}

/**
 * Sensor filter the compoenent
 * @param {Number} structureId The structure id
 * @param {Function} setSensors The setter function for sensors
 * @param {Number} limit The limit
 * @param {Number} offset The offset
 * @param {Function} setTotalItems The setter function
 * @returns The component
 */
const SensorFilter = ({structureId, setSensors, limit, offset, setTotalItems}) => {
    const navigate = useNavigate();
    const SORT_VALUES = {
        "Tout" : "Tout", "Nom": "NAME", "Etat": "STATE", "Date d'installation": "INSTALLATION_DATE"
    };
    const FILTER_VALUES = {"Tout" : "Tout", "OK" : "OK", "NOK" : "NOK", "Défaillant" : "DEFECTIVE", "Non détecté" : "UNKNOWN"};

    
    const [orderByColumn, setOrderByColumn] = createSignal(SORT_VALUES.Tout);
    const [orderType, setOrderType] = createSignal(true);
    const [isCheckedPlanFilter, setIsCheckedPlanFilter] = createSignal(false);
    const [isCheckedArchivedFilter, setIsCheckedArchivedFilter] = createSignal(false);
    const [startDate, setStartDate] = createSignal("");
    const [endDate, setEndDate] = createSignal("");
    const [statefilter, setStateFilter] = createSignal(FILTER_VALUES.Tout);

    /**
     * Create effect to update sensors when filter added
     */
    createEffect(() => {
        sensorsFetchRequest(structureId, setSensors, setTotalItems, navigate, {
            orderByColumn: orderByColumn() !== "Tout" ? SORT_VALUES[orderByColumn()] : "STATE",
            orderType: orderType() ? "ASC" : "DESC",
            limit: limit(),
            offset: offset(),
            ...(statefilter() !== "Tout" && {stateFilter: FILTER_VALUES[statefilter()] }),
            ...(isCheckedArchivedFilter() ? {archivedFilter: isCheckedArchivedFilter()} : false),
            /*...(filters?.planFilter && {planFilter: filters.planFilter}),*/
            ...(startDate() !== "" && {minInstallationDate: startDate()}),
            ...(endDate() !== "" && {maxInstallationDate: endDate()})
        })
        
    });


    return (
        <div class="flex flex-col rounded-[20px] px-[20px] py-[15px] gap-[20px] bg-[#FFFFFF]">
            <div class="lg:flex lg:flex-row lg:gap-[20px] flex flex-col gap-[20px]">
                <SortFilterField 
                    dropDownValues={Object.keys(SORT_VALUES)}
                    setOrderByColumn={setOrderByColumn}
                    orderType={orderType}
                    setOrderType={setOrderType}
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
                    dropDownValues={Object.keys(FILTER_VALUES)}
                    styles={"rounded-[10px]"}
                    setter={setStateFilter}
                />
                <div class="flex flex-col w-full">
                    <CheckBoxComponent 
                        description={"Capteurs du plan sélectionné uniquement"}
                        value={isCheckedPlanFilter}
                        setter={setIsCheckedPlanFilter}
                    />
                    <CheckBoxComponent 
                        description={"Capteurs archivés"}
                        value={isCheckedArchivedFilter}
                        setter={setIsCheckedArchivedFilter}
                    />
                </div>
            </div>
        </div>
    );
}

export default SensorFilter