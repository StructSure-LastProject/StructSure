import { createSignal } from 'solid-js';
import useFetch from '../hooks/useFetch';
import { useNavigate } from '@solidjs/router';


/**
 * Account Change Password component
 */
function AccountChangePassword() {
    const navigate = useNavigate();

    const [errorFront, setErrorFront] = createSignal(null);

    const [oldPassword, setOldPassword] = createSignal(null);
    const [newPassword, setNewPassword] = createSignal(null);
    const [passwordConfirmation, setPasswordConfirmation] = createSignal(null);

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
     * Will call the api to change password
     */
    const changePasswordFetchRequest = async () => {
        const token = localStorage.getItem("token");
        const userId = localStorage.getItem("userId");

        const requestData = {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            },
            body: JSON.stringify({
                userId: userId,
                currentPassword: oldPassword(),
                newPassword: newPassword(),
                confirmNewPassword: passwordConfirmation()
            })
        };

        const { fetchData, statusCode, data, error } = useFetch();
        await fetchData(navigate, "/api/change-password", requestData);

        if (statusCode() === 200) {
            clearLocalStorage();
            navigate("/login");
        } else if (statusCode() === 422 || statusCode() === 404) {
            setErrorFront(error().errorData.error);
        }
    };

    /**
     * Handles the submit of the form that changes the password
     * @param {Event} event the submit event
     */
    const handlSubmit = (event) => {
        event.preventDefault();
        if (oldPassword() === null || newPassword() === null || passwordConfirmation() === null) {
            setErrorFront("Un ou plusieurs champ(s) vide(s)");
        } else if (newPassword() !== passwordConfirmation()) {
            setErrorFront("Le mot de passe et sa confirmation ne correspondent pas. Veuillez réessayer.");
        } else if (newPassword().length < 12 || newPassword().length > 64) {
            setErrorFront("Le mot de passe doit comporter entre 12 et 64 caractères");
        } else {
            changePasswordFetchRequest();
        }
    };

    return (
        <div class="px-25px flex justify-center">
            <form class="bg-white w-500px flex flex-col gap-y-25px px-[20px] py-[15px] rounded-20px"
            onSubmit={handlSubmit}>
                <div class="flex flex-col">
                    <h1 class="title">Changer le mot de passe</h1>
                    <p class="text-sm text-red">{errorFront()}</p>
                </div>

                <div class="flex flex-col gap-y-25px">
                    <div class="flex flex-col gap-[5px] justify-start">
                        <p class="normal opacity-75">Ancien mot de passe</p>
                        <input 
                            id="Password" 
                            type="password" 
                            placeholder="••••••••••••" 
                            class="w-full py-2 px-4 normal rounded-50px  bg-lightgray"
                            onChange={(e) => setOldPassword(e.target.value)}
                        />
                    </div>
                    <div class="flex flex-col gap-[5px] justify-start">
                        <p class="normal opacity-75">Nouveau mot de passe</p>
                        <input 
                            id="Password" 
                            type="password" 
                            placeholder="••••••••••••" 
                            class="w-full py-2 px-4 normal rounded-50px  bg-lightgray"
                            onChange={(e) => setNewPassword(e.target.value)}
                        />
                    </div>
                    <div class="flex flex-col gap-[5px] justify-start">
                        <p class="normal opacity-75">Confirmation du mot de passe</p>
                        <input 
                            id="Password" 
                            type="password" 
                            placeholder="••••••••••••" 
                            class="w-full py-2 px-4 normal rounded-50px  bg-lightgray"
                            onChange={(e) => setPasswordConfirmation(e.target.value)}
                        />
                    </div>
                </div>
                <div class="flex justify-end">
                    <button type="submit" class="bg-black flex justify-between gap-[10px] rounded-50px py-2 px-4" >
                        <p class="accent text-white">Se connecter</p>
                        
                    </button>
                </div>
            </form>
        </div>
    );
}

export default AccountChangePassword
