import add from '/src/assets/add.svg';
import AccountDetails from './AccountDetails';
import useFetch from "../hooks/useFetch";
import { createResource, createSignal } from 'solid-js';
import AddAccountModal from "./AdminPanel/AddAccountModal";

/**
 * The admin panel body component 
 * @returns the component for the admin panel
 */
const AdminPanelBody = () => {

    const [users, setUsers] = createSignal([]);
    const [isAddModalOpen, setIsAddModalOpen] = createSignal(false);

    /**
     * Handle the add account click event by opening the modal
     */
    const handleAddAccountClick = () => {        
        setIsAddModalOpen(true); 
    };

     /**
     * Close the add account modal
     */
     const closeAddAccountModal = () => {
        setIsAddModalOpen(false); 
    };

    /**
     * Fetch user details
     */
    const fetchUserDetails = async () => {
        const token = localStorage.getItem("token");
        
        const requestData = {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            }
        };
        
        const { fetchData, data, statusCode } = useFetch();
        await fetchData("/api/accounts", requestData);
        
        if (statusCode() === 200) {
            setUsers(data());            
        }
        
    }

    createResource(() => fetchUserDetails());

    return (
        <>             
            <button onClick={handleAddAccountClick} class="flex justify-between items-center w-full max-w-[1250px] h-[40px] sm:h-[50px] rounded-[20px] pl-[20px] gap-[10px]">
                <h1 class="text-2xl sm:text-3xl font-poppins title">Comptes</h1>
                <div class="pr-[5%]">
                    <img
                        src={add}
                        alt="Add Button logo"
                        class="cursor-pointer w-[40px] h-auto rounded-[50px]"
                    />
                </div>
            </button>

            <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4 sm:px-4 lg:px-10">
                <For each={users()}>
                    {
                        (item) => (
                            <AccountDetails firstName={item.firstName} lastName={item.lastName} login={item.login} role={item.role} isEnabled={item.enabled} />
                        )
                    }
                </For>
            </div>
            {isAddModalOpen() && (
                <AddAccountModal closeModal={closeAddAccountModal} />
            )}
        </>
    )
}

export default AdminPanelBody