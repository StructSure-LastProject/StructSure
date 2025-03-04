import loginIconBlack from '/src/assets/loginIconBlack.svg';
import Header from '../components/Header';
import { useNavigate } from '@solidjs/router';
import AccountChangePassword from '../components/AccountChangePassword';

/**
 * Component for the account page
 * @returns component for the account page
 */
function Account() {

    const navigate = useNavigate();

    /**
     * Clears the data about the user in the local storage
     */
    const clearLocalStorage = () => {
        localStorage.removeItem("token");
        localStorage.removeItem("role");
        localStorage.removeItem("firstName");
        localStorage.removeItem("lastName");
        localStorage.removeItem("login");
        localStorage.removeItem("controlChip");
        localStorage.removeItem("measureChip");
        localStorage.removeItem("userId");
    };

    /**
     * Handles the submit event
     * @param {Event} e the submit event 
     */
    const handlSubmit = async (e) => {
        e.preventDefault();
        clearLocalStorage();
        navigate("/login");
    };

    return (
        <div class="bg-lightgray min-h-screen p-25px">
            <Header />
            <div class="flex justify-center">
                <form class="bg-black w-500px rounded-20px flex flex-col gap-y-25px px-5 px-15px" onSubmit={handlSubmit}>
                    <div class="flex flex-col items-center">
                        <h1 class="title text-center py-0.5 text-white">Bonjour {localStorage.getItem("firstName") + " " +  localStorage.getItem("lastName")}</h1>
                        <p class="normal text-white text-center">{localStorage.getItem("login")}</p>
                    </div>
                    <div class="flex justify-center">
                        <button type="submit" class="bg-white flex justify-between gap-[10px] rounded-50px pb-2 pt-2 pl-4 pr-4" >
                            <p class="accent">Se déconnecter</p>
                            <img src={loginIconBlack} class="w-20px" alt="Login Icon" />
                        </button>
                    </div>
                </form>
            </div>
            <AccountChangePassword />
        </div>
    );
}

export default Account
