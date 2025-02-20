/**
 * Returns the sensor color corresponding for its state
 * @param {Object} state the sensor state
 * @returns The corresponding color
 */
const getSensorStatusColor = (state) => {
    let colorsClasses = "";
    switch(state) {
        case "OK":
            colorsClasses = "bg-[#25B61F] border-green-200";
            break;
        case "NOK":
            colorsClasses = "bg-[#F13327] border-red-200";
            break;
        case "UNKNOWN":
            colorsClasses = "bg-[#6A6A6A] border-grey-200";
            break;
        case "DEFECTIVE":
            colorsClasses = "bg-[#F19327] border-yellow-200";
            break;
        default:
            break;
    }
    return colorsClasses;
};

export default getSensorStatusColor;