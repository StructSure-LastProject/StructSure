import logo from '/src/assets/logo.svg';
import loginIcon from '/src/assets/loginIcon.svg';
import { createResource, createSignal } from "solid-js";

function NeedsAuthentification() {

    return (
        <div class="p-25px mb-35px">
            <div class="flex justify-center">
                <img src={logo} class="w-236px" alt="Vite logo" />
            </div>
            <p>{localStorage.getItem("token")}</p>
        </div>
    );
}

export default NeedsAuthentification
