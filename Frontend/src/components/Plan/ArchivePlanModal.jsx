import {useNavigate} from "@solidjs/router";
import {Trash2} from "lucide-solid";
import { onCleanup, onMount } from 'solid-js';
import useFetch from "../../hooks/useFetch.js";
import ErrorMessage from "../Modal/ErrorMessage.jsx";

/**
 * Modal component for archiving plan
 * @param {Object} props Component properties
 * @returns {JSX.Element} The plan modal component
 */
function ArchivePlanModal(props) {
  const { fetchData, statusCode, data, error } = useFetch();
  const navigate = useNavigate();

  let modalRef;

  /**
   * Handles the close of the modal when click outside
   * @param {Event} event
   */
  const handleClickOutside = (event) => {
    if (modalRef && !modalRef.contains(event.target)) {
      props.onCloseArchive();
    }
  };

  onMount(() => {
    document.addEventListener("mousedown", handleClickOutside);
    document.body.style.overflow = 'hidden';
  });

  onCleanup(() => {
    document.removeEventListener("mousedown", handleClickOutside);
    document.body.style.overflow = 'auto';
  });

  /**
   * Handles the archive of a plan
   */
  const handleArchive = async () => {
    if (!props.planId || !props.structureId) return;

    const token = localStorage.getItem("token");
    const requestData = {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${token}`
      }
    };

    await fetchData(
      navigate,
      `/api/structures/${props.structureId}/plans/${props.planId}/archive`,
      requestData
    );

    if (statusCode() === 200) {
      props.onArchive && props.onArchive(data()?.id);
    } else if (statusCode() === 422 || statusCode() === 404) {
      props.setErrorMsgArchivePlan(error()?.errorData.error || "Une erreur est survenue");
    } else {
      props.setErrorMsgArchivePlan("Une erreur est survenue");
    }
  };

  return (
    <div class="fixed inset-0 z-[60] flex items-center justify-center bg-black/50 backdrop-blur-[10px]">
      <div ref={modalRef} class="bg-white p-6 rounded-[20px] shadow-lg w-[370px]">
        <h2 class="title mb-4">Archivage</h2>
        <ErrorMessage message={props.errorMsgArchivePlan}></ErrorMessage>
        <p class="mb-6 normal">
          Souhaitez vous archiver le plan <span class="font-bold">{props.plan?.name}</span> ?
        </p>
        <div class="flex justify-between gap-4">
          <button
            class="px-[16px] py-[8px] bg-lightgray accent text-black rounded-[50px] text-center flex-1"
            onclick={props.onCloseArchive}
          >
            Annuler
          </button>
          <button
            class="px-[16px] py-[8px] bg-red accent text-white rounded-[50px] flex items-center justify-center gap-2 flex-1"
            onclick={handleArchive}
          >
            Archiver
            <Trash2 color="white" size={20} />
          </button>
        </div>
      </div>
    </div>
  );
}

export default ArchivePlanModal;