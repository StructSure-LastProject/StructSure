import StructureDetailNote from './StructureDetailNote';
import StructureDetailCapteurs from './StructureDetailCapteurs';

/**
 * Show the structre detail row part
 * @param {String} structureId The structure id
 * @param {Function} setSensors The set sonsors function
 * @param {String} selectedPlanId The selected plan id
 * @param {Array} sensors The sensors array
 * @returns the component for the strucutre detail row
 */
function StructureDetailRow({structureId, setSensors, selectedPlanId, sensors}) {
    return (
        <div class="flex lg:flex-row flex-col gap-y-[50px] lg:gap-x-[50px] w-full">
            <StructureDetailNote />
            <StructureDetailCapteurs structureId={structureId} setSensors={setSensors} selectedPlanId={selectedPlanId} sensors={sensors} />
        </div>
    );
}

export default StructureDetailRow

