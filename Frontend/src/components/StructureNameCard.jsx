
/**
 * Structure name card component
 * @param {string} structureName The structure name
 * @param {boolean} isChoosed If the structure is choosed or not 
 * @returns 
 */
const StructureNameCard = ({structureName, isChoosed}) => {

    const cardColor = isChoosed ? {
        bg: "bg-[#181818]",
        text: "text-[#FFFFFF]" 
    }: {
        bg: "bg-[#F2F2F4]",
        text: "text-[#181818]"
    };
    
    return (
        <div class={`items-center w-auto sm:w-[auto] rounded-[50px] px-[12px] py-[4px] gap-[8px] ${cardColor.bg}`}>
            <p class={`${cardColor.text} font-poppins font-[600] text-[14px] leading-[18px] sm:leading-[21px] tracking-[0%]`}>
                {structureName}
            </p>
        </div>

    )
}


export default StructureNameCard