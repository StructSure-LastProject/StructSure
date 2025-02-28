import {createSignal, For} from "solid-js";
import {ChevronDown, X} from "lucide-solid";

const CustomDropDown = ({dropDownTitle, dropDownValues}) => {
  return (
    <div class="relative w-full md:w-1/3">
      <div class="text-sm text-gray-600 mb-1">Filtrer</div>
      <div class="relative">
        <select
          class="appearance-none w-full border border-gray-300 rounded py-2 px-3 focus:outline-none focus:ring-1 focus:ring-blue-500">
          <option>Tout</option>
          <option>Documents</option>
          <option>Images</option>
          <option>Vidéos</option>
        </select>
        <div class="pointer-events-none absolute inset-y-0 right-0 flex items-center px-2 text-gray-700">
          <ChevronDown size={16}/>
        </div>
      </div>
    </div>
  );
}

const StructuresFilter = () => {
  const [searchByName, setSearchByName] = createSignal("");
  const [orderByColumnName, setOrderByColumnName] = createSignal("");
  const [orderType, setOrderType] = createSignal("");

  return (
    <div class="bg-white flex flex-row justify-between w-full rounded-[20px]">
      <label class="flex flex-col gap-[5px]">
        <p class="normal opacity-75">Rechercher</p>
        <div class="flex flex-row gap-[5px] bg-lightgray normal items-center rounded-[10px]">
          <input
            type="text"
            maxLength={64}
            placeholder="Viaduc"
            class="w-full px-[16px] py-[8px] bg-lightgray normal text-black"
          />
          <X size={12} />
        </div>
      </label>
      <CustomDropDown
        dropDownTitle={"Filtrer"}
      />
      <CustomDropDown
        dropDownTitle={"Trier"}
      />
    </div>
  );
}
export default StructuresFilter;