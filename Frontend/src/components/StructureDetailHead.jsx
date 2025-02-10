import { Download, Plus } from 'lucide-solid';


function StructureDetailHead() {
    
    return (
        <div class="flex flex-col gap-y-2.5">
            <p class="prose font-poppins title">Viaduc de Sylans</p>
            <div class="flex gap-x-[10px]">
                <select class="px-4 py-2 w-full h-10 rounded-[20px] font-poppins font-1">
                    <option value="0">Scan 03</option>
                    <option value="1">Scan 04</option>
                </select>
                <button class="bg-white rounded-[50px] h-[40px] w-[40px] flex items-center justify-center">
                    <Download />
                </button>
                <button class="bg-black rounded-[50px] h-[40px] w-[40px] flex items-center justify-center">
                    <Plus color="white"/>
                </button>
            </div>

            <div class="flex justify-between px-[10px]">
                <p>Yannick Falaise</p>
                <p>10 novembre 2025</p>
            </div>
        </div>
    );
}

export default StructureDetailHead

