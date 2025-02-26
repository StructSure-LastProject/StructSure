import { Trash2, X, ChevronDown } from 'lucide-solid';
import { createEffect, createResource, createSignal } from 'solid-js';
import { validateUserAccountForm } from '../../hooks/vaildateUserAccountForm';
import useFetch from '../../hooks/useFetch';
import AccountStructureSection from './AccountStructureSection';
import { useNavigate } from '@solidjs/router';

/**
 * Edit account modal component
 * @param {Function} fetchUserDetails The fetch user details function
 * @param {Function} closeModal The function to close the modal
 * @param {Object} userDetails The userDetails object
 * @returns The Model component
 */
const EditAccountModal = ({fetchUserDetails, closeModal, userDetails}) => {

    const [firstName, setFirstName] = createSignal(userDetails.firstName);
    const [lastName, setLastName] = createSignal(userDetails.lastName);
    const [password, setPassword] = createSignal("");
    const [role, setRole] = createSignal(userDetails.role); 
    const [accountState, setAccountState] = createSignal(userDetails.accountState);
    const [errorModal, setErrorModal] = createSignal([]);
    const [apiError, setApiError] = createSignal("");
    const [structureSelection, setStructureSelection] = createSignal([]);
    const copyOfStructureSelection = [];
    const login = userDetails.login;
    const navigate = useNavigate();


    const login = userDetails.login;
    const { fetchData, data, statusCode, error } = useFetch();


    
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
     * Create effect to fill the two arrays
     */
    createEffect(() => {
        if (copyOfStructureSelection.length === 0) {
            if (data() !== null) {
                for (const e of data().structureAcessDetailsList) {
                    copyOfStructureSelection.push({structureId: e.structureId, structureName: e.structureName, hasAccess: e.hasAccess});
                }
                
            }
        }
    })


    /**
     * Function that verify if the two arrays are equal
     * @param {Array} arr1 First Array 
     * @param {Array} arr2 Second Array 
     * @param {Function} compareFn The comparing function 
     * @returns True if equal or false if not equal
     */
    const areArraysEqual = (arr1, arr2, compareFn = (a, b) => a === b) => {
        if (arr1.length !== arr2.length) return false;
      
        for (let i = 0; i < arr1.length; i++) {
          if (!compareFn(arr1[i], arr2[i])) {
            return false;
          }
        }
      
        return true; 
    }
      

    /**
     * Helper function to compare objects inside the array
     * @param {Object} a First Object 
     * @param {Object} b Second Object
     * @returns True if equal or false if not equal
     */
    const deepCompare = (a, b) => {
        if (typeof a !== 'object' || typeof b !== 'object') {
            return a === b;
        }
        
        if (Object.keys(a).length !== Object.keys(b).length) return false;

        for (const key in a) {
            if (!Object.prototype.hasOwnProperty.call(a, key)) continue;

            if (!deepCompare(a[key], b[key])) return false;
        }

        
        return true;
    };


    /**
     * Create effect to fill the two arrays
     */
    createEffect(() => {
        if (copyOfStructureSelection.length === 0) {
            if (data() !== null) {
                for (const e of data().structureDetailsList) {
                    copyOfStructureSelection.push({structureId: e.structureId, structureName: e.structureName, hasAccess: e.hasAccess});
                }
                
            }
        }
    })

    /**
     * Get structure for the user account
     */
    const getStructuresForAccount = async () => {
        const token = localStorage.getItem("token");
        
        const requestData = {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            }
        };

        await fetchData(`/api/accounts/${login}/structures`, requestData);

        if (statusCode() === 200) {
            setStructureSelection(data().structureAccessDetailsList);
        }

    }
      
    

    /**
     * Handle the submit buttom
     * @param {Event} e The click event
     */
    const handleSubmit = async (e) => {
        e.preventDefault();

        validateUserAccountForm(firstName(), lastName(), login, role(), password(), addError, removeError, false)

        const token = localStorage.getItem("token")

        if (errorModal().length === 0) {
            const updatedFields = [];

            if (firstName() !== userDetails.firstName) updatedFields.push("firstname");
            if (lastName() !== userDetails.lastName) updatedFields.push("lastname");
            if (role() !== userDetails.role) updatedFields.push("role");
            if (accountState() !== userDetails.accountState) updatedFields.push("accountState");
            if (password() !== "") updatedFields.push("password");
            if (!areArraysEqual(structureSelection(), copyOfStructureSelection, deepCompare)) updatedFields.push("structures")


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
             * @param {String} method The HTTP method 
             * @param {String} requestData The request data to send
             * @returns json object
             */
            const createRequestData = (requestMethod, requestData) => {
                return {
                    method: requestMethod,
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

            if (updatedFields.includes("structures")) {
                await fetchData(`/api/accounts/${userDetails.login}/access`, createRequestData("POST", { access: structureSelection() }));
                if (statusCode() === 200) {
                    updatedFields.pop("structures");
                }
            }
            
            await fetchData(navigate, "/api/accounts/reset", createRequestData("PUT", requestBody));

            
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

    createResource(() => getStructuresForAccount())


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
                                    <div class="absolute right-4 top-1/2 transform -translate-y-1/2 flex items-center pointer-events-none">
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

                    {
                        <div class="flex flex-col w-[100%] h-auto gap-[5px]">
                            <p class="normal opacity-50">Ouvrages autorisés</p>
                            <AccountStructureSection 
                                structures={structureSelection} 
                                setStructureSelection={setStructureSelection}
                            />
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