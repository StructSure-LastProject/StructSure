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
    const [error, setError] = createSignal("");
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

        const { fetchData, statusCode, data, errorFetch } = useFetch();
        await fetchData(url, requestData);

        if (statusCode() === 200) {
            const response = data();
            fillLocalStorage(response);
            navigate("/");
        } else if (statusCode() === 404) {
            const response = data();
            setError(errorFetch);
        }
    };

    /**
     * Handles the form submit event
     * @param {Event} e Form submit event 
     */
    const handlSubmit = async (e) => {
        e.preventDefault();
        const req = await loginFetchRequest("http://localhost:8080/api/login");    
    };
    

    return (
        <div class="bg-gray-100 h-screen">
            <div class="p-25px mb-35px">
                <div class="flex justify-center">
                    <img src={logo} class="w-236px" alt="Vite logo" />
                </div>
            </div>
            <div class="px-25px flex justify-center">
                <form class="bg-white w-500px h-394px flex flex-col gap-y-25px pr-5 pl-5 rounded-20px"
                onSubmit={handlSubmit}>
                    <div class="flex flex-col items-center">
                        <h1 class="text-2xl font-bold text-center py-0.5">Connexion</h1>
                        <div class="w-276px">
                            <p class="text-sm text-center text-gray-500">Renseignez vos identifiants StructSure pour accéder à l'application.</p>
                        </div>
                    </div>

                    <div>
                        <p class="text-sm text-center text-red-500">{error()}</p>
                    </div>

                    <div class="flex flex-col gap-y-25px">
                        <div class="flex flex-col justify-start">
                            <p class="text-sm text-gray-700">Identifiant</p>
                            <input 
                                id="Identifiant" 
                                type="text" 
                                placeholder="votre identifiant" 
                                class="w-full pb-2 pt-2 pl-4 pr-4 text-sm font-bold rounded-50px bg-gray-200"
                                onChange={(e) => setLogin(e.target.value)}
                            />
                        </div>
                            
                        <div class="flex flex-col justify-start">
                            <p class="text-sm text-gray-700">Mot de passe</p>
                            <input 
                                id="Identifiant" 
                                type="password" 
                                placeholder="votre identifiant" 
                                class="w-full pb-2 pt-2 pl-4 pr-4 text-sm font-bold rounded-50px bg-gray-200"
                                onChange={(e) => setPassword(e.target.value)}
                            />
                        </div>
                    </div>
                    <div class="flex justify-end">
                        <button type="submit" class="w-156px bg-black flex justify-between rounded-50px pb-2 pt-2 pl-4 pr-4" >
                            <p class="text-sm text-white">Se connecter</p>
                            <img src={log_in} class="w-20px" alt="Login Icon" />
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}

export default Login
