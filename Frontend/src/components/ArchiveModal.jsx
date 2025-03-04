import useFetch from '../hooks/useFetch';
import {useNavigate} from "@solidjs/router";
import {FolderSync, Shield, Trash2} from "lucide-solid";
import ErrorMessage from "./Modal/ErrorMessage.jsx";

/**
 * Modal component for archiving structures
 * @param {Object} props Component properties
 * @returns {JSX.Element} The archive modal component
 */
function ArchiveModal(props) {
  const { fetchData, statusCode, data, error } = useFetch();
  const navigate = useNavigate();

  /**
   * Handles the archive of a structure
   */
  const handleArchive = async () => {
    if (!props.structure) return;

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
      `/api/structures/${props.structure.id}/archive`,
      requestData
    );

    if (statusCode() === 200) {
      props.onArchive && props.onArchive(data());
    } else if (statusCode() === 422) {
      props.setErrorMsgActiveStructure(error().errorData.error);
    } else {
      props.setErrorMsgActiveStructure("Une erreur est survenue");
    }
  };

  return (
    <div class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-[10px]">
      <div class="bg-white p-6 rounded-[20px] shadow-lg w-[370px]">
        <h2 class="title mb-4">Archivage</h2>
        <ErrorMessage message={props.errorMsgArchiveStructure}></ErrorMessage>
        <p class="mb-6 normal">
          Souhaitez vous archiver l&apos;ouvrage <span class="font-bold">{props.structure?.name}</span> ?
        </p>
        <div class="flex justify-between gap-4">
          <button
            class="px-[16px] py-[8px] bg-lightgray accent text-black rounded-[50px] text-center flex-1"
            onclick={props.onClose}
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

export default ArchiveModal;