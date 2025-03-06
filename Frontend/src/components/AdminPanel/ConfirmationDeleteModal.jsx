import { Trash2 } from 'lucide-solid';
import useFetch from '../../hooks/useFetch';
import { onCleanup, onMount } from 'solid-js';

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
    let modalRef;
        
    /**
     * Handles the close of the modal when click outside
     * @param {Event} event 
     */
    const handleClickOutside = (event) => {
        if (modalRef && !modalRef.contains(event.target)) {
            closeConfirmationModal();
        }
    };

    onMount(() => {
        document.addEventListener("mousedown", handleClickOutside);
    });

    onCleanup(() => {
        document.removeEventListener("mousedown", handleClickOutside);
    });
    
    /**
     * Delete the user account
    */
   const deleteUserAccount = async () => {
    
        await fetchData(navigate, `/api/api/accounts/${userLogin}/anonymize`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
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
        <div ref={modalRef} class="min-w-[130px] max-w-[377px] rounded-[20px] p-[25px] flex flex-col gap-[15px] bg-white shadow-[0px 0px 50px 0px #33333340]">
            <h1 class="title">Supprimer le compte utilisateur</h1>
            <p class="normal">Souhaitez-vous supprimer le compte utilisateur <span class="font-bold">{userLogin}</span> ?</p>
            <div class="flex justify-between gap-[10px]">
                <button onClick={closeConfirmationModal} class="w-full bg-lightgray rounded-full px-[16px] py-[8px] accent">Annuler</button>
                <button onClick={deleteUserAccount} class="w-full flex gap-[10px] justify-between items-center bg-red rounded-full px-[16px] py-[8px] text-white accent">
                    <p class="w-full text-center">Supprimer</p>
                    <Trash2 color="#FFFFFF" size={20} width={20} top={10} left={10}/>
                </button>
            </div>
        </div>
    </div>
    )
}

export default ConfirmationDeleteModal