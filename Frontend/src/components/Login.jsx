import logo from '/src/assets/logo.svg';
import log_in from '/src/assets/log_in.svg';
import { createSignal } from "solid-js";
import { useNavigate } from '@solidjs/router';
import useFetch from '../hooks/useFetch';

/**
 * Component for the login
 * @returns component for the login
 */
function Login() {
    const [login, setLogin] = createSignal("");
    const [password, setPassword] = createSignal("");
    const [errorFront, setErrorFront] = createSignal("");
    const navigate = useNavigate();

    /**
     * Fills the local storage with informations of the user
     * @param {Object} response the response containing token, role, ...
     */
    const fillLocalStorage = (response) => {
        localStorage.setItem("token", response.token);
        localStorage.setItem("role", response.role);
        localStorage.setItem("firstName", response.firstName);
        localStorage.setItem("lastName", response.lastName);
        localStorage.setItem("login", response.login);
    };

    /**
     * Call the login endpoint to login the user
     * @param {String} url the url of the server 
     */
    const loginFetchRequest = async (url) => {
        const requestBody = JSON.stringify({
            login: login(),
            password: password()
        });
        
        const requestData = {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: requestBody,
        };

        const { fetchData, statusCode, data, error } = useFetch();
        await fetchData(url, requestData);

        if (statusCode() === 200) {
            const response = data();
            fillLocalStorage(response);
            navigate("/");
        } else if (statusCode() === 404) {
            setErrorFront(error().errorData.error);
        }
    };

    /**
     * Handles the form submit event
     * @param {Event} e Form submit event 
     */
    const handlSubmit = async (e) => {
        e.preventDefault();
        const req = await loginFetchRequest("/api/login");    
    };
    

    return (
        <div class="bg-lightgray min-h-screen">
            <div class="p-25px mb-35px">
                <div class="flex justify-center">
                    <img src={logo} class="w-236px" alt="Vite logo" />
                </div>
            </div>
            <div class="px-25px flex justify-center">
                <form class="bg-white w-500px flex flex-col gap-y-25px px-[20px] py-[15px] rounded-20px"
                onSubmit={handlSubmit}>
                    <div class="flex flex-col items-center">
                        <h1 class="title text-center">Connexion</h1>
                        <p class="w-276px normal text-center opacity-50">Renseignez vos identifiants StructSure pour accéder à l'application.</p>
                    </div>

                    <div>
                        <p class="text-sm text-center text-red-500">{errorFront()}</p>
                    </div>

                    <div class="flex flex-col gap-y-25px">
                        <div class="flex flex-col gap-[5px] justify-start">
                            <p class="normal opacity-75">Identifiant</p>
                            <input 
                                id="Login" 
                                type="text" 
                                placeholder="votre identifiant" 
                                class="w-full py-2 px-4 normal rounded-50px bg-lightgray"
                                onChange={(e) => setLogin(e.target.value)}
                            />
                        </div>
                            
                        <div class="flex flex-col gap-[5px] justify-start">
                            <p class="normal opacity-75">Mot de passe</p>
                            <input 
                                id="Password" 
                                type="password" 
                                placeholder="••••••••••••" 
                                class="w-full py-2 px-4 normal rounded-50px  bg-lightgray"
                                onChange={(e) => setPassword(e.target.value)}
                            />
                        </div>
                    </div>
                    <div class="flex justify-end">
                        <button type="submit" class="bg-black flex justify-between gap-[10px] rounded-50px py-2 px-4" >
                            <p class="accent text-white">Se connecter</p>
                            <img src={log_in} class="w-20px" alt="Login Icon" />
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}

export default Login
