import { X, ChevronDown } from 'lucide-solid';
import StructureNameCard from '../StructureNameCard';
import { createSignal } from 'solid-js';

/**
 * Add account modal component
 * @param {Function} closeModal The function to close the modal
 * @returns The Model compoanent
 */
const AddAccountModal = ({ closeModal }) => {

    const [firstName, setFirstName] = createSignal("");
    const [lastName, setLastName] = createSignal("");
    const [login, setLogin] = createSignal("");
    const [password, setPassword] = createSignal("");
    const [role, setRole] = createSignal(""); 
    const [accountState, setAccountState] = createSignal(true);
    const [error, setError] = createSignal([]);

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
        setError(prevError => {
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
        setError(prevError => {
            return prevError.filter(errorValue => errorValue !== errorMessage);
        });
    };
    
    

    /**
     * Handle the submit buttom
     */
    const handleSubmit = (e) => {
        e.preventDefault();

        const fields = [
            lastName(),
            firstName(),
            login(),
            password(),
            role(),
        ];

        const missingFields = Object.values(fields).map(item => item === "" ? 1 : 0).filter(field => field !== 0);
        const errorMessage = "Assurez-vous que tous les champs marqués d'un astérisque (*) sont complétés.";
        const passworErrorMessage = "Le champ mot de passe doit contenir entre 12 et 64 caractères";

        if (missingFields.length > 0) {
            addError(errorMessage)
        }
        else {
            removeError(errorMessage)
        }

        if (password().length < 12) {
            addError(passworErrorMessage)
        }
        else {
            removeError(passworErrorMessage)
        }


    };


    return (
        <div class="bg-gray-800 bg-opacity-50 backdrop-blur-[10px] shadow-[0px 0px 50px 0px #33333340] z-[100] bg-[#00000040] flex justify-center align-middle w-[100vw] h-[100vh] absolute top-0 left-0 p-[25px]">
            <div class="sm:text-start inset-0 relative flex flex-col w-[100%] max-w-[776px] size-fit rounded-[20px] p-[25px] gap-[15px] bg-white shadow-[0px 0px 50px 0px #33333340]">
                <div class="flex justify-between items-center w-full gap-[10px]">
                    <h1 class="font-poppins text-[20px] sm:text-[20px] font-[600] leading-[30px] sm:leading-[37.5px] tracking-[0%]">
                        Créer un Compte
                    </h1>
                    <div class="flex flex-wrap gap-[10px]">
                        <button onClick={closeModal} class="flex justify-center items-center w-[40px] h-[40px] sm:w-[40px] sm:h-[40px] rounded-[50px] bg-[#F2F2F4]">
                            <X />
                        </button>
                    </div>
                </div>

                <div>
                    {error().map(err => (
                        <p class="text-[#F13327] font-poppins HeadLineMedium">{err}</p>
                    ))}
                </div>
                <form action="" >
                    <div className="flex flex-wrap gap-[15px] lg:gap-[50px] text-[#181818] font-poppins">
                        <div className="flex flex-col w-full lg:w-[338px] gap-[15px]">
                            <div className="flex flex-col gap-[5px]">
                                <label htmlFor="lastname" className="font-poppins HeadLineMedium text-[#181818]">Nom*</label>
                                <input
                                    id="lastname"
                                    required
                                    value={lastName()}
                                    onChange={(e) => setLastName(e.target.value)} 
                                    type="text"
                                    className="bg-[#F2F2F4] w-full rounded-[10px] py-[8px] px-[16px]"
                                    minLength="1"
                                    maxLength="64"
                                />

                            </div>
                            <div className="flex flex-col gap-[5px]">
                                <label htmlFor="firstname" className="font-poppins HeadLineMedium text-[#181818]">Prénom*</label>
                                <input
                                    id="firstname"
                                    required
                                    value={firstName()}
                                    onChange={(e) => setFirstName(e.target.value)} 
                                    type="text"
                                    className="bg-[#F2F2F4] w-full rounded-[10px] py-[8px] px-[16px]"
                                    minLength="1"
                                    maxLength="64"
                                />
                            </div>
                            <div className="flex flex-col gap-[5px]">
                                <label htmlFor="id" className="font-poppins HeadLineMedium text-[#181818]">Identifiant*</label>
                                <input
                                    id="id"
                                    required
                                    value={login()}
                                    onChange={(e) => setLogin(e.target.value)} 
                                    type="text"
                                    className="bg-[#F2F2F4] w-full h-[37px] rounded-[10px] py-[8px] px-[16px]"
                                    minLength="1"
                                    maxLength="128"
                                />
                            </div>
                        </div>

                        <div className="flex flex-col w-full lg:w-[338px] gap-[15px]">
                            <div className="flex flex-col gap-[5px]">
                                <label htmlFor="password" className="font-poppins HeadLineMedium text-[#181818]">Mot de passe*</label>
                                <input
                                    id="password"
                                    required
                                    value={password()}
                                    onChange={(e) => setPassword(e.target.value)}
                                    type="password"
                                    placeholder="*******"
                                    className="bg-[#F2F2F4] w-full h-[37px] rounded-[10px] py-[8px] px-[16px]"
                                    minLength="12"
                                    maxLength="64"
                                />
                            </div>
                            <div className="flex flex-col gap-[5px]">
                                <label htmlFor="role" className="font-poppins HeadLineMedium text-[#181818]">Role*</label>
                                <div className="relative">
                                    <select
                                        id="role"
                                        required
                                        value={role()}
                                        onChange={(e) => setRole(e.target.value)}
                                        name="roles"
                                        className="bg-[#F2F2F4] w-full h-[37px] rounded-[10px] px-[16px] appearance-none"
                                    >
                                        {
                                            roles.map((roleItem, index) => (
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
                                <label htmlFor="accountState" className="font-poppins HeadLineMedium text-[#181818]">Etat*</label>
                                <div className="flex items-center w-full h-[37px] rounded-[10px] py-[8px] px-[16px] gap-[10px]">
                                    <input
                                        id="accountState"
                                        checked={accountState()}
                                        onChange={(e) => setAccountState(e.target.checked)} 
                                        type="checkbox"
                                        className="w-[14px] h-auto bg-white border-2"
                                    />
                                    <span className="font-poppins HeadLineMedium">Compte activé</span>
                                </div>
                            </div>
                        </div>
                        
                    </div>
                

                {<div class="flex flex-col w-[100%] h-auto gap-[5px]">
                    <p class="text-[#181818] opacity-[75%]">Ouvrages autorisés</p>
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
                <div class="md:flex md:flex-row-reverse mt-[10px]">
                    <button type="submit" onClick={handleSubmit}  class="w-[72px] h-auto rounded-[50px] px-[16px] py-[8px] gap-[10px] bg-[#181818]">
                        <p class="w-[40px] h-auto text-white font-poppins font-[600] text-[14px] leading-[21px] tracking-[0%]">Créer</p>
                    </button>
                </div>
                </form>
            </div>
        </div>
    )
}

export default AddAccountModal