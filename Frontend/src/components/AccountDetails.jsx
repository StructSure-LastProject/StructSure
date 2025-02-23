import { createSignal } from "solid-js";
import EditAccountModal from "./AdminPanel/EditAccountModal";

/**
 * Display the Account details
 * @param {String} firstName the firstname
 * @param {String} lastName the lastName
 * @param {String} login the login
 * @param {String} role role of the user 
 * @returns the component for the account details
 */
const AccountDetails = ({fetchUserDetails ,firstName, lastName, login, role, isEnabled}) => {

    const [isEditModalOpen, setIsEditModalOpen] = createSignal(false);
    
    /**
     * Handle the edit account click event by opening the modal
     */
    const handleEditAccountClick = () => {
        if (localStorage.getItem("login") === "StructSureAdmin" && login === "StructSureAdmin") {
            return;
        }
        if (role === "Admin" && localStorage.getItem("login") !== "StructSureAdmin") {
            return;
        }
        setIsEditModalOpen(true); 
    };


    /**
     * Close the edit account modal
     */
    const closeEditAccountModal = () => {
        setIsEditModalOpen(false); 
    };


    const roleStyles = {
        "Opérateur": {
            text: "text-[#25B61F] w-[74px] h-auto flex items-center justify-center font-poppins user-role",
            bg: "bg-[#25B61F1A] py-[2px] px-[10px] w-[94px] h-auto rounded-[20px]"
        },
        "Admin": {
            text: "text-[#F13327] w-[48px] h-auto flex items-center justify-center font-poppins user-role",
            bg: "bg-[#F133271A] py-[2px] px-[10px] w-[68px] h-auto rounded-[20px]"
        },
        "Responsable": {
            text: "text-[#F19327] w-[93px] h-auto flex items-center justify-center font-poppins user-role",
            bg: "bg-[#F193271A] py-[2px] px-[10px] w-[113px] h-auto rounded-[20px]"
        }
    };
    
    const bgColorText = roleStyles[role]?.text || "";
    const bgColor = roleStyles[role]?.bg || "";
    
    return (
        <div>
            <button onClick={handleEditAccountClick} class={`${!isEnabled ? "opacity-50" : ""}  
                ${(role === "Admin" && localStorage.getItem("login") !== "StructSureAdmin") || (login === "StructSureAdmin" && localStorage.getItem("login") === "StructSureAdmin") ? "cursor-not-allowed" : ""} flex justify-between items-center py-[10px] px-[25px] bg-white rounded-[20px] w-full h-auto`}>
                <div class="flex flex-col text-start w-full sm:w-[200px] md:w-[219px] h-auto">
                    <h2 class="subtitle">{firstName} {lastName}</h2>
                    <span class="normal opacity-50 -mt-[5px]">{login}</span>
                </div>
                <div>
                    <div class="flex items-center">
                        <div class={`${bgColor}`}>
                            <p class={`${bgColorText}`}>
                                {role}
                            </p>
                        </div>
                    </div>
                </div>
            </button>
            {isEditModalOpen() && (
                <EditAccountModal
                    fetchUserDetails={fetchUserDetails} 
                    closeModal={closeEditAccountModal} 
                    userDetails={{
                        firstName: firstName,
                        lastName: lastName,
                        login: login,
                        role: role,
                        accountState: isEnabled
                    }}
                />
            )}
        </div>
    );
}

export default AccountDetails