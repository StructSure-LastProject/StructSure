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
 * @param {Number} selectedScan The selected scan
 * @param {Function} structureDetails The structure detail
 * @returns the component for the strucutre detail row
 */
function StructureDetailRow({structureId, setSensors, selectedPlanId, sensors, totalItems, setTotalItems, selectedScan, structureDetails}) {
    return (
        <div class="flex lg:flex-row flex-col gap-y-[50px] lg:gap-x-[50px] w-full">
            <StructureDetailNote selectedScan={selectedScan} structureDetails={structureDetails} />
            <StructureDetailCapteurs structureId={structureId} setSensors={setSensors} selectedPlanId={selectedPlanId} sensors={sensors} totalItems={totalItems} setTotalItems={setTotalItems}/>
        </div>
    );
}

export default StructureDetailRow

