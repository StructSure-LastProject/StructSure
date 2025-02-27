import { createSignal } from "solid-js";

/**
 * Structure name card component
 * @param {Function} toggle Function that will toggle the access
 * @param {String} structureId The structure id
 * @param {string} structureName The structure name
 * @param {boolean} isSelected If the structure is selected or not 
 * @returns 
 */
const StructureNameCard = ({toggle, structureId, structureName, isSelected}) => {

    const [isChoosed, setIsChoosed] = createSignal(isSelected);

    /**
     * The toogle event
     * @param {Event} e The click event
     */
    const handleClick = (e) => {
        e.preventDefault();
        setIsChoosed((prevState) => !prevState);
        toggle(structureId)
    };

    return (
        <button onClick={handleClick} class={`items-center w-auto sm:w-[auto] rounded-[50px] px-[12px] py-[4px] gap-[8px] ${isChoosed() ? "bg-black" : "bg-lightgray" }`}>
            <p class={`${isChoosed() ? "text-white" : "text-black" } accent`}>
                {structureName}
            </p>
        </button>

    )
}


export default StructureNameCard