import logo from '/src/assets/logo.svg';
import leftAccount from '/src/assets/leftAccount.svg';
import accountButton from '/src/assets/accountButton.svg';
import { createSignal } from "solid-js";
import { useNavigate } from '@solidjs/router';


function Header() {

    return (
        <div class="flex justify-between">
            <img src={logo} class="w-236px" alt="Vite logo" />
            <div class="flex gap-x-25px items-center">
                <img src={leftAccount} class="w-10 h-10" alt="Vite logo" />
                <img src={accountButton} class="w-10 h-10" alt="Vite logo" />
            </div>
        </div>
    );
}

export default Header
