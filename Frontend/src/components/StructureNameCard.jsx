import { createSignal } from "solid-js";

/**
 * Structure name card component
 * @param {string} structureName The structure name
 * @param {boolean} isChoosed If the structure is choosed or not 
 * @returns 
 */
const StructureNameCard = ({add, remove, structureId, structureName, isSelected}) => {

    const [isChoosed, setIsChoosed] = createSignal(isSelected);

    const handleClick = (e) => {
        e.preventDefault();
        setIsChoosed((prevState) => !prevState);

        if (isChoosed()) {
            add(structureId, true);
        } else {
            remove(structureId);
        }
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