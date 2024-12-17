/* @refresh reload */
import { render } from 'solid-js/web';
import './Login.css';
import logo from '../assets/logo.svg';

function Login() {
    return (
        <>
        <div class="bg-gray-50 h-screen">
            <div class="p-25px mb-35px">
                <div class="flex justify-center">
                    <img src={logo} class="w-236px" alt="Vite logo" />
                </div>
            </div>
            <div class="px-25px">
                <div class="bg-white w-500px h-394px flex flex-col gap-x-25px pr-5 pl-5">
                    <div class="">
                        <h1>Connexion</h1>
                        <p class="w-276px size-3.5 text-sm">Renseignez vos identifiants StructSure pour accéder à l'application.</p>
                    </div>
                    <div>
                        
                    </div>

                    <div>
                        
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
