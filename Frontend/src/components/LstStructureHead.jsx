import {createSignal} from "solid-js";
import { useNavigate } from "@solidjs/router";
import useFetch from '../hooks/useFetch';
import { ChevronDown, ChevronUp, Plus } from 'lucide-solid';

/**
 * Component for list of structure
 * @returns component for the list of structure
 */
function LstStructureHead({setFilterVisible, filterVisible}) {
    const [isModalVisible, setModalVisible] = createSignal(false);

    const openModal = () => setModalVisible(true);

    /**
     * Handles close modal
     */
    const closeModal = () => setModalVisible(false);

    const [error, setError] = createSignal("");

    const [name, setName] = createSignal("");

    const [note, setNote] = createSignal("");

    const navigate = useNavigate();

    /**
     * Toggle filter visibility
     */
    const toggleFilter = () => {
        setFilterVisible(!filterVisible());
    };

    /**
     * Handles the form submit
     * @param {Event} e The event of form submit
     */
    const handleFormSubmit = (e) => {
        e.preventDefault();
        structuresFetchRequest("/api/structures");
    };

    /**
     * Fetch the strucutres
     * @param {String} url the url of the server
     */
    const structuresFetchRequest = async (url) => {
        const token = localStorage.getItem("token");
        const requestBody = JSON.stringify({
            name: name(),
            note: note()
        });

        const requestData = {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            },
            body: requestBody
        };

        const { fetchData, statusCode, errorFetch } = useFetch();
        await fetchData(navigate, url, requestData);

        if (statusCode() === 201) {
            location.reload();
        } else {
            setError(errorFetch());
        }
    };

    return (
      <div class="flex justify-between w-full pl-5">
          <h1 class="title">Ouvrages</h1>
          <div class="flex gap-x-[10px]">
              <button
                class="w-10 h-10 bg-white rounded-[50px] flex items-center justify-center"
                onClick={toggleFilter}
              >
                  {filterVisible() ? <ChevronUp/> : <ChevronDown/>}
              </button>
              <button
                class="w-10 h-10 bg-black rounded-[50px] flex items-center justify-center"
                onClick={openModal}
              >
                  <Plus color="white" />
              </button>
          </div>

          {isModalVisible() && (
            <div class="fixed z-50 inset-0 bg-black bg-opacity-50 flex justify-center items-center">
                <form class="bg-white p-25px rounded-20px w-388px flex flex-col gap-y-15px"
                      onSubmit={handleFormSubmit}>
                    <h1 class="title">Ajouter un Ouvrage</h1>
                    <p class="text-sm text-red-500">{error}</p>
                    <div class="flex flex-col justify-start gap-y-15px">
                        <div class="flex flex-col gap-y-5px">
                            <p class="normal opacity-75">Nom*</p>
                            <input
                              id="name"
                              type="text"
                              placeholder="Ajouter un nom"
                              class="w-full pb-2 pt-2 pl-4 pr-4 normal rounded-10px bg-lightgray h-37px"
                              onChange={(e) => setName(e.target.value)}
                            />
                        </div>

                        <div class="flex flex-col gap-y-5px">
                            <p class="normal opacity-75">Note</p>
                            <input
                              id="note"
                              type="text"
                              placeholder="Ajouter une note"
                              class="w-full pb-2 pt-2 pl-4 pr-4 normal rounded-10px bg-lightgray h-37px"
                              onChange={(e) => setNote(e.target.value)}
                            />
                        </div>
                    </div>

                    <div class="flex justify-end justify-between">
                        <button class="w-161px bg-lightgray rounded-50px pb-2 pt-2 pl-4 pr-4 h-9"
                                onClick={closeModal}>
                            <p class="accent">Annuler</p>
                        </button>
                        <button type="submit" class="w-161px bg-black rounded-50px pb-2 pt-2 pl-4 pr-4 h-9" >
                            <p class="text-white accent">Créer</p>
                        </button>
                    </div>
                </form>
            </div>
          )}
      </div>
    );
}

export default LstStructureHead;