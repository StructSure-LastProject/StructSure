import logo from '/src/assets/logo.svg';
import leftAccount from '/src/assets/leftAccount.svg';
import accountButton from '/src/assets/accountButton.svg';
import { A, useNavigate } from '@solidjs/router';
import { JSX } from 'solid-js';

/**
 * Component for the header
 * @returns {JSX.Element} component for the Header
 */
function Header() {
    const navigate = useNavigate();

    return (
        <div class="flex justify-between">
            <A href="/">
                <img src={logo} class="w-236px" alt="Application logo" onClick={() => navigate("/")}/>
            </A>
            <div class="flex gap-x-25px items-center">
                <img src={leftAccount} class="w-10 h-10" alt="Vite logo" />
                <A href="/account">
                    <img src={accountButton} class="w-10 h-10" alt="Vite logo"/>
                </A>
            </div>
        </div>
    );
}

export default Header
