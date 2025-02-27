import { createEffect, createSignal } from "solid-js";
import useFetch from "../../hooks/useFetch";

/**
 * Show the modal to edit structure
 */
function StructureDetailEdit(props) {

    /**
     * Will send request to edit the structure details (name, note)
     */
    const structuresUpdateRequest = async () => {
        const token = localStorage.getItem("token");       
        const url = `/api/structures/${props.structureDetails().id}`;
    
        const requestData = {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            },
            body: JSON.stringify({
                name: name(),
                note: note()
            })
        };
    
        const { fetchData, statusCode, data, error } = useFetch();
        await fetchData(url, requestData);
    
        if (statusCode() === 201) {
            props.closeModal();
            props.setStructureDetails({
                ...props.structureDetails(),
                name: name(),
                note: note()
            });
        } else {
            setErrorFronted(error().errorData.error);
        }
    };

    /**
     * Handles the form submit
     * @param {Event} e The event of form submit 
     */
    const handleFormSubmit = (e) => {
        e.preventDefault();
        if (name().length > 64) {
            setErrorFronted("Le nom d'un ouvrage ne peut pas dépasser 64 caractères");
        } else if (note().length > 1000) {
            setErrorFronted("La note d'un ouvrage ne peut pas dépasser 1000 caractères");
        } else {
            structuresUpdateRequest();
        }
    };

    const [errorFronted, setErrorFronted] = createSignal("");


    const [name, setName] = createSignal("");

    const [note, setNote] = createSignal("");

    createEffect(() => {
        setName(props.structureDetails().name);
        setNote(props.structureDetails().note);
    });

    
    

    
    return (
        <Show when={props.isModalVisible() === true}>
            <div class="fixed z-50 inset-0 bg-black bg-opacity-50 flex justify-center items-center">
                <form class="bg-white p-25px rounded-20px w-388px flex flex-col gap-y-15px" 
                onSubmit={handleFormSubmit}>
                    <h1 class="title">Modifier l’ouvrage</h1>
                    <p class="text-sm text-red">{errorFronted}</p>
                    <div class="flex flex-col justify-start gap-y-15px">
                        <div class="flex flex-col gap-y-5px">
                            <p class="normal opacity-75">Nom*</p>
                            <input 
                                id="name" 
                                type="text" 
                                value={props.structureDetails().name} 
                                class="w-full pb-2 pt-2 pl-4 pr-4 normal rounded-10px bg-lightgray h-37px"
                                onChange={(e) => setName(e.target.value)}
                                maxlength="64"
                            />
                        </div>

                        <div class="flex flex-col gap-y-5px">
                            <p class="normal opacity-75">Note</p>
                            <textarea 
                                id="note" 
                                type="text" 
                                placeholder="Ajouter une note"
                                value={props.structureDetails().note}
                                class="w-full pb-2 pt-2 pl-4 pr-4 normal rounded-10px bg-lightgray h-20"
                                onChange={(e) => setNote(e.target.value)}
                                maxlength="1000"
                            />
                        </div>
                    </div>

                    <div class="flex justify-end justify-between">
                        <button class="w-161px bg-lightgray rounded-50px pb-2 pt-2 pl-4 pr-4 h-9" 
                        onClick={props.closeModal}>
                            <p class="accent">Annuler</p>
                        </button>
                        <button type="submit" class="w-161px bg-black rounded-50px pb-2 pt-2 pl-4 pr-4 h-9" >
                            <p class="text-white accent">Mettre à jour</p>
                        </button>
                    </div>
                </form>
            </div>
        </Show>
    );
}

export default StructureDetailEdit