import {useNavigate} from "@solidjs/router";
import {FolderSync} from "lucide-solid";
import { onCleanup, onMount } from 'solid-js';
import useFetch from "../../hooks/useFetch.js";
import ErrorMessage from "../Modal/ErrorMessage.jsx";

/**
 * Modal component for restoring plan
 * @param {Object} props Component properties
 * @returns {JSX.Element} The plan modal component
 */
function RestorePlanModal(props) {
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
   * Handles the restoration of an archived plan
   */
  const handleRestore = async () => {
    if (!props.planId || !props.structureId) return;

    const token = localStorage.getItem("token");
    const requestData = {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${token}`
      }
    };
    console.log("props", props);
    await fetchData(
      navigate,
      `/api/structures/${props.structureId}/plans/${props.planId}/restore`,
      requestData
    );

    if (statusCode() === 200) {
      props.onRestore && props.onRestore(data());
    } else if (statusCode() === 422 || statusCode() === 404) {
      props.setErrorMsgRestorePlan(error()?.errorData.error);
    } else {
      props.setErrorMsgRestorePlan("Une erreur est survenue");
    }
  };

  return (
    <div class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-[10px]">
      <div ref={modalRef} class="bg-white p-6 rounded-[20px] shadow-lg w-[370px]">
        <h2 class="title mb-4">Restauration</h2>
        <ErrorMessage message={props.errorMsgRestorePlan}></ErrorMessage>
        <p class="mb-6 normal">
          Souhaitez vous restaurer le plan <span class="font-bold">{props.plan?.name}</span> ?
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

export default RestorePlanModal;