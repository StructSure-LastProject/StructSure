/* @refresh reload */
import { render } from 'solid-js/web';
import './Login.css';
import logo from '/src/assets/logo.svg';

function Login() {
    return (
        <>
        <div class="bg-gray-50 h-screen">
            <div class="p-25px mb-35px">
                <div class="flex justify-center">
                    <img src={logo} class="w-236px" alt="Vite logo" />
                </div>
            </div>
            <div class="px-25px flex justify-center">
                <div class="bg-white w-500px h-394px flex flex-col gap-y-25px pr-5 pl-5 rounded-20px">
                    <div class="flex flex-col items-center">
                        <h1 class="text-2xl font-bold text-center">Connexion</h1>
                        <div class="w-276px">
                            <p class="text-sm text-center text-gray-500">Renseignez vos identifiants StructSure pour accéder à l'application.</p>
                        </div>
                    </div>

                    <div>
                        <p class="text-sm text-center text-red-500">Identifiant et/ou mot de passe incorrecte</p>
                    </div>

                    <div class="flex flex-col gap-y-25px">
                        <div class="flex flex-col justify-start">
                            <p class="text-sm text-gray-700">Identifiant</p>
                            <input 
                                id="Identifiant" 
                                type="text" 
                                placeholder="Votre email" 
                                class="mt-1 mb-4 w-full"
                            />
                        </div>
                            
                        <div class="flex justify-start">
                            <p class="text-sm text-center text-gray-700">Mot de passe</p>
                        </div>
                    </div>
                    <div>
                        
                    </div>
                </div>
            </div>
        </div>
        </>
    );
}

export default Login
