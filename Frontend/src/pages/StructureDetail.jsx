
import logo from '/src/assets/logo.svg';
import loginIconBlack from '/src/assets/loginIconBlack.svg';
import Header from '../../components/Header';
import { createSignal } from "solid-js";
import { useNavigate } from '@solidjs/router';
import StructureDetailBody from '../../components/StructureDetailBody';


function StructSureDetail() {

    return (
        <div class="flex flex-col gap-y-35px p-25px bg-gray-100">
            <Header />
            <StructureDetailBody />
        </div>
    );
}

export default StructSureDetail
