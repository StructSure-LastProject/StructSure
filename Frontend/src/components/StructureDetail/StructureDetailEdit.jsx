import {createEffect, createSignal, onCleanup, onMount, Show} from "solid-js";
import useFetch from "../../hooks/useFetch";
import { useNavigate } from "@solidjs/router";

/**
 * Show the modal to edit structure
 */
function StructureDetailEdit(props) {
    const navigate = useNavigate();
    let modalRef;

    /**
     * Will send request to edit the structure details (name, note)
     */
    const structuresUpdateRequest = async () => {
        const url = `/api/structures/${props.structureDetails().id}`;
    
        const requestData = {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                name: name(),
                note: note()
            })
        };
    
        const { fetchData, statusCode, data, error } = useFetch();
        await fetchData(navigate, url, requestData);
    
        if (statusCode() === 201) {
            props.closeModal();
            props.setStructureDetails({
                ...props.structureDetails(),
                name: name(),
                note: note()
            });
            props.setNote(note());
            setErrorFrontend("");
        } else {
            setErrorFrontend(error()?.errorData.error || "Une erreur est survenue");
        }
    };

    /**
     * Handles the form submit
     * @param {Event} e The event of form submit 
     */
    const handleFormSubmit = (e) => {
        e.preventDefault();
        if (name().length <= 0) {
            setErrorFrontend("Le nom d'un ouvrage ne peut pas être vide");
        } else if (name().length > 64) {
            setErrorFrontend("Le nom d'un ouvrage ne peut pas dépasser 64 caractères");
        } else if (note().length > 1000) {
            setErrorFrontend("La note d'un ouvrage ne peut pas dépasser 1000 caractères");
        } else {
            structuresUpdateRequest();
        }
    };

    const [errorFrontend, setErrorFrontend] = createSignal("");


    const [name, setName] = createSignal("");

    const [note, setNote] = createSignal("");

    createEffect(() => {
        setName(props.structureDetails().name);
        setNote(props.structureDetails().note);
    });

    /**
     * Handles the close of the modal when click outside
     * @param {Event} event 
     */
    const handleClickOutside = (event) => {
        if (modalRef && !modalRef.contains(event.target)) {
            setErrorFrontend("");
            props.closeModal();
        }
    };

    onMount(() => {
        document.addEventListener("mousedown", handleClickOutside);
    });

    onCleanup(() => {
        document.removeEventListener("mousedown", handleClickOutside);
    });
    
    return (
        <Show when={props.isModalVisible() === true}>
            <div class="fixed z-50 inset-0 flex justify-center items-center bg-black/50 backdrop-blur-[10px]">
                <form ref={modalRef} class="bg-white p-25px rounded-20px w-388px flex flex-col gap-y-15px" 
                onSubmit={handleFormSubmit}>
                    <h1 class="title">Modifier l’ouvrage</h1>
                    <p class="text-sm text-red">{errorFrontend}</p>
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
                        onClick={()=>{
                            props.closeModal();
                            setErrorFrontend("");
                        }}>
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