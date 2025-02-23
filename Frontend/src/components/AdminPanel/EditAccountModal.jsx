import { Trash2, X, ChevronDown } from 'lucide-solid';
import StructureNameCard from '../StructureNameCard';
import { createSignal } from 'solid-js';
import { validateUserAccountForm } from '../../hooks/vaildateUserAccountForm';
import useFetch from '../../hooks/useFetch';

/**
 * Edit account modal component
 * @param {Function} closeModal The function to close the modal
 * @returns The Model compoanent
 */
const EditAccountModal = ({fetchUserDetails, closeModal, userDetails}) => {

    const [firstName, setFirstName] = createSignal(userDetails.firstName);
    const [lastName, setLastName] = createSignal(userDetails.lastName);
    const [password, setPassword] = createSignal("");
    const [role, setRole] = createSignal(userDetails.role); 
    const [accountState, setAccountState] = createSignal(userDetails.accountState);
    const [errorModal, setErrorModal] = createSignal([]);
    const [apiError, setApiError] = createSignal("");
    const login = userDetails.login;

    /**
     * Roles
     */
    const roles = [
        "Opérateur",
        "Responsable",
        "Admin"
    ];

    /**
     * Add error message to show
     * @param {string} newErrorMessage Error message 
     */
    const addError = (newErrorMessage) => {
        setErrorModal(prevError => {
            if (!prevError.includes(newErrorMessage)) {
                return [...prevError, newErrorMessage];
            }
            return prevError;
        });
    };

    /**
     * Remove error message
     * @param {string} errorMessage Error message to remove 
     */
    const removeError = (errorMessage) => {
        setErrorModal(prevError => {
            return prevError.filter(errorValue => errorValue !== errorMessage);
        });
    };
    
    

    /**
     * Handle the submit buttom
     */
    const handleSubmit = async (e) => {
        e.preventDefault();

        validateUserAccountForm(firstName(), lastName(), login, role(), password(), addError, removeError, false)

        const { fetchData, error, statusCode } = useFetch();
        const token = localStorage.getItem("token")


        if (errorModal().length === 0) {
            const updatedFields = [];

            if (firstName() !== userDetails.firstName) updatedFields.push("firstname");
            if (lastName() !== userDetails.lastName) updatedFields.push("lastname");
            if (role() !== userDetails.role) updatedFields.push("role");
            if (accountState() !== userDetails.accountState) updatedFields.push("accountState");
            if (password() !== "") updatedFields.push("password");


            if (updatedFields.length === 0) {
                setApiError("");
                closeModal();
                return;
            }


            const requestBody = {
                firstname: userDetails.firstName,
                lastname: userDetails.lastName,
                login: userDetails.login,
                role: userDetails.role,
                password: "",
                accountState: userDetails.accountState,
            }


            /**
             * Create the body of the request
             * @param {object} requestBody 
             * @param {string} token 
             * @returns json object
             */
            const createRequestData = (requestData) => {
                return {
                    method: "PUT",
                    headers: {
                        "Content-Type": "application/json",
                        "Authorization": `Bearer ${token}`
                    },
                    body: JSON.stringify(requestData),
                };
            }
            
            if(updatedFields.includes("firstname")){
                requestBody.firstname = firstName();
            }

            if(updatedFields.includes("lastname")){
                requestBody.lastname = lastName();
            }

            if (updatedFields.includes("role")) {
                requestBody.role = role();
            }

            if(updatedFields.includes("password")){
                requestBody.password = password();
            }

            if(updatedFields.includes("accountState")){
                requestBody.accountState = accountState();
            }
            
            await fetchData("/api/accounts/reset", createRequestData(requestBody));

            
            let editError = "";
            if(error() !== null){
                editError = error().errorData.error;
            }
            if (statusCode() === 200) {
                closeModal();
                fetchUserDetails();
                setApiError("");
            }
            else if (statusCode() === 404){
                setApiError(editError);
            }
            else if (statusCode() === 422) {            
                setApiError(editError);
            }
            
        }
        
    };



    return (
        <div class="min-h-[100vh] items-center bg-gray-800 bg-opacity-50 backdrop-blur-[10px] shadow-[0px 0px 50px 0px #33333340] z-[100] bg-[#00000040] flex justify-center align-middle w-[100vw] h-[100vh] absolute top-0 left-0 p-[25px]">
            <div class="max-h-[100%]  overflow-y-auto  sm:text-start inset-0 relative flex flex-col w-[100%] max-w-[776px] size-fit rounded-[20px] p-[25px] gap-[15px] bg-white shadow-[0px 0px 50px 0px #33333340]">
                <div class="flex justify-between items-center w-full gap-[10px]">
                    <h1 class="title">Edition de Compte</h1>
                    <div class="flex flex-wrap gap-[10px]">
                        <button class="flex justify-center items-center w-[40px] h-[40px] sm:w-[40px] sm:h-[40px] rounded-[50px] bg-[#F133271A]">
                            <Trash2 color="#f13327" size={20} width={20} top={10} left={10}/>
                        </button>
                        <button onClick={closeModal} class="flex justify-center items-center w-[40px] h-[40px] sm:w-[40px] sm:h-[40px] rounded-[50px] bg-lightgray">
                            <X />
                        </button>
                    </div>
                </div>

                <div>
                    {errorModal().map(err => (
                        <p class="text-red normal">{err}</p>
                    ))}
                    {
                        <p class="text-red normal">{apiError()}</p>
                    }
                </div>
                
                <form action="" class="flex flex-col gap-5">
                    <div className="flex flex-wrap gap-[15px] lg:gap-[50px] text-black font-poppins">
                        <div className="flex flex-col w-full lg:w-[338px] gap-[15px]">
                            <div className="flex flex-col gap-[5px]">
                                <label htmlFor="lastname" className="normal opacity-50">Nom*</label>
                                <input
                                    id="lastname"
                                    required
                                    value={lastName()}
                                    onChange={(e) => setLastName(e.target.value)}
                                    type="text"
                                    className="normal bg-lightgray w-full rounded-[10px] py-[8px] px-[16px]"
                                    minLength="1"
                                    maxLength="64"
                                />

                            </div>
                            <div className="flex flex-col gap-[5px]">
                                <label htmlFor="firstname" className="normal opacity-50">Prénom*</label>
                                <input
                                    id="firstname"
                                    required
                                    value={firstName()}
                                    onChange={(e) => setFirstName(e.target.value)}
                                    type="text"
                                    className="normal bg-lightgray w-full rounded-[10px] py-[8px] px-[16px]"
                                    minLength="1"
                                    maxLength="64"
                                />
                            </div>
                            <div className="flex flex-col gap-[5px]">
                                <label htmlFor="id" className="normal opacity-50">Identifiant*</label>
                                <input
                                    id="id"
                                    required
                                    value={login}
                                    type="text"
                                    className="normal bg-lightgray w-full h-[37px] rounded-[10px] py-[8px] px-[16px] opacity-[70%]"
                                    minLength="1"
                                    maxLength="128"
                                    disabled
                                />
                            </div>
                        </div>

                        <div className="flex flex-col w-full lg:w-[338px] gap-[15px]">
                            <div className="flex flex-col gap-[5px]">
                                <label htmlFor="password" className="normal opacity-50">Mot de passe</label>
                                <input
                                    id="password"
                                    required
                                    value={password()}
                                    onChange={(e) => setPassword(e.target.value)}
                                    type="password"
                                    placeholder="*******"
                                    className="normal bg-lightgray w-full h-[37px] rounded-[10px] py-[8px] px-[16px]"
                                />
                            </div>
                            <div className="flex flex-col gap-[5px]">
                                <label htmlFor="role" className="normal opacity-50">Role*</label>
                                <div className="relative">
                                    <select
                                        id="role"
                                        required
                                        value={role()}
                                        onChange={(e) => setRole(e.target.value)}
                                        name="roles"
                                        className="normal bg-lightgray w-full h-[37px] rounded-[10px] px-[16px] appearance-none"
                                    >
                                        {
                                            roles.map((roleItem, index) => (
                                                roleItem === "Admin" && localStorage.getItem("login") !== "StructSureAdmin" ? <option key={index} value={roleItem} disabled>{roleItem}</option> :
                                                <option key={index} value={roleItem}>{roleItem}</option>
                                            ))
                                        }
                                    </select>
                                    <div className="absolute right-4 top-1/2 transform -translate-y-1/2 flex items-center pointer-events-none">
                                        <ChevronDown size={20} strokeWidth={2.5} />
                                    </div>
                                </div>
                            </div>
                            <div className="flex flex-col gap-[5px]">
                                <label htmlFor="accountState" className="normal opacity-50">Etat*</label>
                                <div className="flex items-center w-full h-[37px] rounded-[10px] py-[8px] px-[16px] gap-[10px]">
                                    <label className="normal flex gap-[10px] select-none">
                                        <input
                                            id="accountState"
                                            checked={accountState()}
                                            onChange={(e) => setAccountState(e.target.checked)}
                                            type="checkbox"
                                            className="normal bg-lightgray border-2 accent-black"
                                        />
                                        Compte activé
                                    </label> 
                                </div>
                            </div>
                        </div>
                        
                    </div>

                    {<div class="flex flex-col w-[100%] h-auto gap-[5px]">
                        <p class="normal opacity-50">Ouvrages autorisés</p>
                        <div class="w-[100%] h-auto flex flex-wrap gap-[10px]">
                            <StructureNameCard structureName={"Grand-Pont de Nemours"}/>
                            <StructureNameCard structureName={"Pont de Tournon-sur-Rhône"} isChoosed={true}/>
                            <StructureNameCard structureName={"Pegasus Bridge"} isChoosed={false}/>
                            <StructureNameCard structureName={"Pont Albert-Louppe"} isChoosed={false}/>
                            <StructureNameCard structureName={"Pont Boutiron"} isChoosed={false}/>
                            <StructureNameCard structureName={"Pont d’Ain"} isChoosed={false}/>
                            <StructureNameCard structureName={"Pont levant de La Seyne-sur-Mer"} isChoosed={false}/>
                            <StructureNameCard structureName={"Pont d’Èze"} isChoosed={false}/>
                            <StructureNameCard structureName={"Pont suspendu de Saint-Ilpize"} isChoosed={false}/>
                        </div>
                    </div>
                    }
                    <div class="md:flex md:flex-row-reverse">
                        <button type="submit" onClick={handleSubmit} class="rounded-[50px] px-[16px] py-[8px] bg-lightgray">
                            <p class="accent">Mettre à jour</p>
                        </button>
                    </div>
                </form>
            </div>
        </div>
    )
}

export default EditAccountModal