import { Download, Plus } from 'lucide-solid';

/**
 * Show the head part of the structure detail
 * @returns the component for the head part
 */
function StructureDetailHead(props) {
    
    return (
        <div class="flex flex-col gap-y-2.5">
            <p class="title">Viaduc de Sylans</p>
            <div class="flex gap-x-[10px]">
                <select class="px-4 py-2 w-full h-10 rounded-[20px] subtitle bg-white">
                    <option value="-1">Aucun Scan Sélectionné</option>
                    <For each={props.scans}>
                        {(scan) => (
                            <option value={scan.id}>{ scan.name }</option>
                        )}
                    </For>
                </select>
                <button class="bg-white rounded-[50px] h-[40px] w-[40px] flex items-center justify-center">
                    <Download />
                </button>
                <button class="bg-black rounded-[50px] h-[40px] w-[40px] flex items-center justify-center">
                    <Plus color="white"/>
                </button>
            </div>

            <div class="flex justify-between px-[10px]">
                <p class="normal">Yannick Falaise</p>
                <p class="normal opacity-50">10 novembre 2025</p>
            </div>
        </div>
    );
}

export default StructureDetailHead

