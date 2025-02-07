import logo from '/src/assets/logo.svg';
import check from '/src/assets/check.svg';
import { For, createResource, createSignal } from "solid-js";
import { useNavigate } from '@solidjs/router';
import StructureDetailNote from './StructureDetailNote';
import StructureDetailCapteurs from './StructureDetailCapteurs';


function StructureDetailRow() {
    
    return (
        <div class="flex gap-x-[50px] w-full">
            <StructureDetailNote />
            <StructureDetailCapteurs />
        </div>
    );
}

export default StructureDetailRow

