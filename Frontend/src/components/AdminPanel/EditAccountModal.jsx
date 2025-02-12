import { Trash2, X, ChevronDown } from 'lucide-solid';
import StructureNameCard from '../StructureNameCard';
import { createSignal } from 'solid-js';

/**
 * Edit account modal component
 * @param {Function} closeModal The function to close the modal
 * @returns The Model compoanent
 */
const EditAccountModal = ({ closeModal, userDetails}) => {

    const [firstName, setFirstName] = createSignal(userDetails.firstName);
    const [lastName, setLastName] = createSignal(userDetails.lastName);
    const [login, setLogin] = createSignal(userDetails.login);
    const [password, setPassword] = createSignal("");
    const [role, setRole] = createSignal(userDetails.role); 
    const [accountState, setAccountState] = createSignal(userDetails.accountState);




    return (
        <div class="bg-gray-800 bg-opacity-50 backdrop-blur-[10px] shadow-[0px 0px 50px 0px #33333340] z-[100] bg-[#00000040] flex justify-center align-middle w-[100vw] h-[100vh] absolute top-0 left-0 p-[25px]">
            <div class="sm:text-start inset-0 relative flex flex-col w-[100%] max-w-[776px] size-fit rounded-[20px] p-[25px] gap-[15px] bg-white shadow-[0px 0px 50px 0px #33333340]">
                <div class="flex justify-between items-center w-full gap-[10px]">
                    <h1 class="font-poppins text-[20px] sm:text-[20px] font-[600] leading-[30px] sm:leading-[37.5px] tracking-[0%]">
                        Edition de Compte
                    </h1>
                    <div class="flex flex-wrap gap-[10px]">
                        <button class="flex justify-center items-center w-[40px] h-[40px] sm:w-[40px] sm:h-[40px] rounded-[50px] bg-[#F133271A]">
                            <Trash2 color="#f13327" size={20} width={20} top={10} left={10}/>
                        </button>
                        <button onClick={closeModal} class="flex justify-center items-center w-[40px] h-[40px] sm:w-[40px] sm:h-[40px] rounded-[50px] bg-[#F2F2F4]">
                            <X />
                        </button>
                    </div>
                </div>

                <div>
                    <p class="text-[#F13327] font-poppins HeadLineMedium">Error</p>
                </div>
                
                <div class="flex flex-wrap gap-[15px] lg:gap-[50px] text-[#181818] font-poppins">
                    <div class="flex flex-col w-full lg:w-[338px] gap-[15px]">
                        <div class="flex flex-col gap-[5px]">
                            <p class="font-poppins HeadLineMedium text-[#181818]">Nom*</p>
                            <input value={lastName()} type="text" class="bg-[#F2F2F4] w-full rounded-[10px] py-[8px] px-[16px]" />
                        </div>
                        <div class="flex flex-col gap-[5px]">
                            <p class="font-poppins HeadLineMedium text-[#181818]">Prénom*</p>
                            <input value={firstName()} type="text" class="bg-[#F2F2F4] w-full rounded-[10px] py-[8px] px-[16px]" />
                        </div>
                        <div class="flex flex-col gap-[5px]">
                            <p class="font-poppins HeadLineMedium text-[#181818]">Identifiant*</p>
                            <input value={login()} type="text" class="bg-[#F2F2F4] w-full h-[37px] rounded-[10px] py-[8px] px-[16px]" />
                        </div>
                    </div>
                <div class="flex flex-col w-full lg:w-[338px] gap-[15px]">
                    <div class="flex flex-col gap-[5px]">
                        <p class="font-poppins HeadLineMedium text-[#181818]">Mot de passe*</p>
                        <input value={password()} type="password" placeholder="*******" class="bg-[#F2F2F4] w-full h-[37px] rounded-[10px] py-[8px] px-[16px]" />
                    </div>
                    <div class="flex flex-col gap-[5px]">
                        <p class="font-poppins HeadLineMedium text-[#181818]">Role*</p>
                        <div class="relative">
                            <select value={role()} name="roles" id="roles" class="bg-[#F2F2F4] w-full h-[37px] rounded-[10px] px-[16px] appearance-none">
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
                            <input checked={accountState() ? "checked" : ""} type="checkbox" class="w-[14px] h-auto bg-white border-2" />
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
                    <button class="w-[123px] h-auto rounded-[50px] px-[16px] py-[8px] gap-[10px] bg-[#F2F2F4]">
                        <p class="w-[91px] h-auto font-poppins font-[600] text-[14px] leading-[21px] tracking-[0%]">Mettre à jour</p>
                    </button>
                </div>
            </div>
        </div>
    )
}

export default EditAccountModal