/**
 * Returns the sensor color corresponding for its state
 * @param {Object} state the sensor state
 * @returns The corresponding color
 */
const getSensorStatusColor = (state) => {
    let colorsClasses = "";
    switch(state) {
        case "OK":
            colorsClasses = "bg-[#25B51F] border-[#C8ECC7]";
            break;
        case "NOK":
            colorsClasses = "bg-[#F13327] border-[#FBCCC9]";
            break;
        case "UNKNOWN":
            colorsClasses = "bg-[#6A6A6A] border-[#DADADA]";
            break;
        case "DEFECTIVE":
            colorsClasses = "bg-[#F19327] border-[#FBE4C9]";
            break;
        default:
            break;
    }
    return colorsClasses;
};

export default getSensorStatusColor;