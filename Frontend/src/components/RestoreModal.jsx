import { Show } from "solid-js";
import useFetch from '../hooks/useFetch';
import {useNavigate} from "@solidjs/router";
import {FolderSync} from "lucide-solid";

/**
 * Modal component for restoring archived structures
 * @param {Object} props Component properties
 * @param {boolean} props.show Whether to show the modal or not
 * @param {Object} props.structure The structure to restore
 * @param {Function} props.onClose Function to call when closing the modal
 * @param {Function} props.onRestore Function to call after successful restoration
 * @param {Function} props.navigate Navigation function from useNavigate
 * @returns {JSX.Element} The restore modal component
 */
function RestoreModal(props) {
  const { fetchData, statusCode, data, error } = useFetch();
  const navigate = useNavigate();

  /**
   * Handles the restoration of an archived structure
   */
  const handleRestore = async () => {
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
      `/api/structures/${props.structure.id}/restore`,
      requestData
    );

    if (statusCode() === 200) {
      props.onRestore && props.onRestore(data());
    } else {

    }
  };

  return (
    <Show when={props.show}>
      <div class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-[10px]">
        <div class="bg-white p-6 rounded-[20px] shadow-lg w-[370px]">
          <h2 class="text-2xl font-bold mb-4">Restauration</h2>
          <p class="mb-6">
            Souhaitez vous restaurer l'ouvrage <span class="font-bold">{props.structure?.name}</span> ?
          </p>
          <div class="flex justify-between gap-4">
            <button
              class="px-[16px] py-[8px] bg-[#F6F6F6] text-black rounded-[50px] text-center flex-1"
              onclick={props.onClose}
            >
              Annuler
            </button>
            <button
              class="px-[16px] py-[8px] bg-black text-white rounded-[50px] flex items-center justify-center gap-2 flex-1"
              onclick={handleRestore}
            >
              Restaurer
              <FolderSync color="white" size={20} />
            </button>
          </div>
        </div>
      </div>
    </Show>
  );
}

export default RestoreModal;