import { createSignal } from "solid-js";
import order from '/src/assets/order.svg';
import filter from '/src/assets/filter.svg';
import add from '/src/assets/add.svg';
import { useNavigate } from "@solidjs/router";
import useFetch from '../hooks/useFetch';

function LstStructureHead() {
    const [isModalVisible, setModalVisible] = createSignal(false);

    const openModal = () => setModalVisible(true);

    const closeModal = () => setModalVisible(false);

    const [error, setError] = createSignal("");

    const [name, setName] = createSignal("");

    const [note, setNote] = createSignal("");

    const navigate = useNavigate();

    const handleFormSubmit = (e) => {
        e.preventDefault();
        structuresFetchRequest("/api/structures");
    };

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

        const { fetchData, statusCode, error } = useFetch();
        await fetchData(url, requestData);
        
        if (statusCode() === 201) {
            location.reload();
        } else if (statusCode() === 401) {
            console.log("not autorized");
            navigate("/login");
        } else {
            console.log("Error occurred, status : ", statusCode());
            setError(error());
        }
    };

    return (
        <div class="flex justify-between w-full">
            <h1 class="text-2xl font-medium">Ouvrages</h1>
            <div class="flex space-x-4">
                <img src={order} alt="Order Elements logo" />
                <img src={filter} alt="Filter logo" />
                <img
                    src={add}
                    alt="Add Button logo"
                    class="cursor-pointer"
                    onClick={openModal}
                />
            </div>

            {isModalVisible() && (
                <div class="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center">
                    <form class="bg-white p-25px rounded-20px w-388px flex flex-col gap-y-15px" 
                    onSubmit={handleFormSubmit}>
                        <h1 class="text-2xl font-medium">Ajouter un Ouvrage</h1>
                        <p class="text-sm text-red-500">{error}</p>
                        <div class="flex flex-col justify-start gap-y-15px">
                            <div class="flex flex-col gap-y-5px">
                                <p class="text-sm text-gray-700">Nom*</p>
                                <input 
                                    id="name" 
                                    type="text" 
                                    placeholder="Ajouter un nom" 
                                    class="w-full pb-2 pt-2 pl-4 pr-4 text-sm rounded-10px bg-gray-200 h-37px"
                                    onChange={(e) => setName(e.target.value)}
                                />
                            </div>

                            <div class="flex flex-col gap-y-5px">
                                <p class="text-sm text-gray-700">Note</p>
                                <input 
                                    id="note" 
                                    type="text" 
                                    placeholder="Ajouter une note" 
                                    class="w-full pb-2 pt-2 pl-4 pr-4 text-sm rounded-10px bg-gray-200 h-37px"
                                    onChange={(e) => setNote(e.target.value)}
                                />
                            </div>
                        </div>

                        <div class="flex justify-end justify-between">
                            <button class="w-161px bg-gray-200 rounded-50px pb-2 pt-2 pl-4 pr-4 h-9" 
                            onClick={closeModal}>
                                <p class="text-sm text-black font-medium">Annuler</p>
                            </button>
                            <button type="submit" class="w-161px bg-black rounded-50px pb-2 pt-2 pl-4 pr-4 h-9" >
                                <p class="text-sm text-white font-medium">Créer</p>
                            </button>
                        </div>
                    </form>
                </div>
            )}
        </div>
    );
}

export default LstStructureHead;
