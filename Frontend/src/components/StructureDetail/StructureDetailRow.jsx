import StructureDetailNote from './StructureDetailNote';
import StructureDetailCapteurs from './StructureDetailCapteurs';

/**
 * Show the structre detail row part
 * @returns the component for the strucutre detail row
 */
function StructureDetailRow({sensors, sensorsFetchRequest, structureId}) {
    return (
        <div class="flex lg:flex-row flex-col gap-y-[50px] lg:gap-x-[50px] w-full">
            <StructureDetailNote />
            <StructureDetailCapteurs
              sensors={sensors}
              sensorsFetchRequest={sensorsFetchRequest}
              structureId={structureId}
            />
        </div>
    );
}

export default StructureDetailRow

