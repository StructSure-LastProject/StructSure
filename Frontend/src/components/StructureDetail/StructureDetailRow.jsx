import StructureDetailNote from './StructureDetailNote';
import StructureDetailCapteurs from './StructureDetailCapteurs';

/**
 * Show the structre detail row part
 * @returns the component for the strucutre detail row
 */
function StructureDetailRow({planSensors, selectedPlanId, sensors}) {
    return (
        <div class="flex lg:flex-row flex-col gap-y-[50px] lg:gap-x-[50px] w-full">
            <StructureDetailNote />
            <StructureDetailCapteurs planSensors={planSensors} selectedPlanId={selectedPlanId} sensors={sensors} />
        </div>
    );
}

export default StructureDetailRow

