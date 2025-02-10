import { ChevronDown, Dot, ChevronRight, Pencil, FolderSync } from 'lucide-solid';


function StructureDetailSection() {

    return (
        <div class="flex flex-col gap-y-[5px]">
            <div class="flex flex-col gap-y-[5px]">
                <div class="bg-white px-[8px] py-[9px] flex gap-x-[10px] rounded-[10px]">
                    <div class="w-4 h-4">
                        <ChevronDown />
                    </div>
                    <p class="prose font-poppins poppins font-semibold">Section OA</p>
                </div>
                <div class="px-[8px] py-[9px] rounded-[10px] ml-4 flex gap-x-[10px] justify-between">
                    <div class="flex gap-x-[10px]">
                        <div class="w-4 h-4">
                            <Dot />
                        </div>
                        <p class="prose font-poppins poppins font-semibold">Plan 01</p>
                    </div>
                </div>
                <div class="px-[8px] py-[9px] rounded-[10px] ml-4 flex gap-x-[10px] justify-between">
                    <div class="flex gap-x-[10px]">
                        <div class="w-4 h-4">
                            <Dot />
                        </div>
                        <p class="prose font-poppins poppins font-semibold">Plan 02</p>
                    </div>
                </div>
                <div class="px-[8px] py-[9px] rounded-[10px] ml-4 flex gap-x-[10px] bg-[#F2F2F4] justify-between">
                    <div class="flex gap-x-[10px]">
                        <div class="w-4 h-4">
                            <Dot />
                        </div>
                        <p class="prose font-poppins poppins font-semibold">Plan 03</p>
                    </div>
                    <div class="w-5 h-5">
                        <Pencil size={20}/>
                    </div>
                </div>
                <div class="px-[8px] py-[9px] rounded-[10px] ml-4 flex gap-x-[10px] justify-between">
                    <div class="flex gap-x-[10px]">
                        <div class="w-4 h-4">
                            <Dot />
                        </div>
                        <p class="prose font-poppins poppins font-semibold">Plan 04</p>
                    </div>
                    <div class="w-5 h-5">
                        <FolderSync size={20} />
                    </div>
                </div>
            </div>
            <div class="flex flex-col gap-y-[5px]">
                <div class="bg-white px-[8px] py-[9px] flex gap-x-[10px] rounded-[10px]">
                    <div class="w-4 h-4">
                        <ChevronRight />
                    </div>
                    <p class="prose font-poppins poppins font-semibold">Section OB</p>
                </div>
            </div>
        </div>
    );
}

export default StructureDetailSection

