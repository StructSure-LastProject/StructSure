import { ChevronDown, SortAsc, SortDesc } from 'lucide-solid';
import { createSignal } from 'solid-js';

/**
 * Sort filter field
 * @returns The component for the sort filter field
 */
const SortFilterField = () => {
    const [sortOrder, setSortOrder] = createSignal(false);

    /**
     * Handle the sort button
     */
    const handleSortOrder = () => {
        setSortOrder(!sortOrder());
    }


    return (
        <div class="flex flex-col gap-[5px] w-[100%] min-w-[350px]">
            <p className="font-poppins HeadLineMedium text-[#181818] opacity-[75%]">Trier</p>
            <div class="flex">
                <select name="sort" id="sort" class="bg-[#F2F2F4] w-[100%] rounded-l-[10px] px-[16px] py-[8px] appearance-none">
                    <option value="Nom">Nom</option>
                </select>
                <div class="flex">
                    <div class="relative">
                        <div className="absolute right-4 top-1/2 transform -translate-y-1/2 pointer-events-none">
                            <ChevronDown size={20} strokeWidth={2.2} />
                        </div>
                    </div>
                    <button onClick={handleSortOrder} class="flex rounded-r-[10px] justify-center items-center bg-[#181818] w-[56px]">
                        {sortOrder() ? <SortAsc color="white" /> : <SortDesc color="white"/>}
                    </button>
                </div>
            </div>
        </div>
    );
}


const SensorFilter = () => {
  return (
    <div class="flex rounded-[20px] px-[20px] py-[15px] gap-[15px] bg-[#FFFFFF]">
        <SortFilterField />
    </div>
  )
}

export default SensorFilter