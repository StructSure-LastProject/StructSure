import useFetch from '../hooks/useFetch';
import {useNavigate} from "@solidjs/router";
import {FolderSync, Shield} from "lucide-solid";
import ErrorMessage from "./Modal/ErrorMessage.jsx";
import { onCleanup, onMount } from 'solid-js';

/**
 * Modal component for restoring archived structures
 * @param {Object} props Component properties
 * @returns {JSX.Element} The restore modal component
 */
function RestoreModal(props) {
  const { fetchData, statusCode, data, error } = useFetch();
  const navigate = useNavigate();

  let modalRef;
  
  /**
   * Handles the close of the modal when click outside
   * @param {Event} event 
   */
  const handleClickOutside = (event) => {
      if (modalRef && !modalRef.contains(event.target)) {
          props.onClose();
      }
  };

  onMount(() => {
      document.addEventListener("mousedown", handleClickOutside);
  });

  onCleanup(() => {
      document.removeEventListener("mousedown", handleClickOutside);
  });

  /**
   * Handles the restoration of an archived structure
   */
  const handleRestore = async () => {
    if (!props.structure) return;

    const requestData = {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
      }
    };

    await fetchData(
      navigate,
      `/api/structures/${props.structure.id}/restore`,
      requestData
    );

    if (statusCode() === 200) {
      props.onRestore && props.onRestore(data());
    } else if (statusCode() === 422) {
      props.setErrorMsgActiveStructure(error()?.errorData.error || "Une erreur est survenue");
    } else {
      props.setErrorMsgActiveStructure("Une erreur est survenue");
    }
  };

  return (
    <div class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-[10px]">
      <div ref={modalRef} class="bg-white p-6 rounded-[20px] shadow-lg w-[370px]">
        <h2 class="title mb-4">Restauration</h2>
        <ErrorMessage message={props.errorMsgActiveStructure}></ErrorMessage>
        <p class="mb-6 normal">
          Souhaitez vous restaurer l&apos;ouvrage <span class="font-bold">{props.structure?.name}</span> ?
        </p>
        <div class="flex justify-between gap-4">
          <button
            class="px-[16px] py-[8px] accent bg-lightgray text-black rounded-[50px] text-center flex-1"
            onclick={props.onClose}
          >
            Annuler
          </button>
          <button
            class="px-[16px] py-[8px] accent bg-black text-white rounded-[50px] flex items-center justify-center gap-2 flex-1"
            onclick={handleRestore}
          >
            Restaurer
            <FolderSync color="white" size={20} />
          </button>
        </div>
      </div>
    </div>
  );
}

export default RestoreModal;