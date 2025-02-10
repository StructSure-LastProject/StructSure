import loginIconBlack from '/src/assets/loginIconBlack.svg';
import Header from '../components/Header';
import { useNavigate } from '@solidjs/router';


function Account() {

    const navigate = useNavigate();

    const clearLocalStorage = () => {
        localStorage.removeItem("token");
        localStorage.removeItem("role");
        localStorage.removeItem("firstName");
        localStorage.removeItem("lastName");
        localStorage.removeItem("login");
    };

    const handlSubmit = async (e) => {
        e.preventDefault();
        clearLocalStorage();
        navigate("/login");
    };

    return (
        <div class="p-25px">
            <Header />
            <div class="flex justify-center">
                <form class="bg-black w-500px rounded-20px flex flex-col gap-y-25px px-5 px-15px" onSubmit={handlSubmit}>
                    <div class="flex flex-col items-center">
                        <h1 class="text-2xl font-bold text-center py-0.5 text-white">Bonjour {localStorage.getItem("firstName") + " " +  localStorage.getItem("lastName")}</h1>
                        <p class="text-xs text-white text-center text-gray-500">{localStorage.getItem("login")}</p>
                    </div>
                    <div class="flex justify-center">
                        <button type="submit" class="w-156px bg-white flex justify-between rounded-50px pb-2 pt-2 pl-4 pr-4" >
                            <p class="text-sm">Se déconnecter</p>
                            <img src={loginIconBlack} class="w-20px" alt="Login Icon" />
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}

export default Account
