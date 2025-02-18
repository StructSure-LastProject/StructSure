import logo from '/src/assets/logo.svg';
import leftAccount from '/src/assets/leftAccount.svg';
import accountButton from '/src/assets/accountButton.svg';
import { A } from '@solidjs/router';

/**
 * Component for the header
 * @returns component for the Header
 */
function Header() {

    return (
        <div class="flex justify-between">
            <A href="/">
                <img src={logo} class="w-236px" alt="Application logo" />
            </A>
            <div class="flex gap-x-25px items-center">
                {localStorage.getItem("role") === "ADMIN" &&
                    <A href="/admin-panel">
                        <img src={leftAccount} class="w-10 h-10" alt="Vite logo" />
                    </A>
                }
                <A href="/account">
                    <img src={accountButton} class="w-10 h-10" alt="Vite logo"/>
                </A>
            </div>
        </div>
    );
}

export default Header
