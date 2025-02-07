import logo from '/src/assets/logo.svg';
import check from '/src/assets/check.svg';
import { For, createResource, createSignal } from "solid-js";
import { useNavigate } from '@solidjs/router';


function StructureDetailNote() {
    
    return (
        
        <div class="w-[30%] bg-white rounded-[20px] flex flex-col gay-y-[15px] px-[15px] py-[20px]">
            <p class="prose font-poppins title">Note</p>
            <div class="rounded-[10px] px-[16px] py-[8px] bg-gray-100">
                <p class="font-poppins normal font-normal">Favoriser des allers-retours du nord vers le sud pour pouvoir scanner tous les capteurs.</p>
            </div>
        </div>
    );
}

export default StructureDetailNote

