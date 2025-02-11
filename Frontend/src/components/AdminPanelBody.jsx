import add from '/src/assets/add.svg';
import AccountDetails from './AccountDetails';
import useFetch from "../hooks/useFetch";
import { createResource, createSignal } from 'solid-js';

/**
 * The admin panel body component 
 * @returns the component for the admin panel
 */
const AdminPanelBody = () => {

    const [users, setUsers] = createSignal([]);

    /**
     * Fetch user details
     */
    const fetchUserDetails = async () => {
        const { fetchData, data, statusCode } = useFetch();
        const token = localStorage.getItem("token");
    
        const requestData = {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            }
        };
    
        await fetchData("/api/accounts", requestData);
        
        if (statusCode() === 200) {
            setUsers(data());
        }
        
    }

    createResource(() => fetchUserDetails());

    return (
        <>
            <div class="flex justify-between items-center w-full max-w-[1250px] h-[40px] sm:h-[50px] rounded-[20px] pl-[20px] gap-[10px]">
                <h1 class="text-2xl sm:text-3xl font-poppins title">Comptes</h1>
                <div class="pr-[5%]">
                    <img
                        src={add}
                        alt="Add Button logo"
                        class="cursor-pointer w-[40px] h-auto rounded-[50px]"
                    />
                </div>
            </div>

            <div class="m-[2%] flex flex-wrap gap-[15px]">
                <For each={users()}>
                    {
                        (item) => (
                            <AccountDetails firstName={item.firstName} lastName={item.lastName} mail={item.mail} role={item.role}/>
                        )
                    }
                </For>
            </div>
        </>
    )
}

export default AdminPanelBody