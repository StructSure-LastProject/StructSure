import {ChevronDown, X, ArrowDownNarrowWide, ArrowUpNarrowWide} from "lucide-solid";

/**
 * Custom DropDown for filter section of structure
 * @param label The label of the dropdown
 * @param options The default options
 * @param value The value selected
 * @param onChange The onChange function
 * @returns The component of filter custom dropdown
 */
const CustomDropDown = ({label, options, value, onChange}) => {
  return (
    <div class="w-full">
      <label class="flex flex-col gap-[5px]">
        <p class="normal opacity-75">{label}</p>
        <div class="relative">
          <select
            value={value}
            onChange={(e) => onChange(e.target.value)}
            class="appearance-none w-full rounded-[10px] py-[10px] px-3 bg-lightgray h-[42px]"
          >
            {options.map((option) => (
              <option value={option.value}>{option.label}</option>
            ))}
          </select>
          <div class="pointer-events-none absolute inset-y-0 right-0 flex items-center px-2 text-gray-700">
            <ChevronDown size={24}/>
          </div>
        </div>
      </label>
    </div>
  );
}

/**
 * Custom DropDown for filter section of structure with order button
 * @param label The label of the dropdown
 * @param options The default options
 * @param value The value selected
 * @param onChange The onChange function
 * @param orderType The orderType selected value
 * @param setOrderType The setOrderType function
 * @returns The component of filter custom dropdown with order button
 */
const SortDropDown = ({label, options, value, onChange, orderType, setOrderType}) => {
  /**
   * Change the order type filter
   */
  const toggleOrder = () => {
    setOrderType(orderType() === "ASC" ? "DESC" : "ASC");
  };

  return (
    <div class="w-full">
      <label class="flex flex-col gap-[5px]">
        <p class="normal opacity-75">{label}</p>
        <div class="flex flex-row">
          <div class="relative flex-grow">
            <select
              value={value}
              onChange={(e) => onChange(e.target.value)}
              class="appearance-none w-full rounded-l-[10px] py-[10px] px-3 bg-lightgray h-[42px]"
            >
              {options.map((option) => (
                <option value={option.value}>{option.label}</option>
              ))}
            </select>
            <div class="pointer-events-none absolute inset-y-0 right-0 flex items-center px-2 text-gray-700">
              <ChevronDown size={24}/>
            </div>
          </div>
          <button
            onClick={toggleOrder}
            class="bg-black text-white py-[10px] px-3 rounded-r-[10px] h-[42px]"
          >
            {orderType() === "DESC" ? <ArrowDownNarrowWide size={20} /> : <ArrowUpNarrowWide size={20} />}
          </button>
        </div>
      </label>
    </div>
  );
}

/**
 * The structures filter section component
 * @param props The props of the component
 * @returns The structures filter section component
 */
const StructuresFilter = (props) => {
  /**
   * Clear the search input
   */
  const clearSearch = () => {
    props.setSearchByName("");
  };

  const filterOptions = [
    { value: "", label: "Tout" },
    { value: "NOK", label: "NOK" },
    { value: "DEFECTIVE", label: "Défaillant" },
    { value: "OK", label: "OK" },
    { value: "UNKNOWN", label: "Non scanné" },
    { value: "ARCHIVED", label: "Archivé" }
  ];

  const filterOptionsOperator = [
    { value: "", label: "Tout" },
    { value: "OK", label: "OK" },
    { value: "NOK", label: "NOK" },
    { value: "DEFECTIVE", label: "Défaillant" },
    { value: "UNKNOWN", label: "Non scanné" }
  ];

  const sortOptions = [
    { value: "STATE", label: "État" },
    { value: "NAME", label: "Nom" },
    { value: "NUMBER_OF_SENSORS", label: "Capteurs" }
  ];

  return (
    <div class="bg-white flex flex-col lg:flex-row justify-between w-full rounded-[20px] px-[20px] py-[15px] gap-4">
      <div class="relative w-full">
        <label class="flex flex-col gap-[5px]">
          <p class="normal opacity-75">Rechercher</p>
          <div class="flex flex-row items-center bg-lightgray rounded-[10px] relative">
            <input
              type="text"
              maxLength={64}
              placeholder="Viaduc"
              value={props.searchByName()}
              onInput={(e) => props.setSearchByName(e.target.value)}
              class="w-full px-[16px] py-[10px] bg-lightgray text-black rounded-[10px] h-[42px]"
            />
            <button onClick={clearSearch} class="absolute right-3 cursor-pointer">
              <X size={24}/>
            </button>
          </div>
        </label>
      </div>

      <CustomDropDown
        label="Filtrer"
        options={localStorage.getItem("role").includes("OPERATEUR") ? filterOptionsOperator : filterOptions}
        value={props.filterValue()}
        onChange={props.setFilterValue}
      />

      <SortDropDown
        label="Trier"
        options={sortOptions}
        value={props.orderByColumnName()}
        onChange={props.setOrderByColumnName}
        orderType={props.orderType}
        setOrderType={props.setOrderType}
      />
    </div>
  );
}

export default StructuresFilter;