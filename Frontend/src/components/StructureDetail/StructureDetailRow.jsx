import StructureDetailNote from './StructureDetailNote';
import StructureDetailCapteurs from './StructureDetailCapteurs';

/**
 * Show the structre detail row part
 * @param {String} structureId The structure id
 * @param {Function} setSensors The set sonsors function
 * @param {String} selectedPlanId The selected plan id
 * @param {Array} sensors The sensors array
 * @param {Number} totalItems The total number of sensors
 * @param {Function} setTotalItems The setter function
 * @returns the component for the strucutre detail row
 */
function StructureDetailRow({structureId, setSensors, selectedPlanId, sensors, totalItems, setTotalItems}) {
    return (
        <div class="flex lg:flex-row flex-col gap-y-[50px] lg:gap-x-[50px] w-full">
            <StructureDetailNote />
            <StructureDetailCapteurs structureId={structureId} setSensors={setSensors} selectedPlanId={selectedPlanId} sensors={sensors} totalItems={totalItems} setTotalItems={setTotalItems}/>
        </div>
    );
}

export default StructureDetailRow

