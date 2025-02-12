import { Trash2, X, ChevronDown } from 'lucide-solid';
import StructureNameCard from '../StructureNameCard';
import { createEffect, createSignal } from 'solid-js';

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
    const [accountState, setAccountState] = createSignal(false);
    const [error, setError] = createSignal([]);

    /**
     * Handle the submit buttom
     */
    const handleSubmit = () => {
        const fields = {
            "Nom" : lastName(),
            "Prénom" : firstName(),
            "Identifiant": login(),
            "Mot de passe" : password(),
            "Role" : role(),
        };
        const missingFields = Object.values(fields)
                            .map((field, index) => field === "" ? index : -1)
                            .filter(field => field !== -1)
                            .map(element => Object.keys(fields)[element]).join(", ");
        
        if (missingFields !== "") {
            const newErrorMessage = `Assurez-vous que tous les champs marqués d'un astérisque (*) sont complétés : ${missingFields}`;
            setError(prevError => {
                prevError = prevError.filter(error => error.toLowerCase().startsWith(newErrorMessage));
                if (!prevError.includes(newErrorMessage)) {
                    return [...prevError, newErrorMessage];
                }
                return prevError;
            });
        }
                            

    }



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
                    <p class="text-[#F13327] font-poppins HeadLineMedium">{error().map(item => (
                        <p>{item}</p>
                    ))}</p>
                </div>
                <div class="flex flex-wrap gap-[15px] lg:gap-[50px] text-[#181818] font-poppins">
                    <div class="flex flex-col w-full lg:w-[338px] gap-[15px]">
                        <div class="flex flex-col gap-[5px]">
                            <p class="font-poppins HeadLineMedium text-[#181818]">Nom*</p>
                            <input required value={lastName()} onInput={(e) => setLastName(e.target.value)} type="text" class="bg-[#F2F2F4] w-full rounded-[10px] py-[8px] px-[16px]" />
                        </div>
                        <div class="flex flex-col gap-[5px]">
                            <p class="font-poppins HeadLineMedium text-[#181818]">Prénom*</p>
                            <input required value={firstName()} onInput={(e) => setFirstName(e.target.value)} type="text" class="bg-[#F2F2F4] w-full rounded-[10px] py-[8px] px-[16px]" />
                        </div>
                        <div class="flex flex-col gap-[5px]">
                            <p class="font-poppins HeadLineMedium text-[#181818]">Identifiant*</p>
                            <input required value={login()} onInput={(e) => setLogin(e.target.value)} type="email" class="bg-[#F2F2F4] w-full h-[37px] rounded-[10px] py-[8px] px-[16px]" />
                        </div>
                    </div>
                <div class="flex flex-col w-full lg:w-[338px] gap-[15px]">
                    <div class="flex flex-col gap-[5px]">
                        <p class="font-poppins HeadLineMedium text-[#181818]">Mot de passe*</p>
                        <input required value={password()} onInput={(e) => setPassword(e.target.value)} type="password" placeholder="*******" class="bg-[#F2F2F4] w-full h-[37px] rounded-[10px] py-[8px] px-[16px]" />
                    </div>
                    <div class="flex flex-col gap-[5px]">
                        <p class="font-poppins HeadLineMedium text-[#181818]">Role*</p>
                        <div class="relative">
                            <select required value={role()} onChange={(e) => setRole(e.target.value)} name="roles" id="roles" class="bg-[#F2F2F4] w-full h-[37px] rounded-[10px] px-[16px] appearance-none">
                                <option value="Opérateur">Opérateur</option>
                                <option value="Responsable">Responsable</option>
                                <option value="Admin">Admin</option>
                            </select>
                            <div class="absolute right-4 top-1/2 transform -translate-y-1/2 flex items-center pointer-events-none">
                                <ChevronDown size={20} strokeWidth={2.5} />
                            </div>
                        </div>

                    </div>
                    <div class="flex flex-col gap-[5px]">
                        <p class="font-poppins HeadLineMedium text-[#181818]">Etat*</p>
                        <div class="flex items-center w-full h-[37px] rounded-[10px] py-[8px] px-[16px] gap-[10px]">
                            <input required checked={accountState()} onChange={(e) => setAccountState(e.target.checked)} type="checkbox" class="w-[14px] h-auto bg-white border-2" />
                            <span class="font-poppins HeadLineMedium">Compte activé</span>
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
                <div class="md:flex md:flex-row-reverse">
                    <button onClick={handleSubmit}  class="w-[72px] h-auto rounded-[50px] px-[16px] py-[8px] gap-[10px] bg-[#181818]">
                        <p class="w-[40px] h-auto text-white font-poppins font-[600] text-[14px] leading-[21px] tracking-[0%]">Créer</p>
                    </button>
                </div>
            </div>
        </div>
    )
}

export default AddAccountModal