import { ChevronDown, SortAsc, SortDesc, MoveRight } from 'lucide-solid';
import { createEffect, createSignal } from 'solid-js';
import SensorFieldComponent from "../components/SensorPanel/SensorFieldComponent";
import { sensorsFetchRequest } from './StructureDetail/StructureDetailBody';
import { useNavigate, useSearchParams } from '@solidjs/router';

/**
 * Custom drop down menu
 * @param {String} dropDownTitle The drop down title
 * @param {Array} dropDownValues The array of values
 * @param {JSX} children The children component
 * @param {String} styles The tailwind css
 * @param {Function} setter The setter function to update the create signal value
 * @param {Function} getter The getter function to search for the choosed one
 * @param {String} searchParamName The parameter name
 * @returns The drop down component
 */
const CustomDropDown = ({dropDownTitle, dropDownValues , children, styles, setter, getter, searchParamName}) => {
    const [searchParams, setSearchParams] = useSearchParams();
    return (
        <div class="flex flex-col gap-[5px] w-[100%] min-w-[140px]">
            <p className="font-poppins HeadLineMedium text-[#181818] opacity-[75%]">{dropDownTitle}</p>
            <div class="flex">
                <select onChange={(e) => {
                    setter(e.target.value);     
                    setSearchParams({ [searchParamName]: e.target.value });
                }} name="sort" id="sort" class={`bg-[#F2F2F4] w-[100%] rounded-l-[10px] px-[16px] py-[8px] appearance-none ${styles}`}>
                    <For each={dropDownValues}>
                        {
                            item => <option value={item} {...(getter() === item ? { selected: true } : {})} >{item}</option>
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
 * @param {Function} orderByColumn The getter function for the order
 * @param {String} orderType The order type value
 * @param {Function} The setter function to update the order type value
 * @returns The component for the sort filter field
 */
const SortFilterField = ({dropDownValues, setOrderByColumn, orderType, setOrderType, orderByColumn}) => {
    const [searchParams, setSearchParams] = useSearchParams();

    /**
     * Handle the sort button
     */
    const handleOrderType = () => {
        const value = !orderType();
        setOrderType(value);
        setSearchParams({ orderType: value });
    }

    return (
        <CustomDropDown
            dropDownTitle={"Trier"}
            dropDownValues={dropDownValues}
            setter={setOrderByColumn}
            getter={orderByColumn} 
            searchParamName={"orderByColumn"}
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
                searchParamName="startDate"
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
                searchParamName="endDate"
            />
        </div>
    );
}

/**
 * The checkbox component
 * @param {String} description The description of the component
 * @param {Boolean} value The value filter of checkbox
 * @param {Function} setter The setter of the checkbox
 * @param {String} searchParamName The parameter name
 * @returns The check box component
 */
const CheckBoxComponent = ({description, value, setter, searchParamName}) => {
    const [searchParams, setSearchParams] = useSearchParams();
    
    /**
     * Handles the onclick
     */
    const handleOnClick = () => {
        const val = !value();
        setter(val);
        setSearchParams({ [searchParamName]: val });
    };

    return (
        <div class="flex gap-[10px] w-full items-center rounded-[10px] py-[8px] h-fit">
            <input 
                type="checkbox" 
                name="display_sensors_from_selected_image" 
                id="displat_sensors_image" 
                class="accent-[#181818]  min-w-[14px] min-h-[14px] rounded-[3px]"
                checked={value()}
                onClick={handleOnClick}
            />
            <button 
                onClick={handleOnClick} 
                class="font-poppins font-[400] text-[14px] leading-[21px] tracking-[0%] opacity-[75%] text-[#181818]"
            >
                {description}
            </button>
        </div>
    );
}

/**
 * Sensor filter the compoenent
 * @param {function} selectedScan The selected scan
 * @param {Number} structureId The structure id
 * @param {Function} setSensors The setter function for sensors
 * @param {Number} limit The limit
 * @param {Number} offset The offset
 * @param {Function} setTotalItems The setter function
 * @returns The component
 */
const SensorFilter = ({
        selectedScan, 
        structureId, 
        setSensors, 
        limit, 
        offset, 
        setTotalItems, 
        selectedPlanId, 
        orderByColumn, 
        setOrderByColumn,
        orderType,
        setOrderType,
        isCheckedPlanFilter,
        setIsCheckedPlanFilter,
        isCheckedArchivedFilter,
        setIsCheckedArchivedFilter,
        startDate,
        setStartDate,
        endDate,
        setEndDate,
        stateFilter,
        setStateFilter,
        SORT_VALUES,
        FILTER_VALUES,
        
    }) => {
    const navigate = useNavigate();

    /**
     * Create effect to update sensors when filter added
     */
    createEffect(() => {
        sensorsFetchRequest(structureId, setSensors, setTotalItems, navigate, {
            orderByColumn: orderByColumn() !== "Tout" ? SORT_VALUES[orderByColumn()] : "STATE",
            orderType: orderType() ? "ASC" : "DESC",
            limit: limit(),
            offset: offset(),
            ...(selectedScan() > -1 && {scanFilter: selectedScan()}),
            ...(stateFilter() !== "Tout" && {stateFilter: FILTER_VALUES[stateFilter()] }),
            ...(isCheckedArchivedFilter() ? {archivedFilter: isCheckedArchivedFilter()} : false),
            ...(isCheckedPlanFilter() && selectedPlanId() !== undefined && {planFilter: selectedPlanId()}),
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
                    orderByColumn={orderByColumn}
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
                    getter={stateFilter}
                    searchParamName={"stateFilter"}
                />
                <div class="flex flex-col w-full">
                    <CheckBoxComponent 
                        description={"Capteurs du plan sélectionné uniquement"}
                        value={isCheckedPlanFilter}
                        setter={setIsCheckedPlanFilter}
                        searchParamName={"isCheckedPlanFilter"}
                    />
                    <CheckBoxComponent 
                        description={"Capteurs archivés"}
                        value={isCheckedArchivedFilter}
                        setter={setIsCheckedArchivedFilter}
                        searchParamName={"isCheckedArchivedFilter"}
                    />
                </div>
            </div>
        </div>
    );
}

export default SensorFilter