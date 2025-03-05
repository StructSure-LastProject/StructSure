import { Trash2 } from 'lucide-solid';
import useFetch from '../../hooks/useFetch';

/**
 * Confirmation delete user account modal
 * @param {Function} navigate The navigate function to change page 
 * @param {Function} closeModal The close modal function to close the user account edit modal
 * @param {Function} fetchUserDetails The function to fecth user details
 * @param {String} userLogin The user login
 * @param {Function} closeConfirmationModal The function to close the confirmation modal
 * @returns The confirmation modal component
 */
const ConfirmationDeleteModal = ({
    navigate,
    closeModalEditModal,
    fetchUserDetails,
    userLogin,
    closeConfirmationModal
}) => {

    const { fetchData, statusCode } = useFetch();
    const token = localStorage.getItem("token");
    
    /**
     * Delete the user account
    */
   const deleteUserAccount = async () => {
    
        await fetchData(navigate, `/api/api/accounts/${userLogin}/anonymize`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            }
        });
        
        
        if (statusCode() === 200) {
            fetchUserDetails();
            closeModalEditModal();
            closeConfirmationModal();
        }
    }
    

    return (
    <div class="flex justify-center items-center min-w-full min-h-screen">
        <div class="min-w-[130px] max-w-[377px] min-h-[197px] rounded-[20px] p-[25px] flex flex-col gap-[15px] bg-[#FFFFFF] shadow-[0px 0px 50px 0px #33333340]">
            <h1 class="title">Archiver le compte utilisateur</h1>
            <p class="normal">Souhaitez-vous archiver le compte utilisateur <span class="font-bold">{userLogin}</span> ?</p>
            <div class="flex flex-wrap justify-center gap-[10px]">
                <button onClick={closeConfirmationModal} class="bg-[#F2F2F4] min-w-[150px] min-h-[37px] rounded-[50px] px-[16px] py-[8px] font-[600]">Annuler</button>
                <button onClick={deleteUserAccount} class="flex gap-[10px] justify-between items-center bg-[#F13327] min-w-[150px] min-h-[37px] rounded-[50px] px-[16px] py-[8px] text-[#FFFFFF] font-[600]">
                    Archiver
                    <Trash2 color="#FFFFFF" size={20} width={20} top={10} left={10}/>
                </button>
            </div>
        </div>
    </div>
    )
}

export default ConfirmationDeleteModal