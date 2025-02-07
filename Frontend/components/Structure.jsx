import logo from '/src/assets/logo.svg';
import log_in from '/src/assets/log_in.svg';
import { createSignal } from "solid-js";
import { useNavigate } from '@solidjs/router';
import LstStructureHead from './LstStructureHead';
import StructureBody from './StructSureBody';


function StructSure() {

    return (
        <div class="flex flex-col justify-center w-1250px mx-auto mt-10 gap-y-15px">
            <LstStructureHead />
            <StructureBody />
        </div>
    );
}

export default StructSure
