
/**
 * Structure name card component
 * @param {string} structureName The structure name
 * @param {boolean} isChoosed If the structure is choosed or not 
 * @returns 
 */
const StructureNameCard = ({structureName, isChoosed}) => {

    const cardColor = isChoosed ? {
        bg: "bg-black",
        text: "text-white" 
    }: {
        bg: "bg-lightgray",
        text: "text-black"
    };
    
    return (
        <div class={`items-center w-auto sm:w-[auto] rounded-[50px] px-[12px] py-[4px] gap-[8px] ${cardColor.bg}`}>
            <p class={`${cardColor.text} accent`}>
                {structureName}
            </p>
        </div>

    )
}


export default StructureNameCard