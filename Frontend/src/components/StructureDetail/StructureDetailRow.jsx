import StructureDetailNote from './StructureDetailNote';
import StructureDetailCapteurs from './StructureDetailCapteurs';

/**
 * Show the structre detail row part
 * @param {function} note The note of the structure/scan
 * @param {String} structureId The structure id
 * @param {Function} setSensors The set sonsors function
 * @param {String} selectedPlanId The selected plan id
 * @param {Array} sensors The sensors array
 * @param {Number} totalItems The total number of sensors
 * @param {Function} setTotalItems The setter function
 * @param {function} selectedScan The selected scan
 * @param {Function} structureDetails The structure detail
 * @param {Function} setSensorsDetail setter to set the sensors in structureDetails state
 * @returns the component for the strucutre detail row
 */
function StructureDetailRow({note, structureId, setSensors, selectedScan, selectedPlanId, sensors, totalItems, setTotalItems, structureDetails, setPlanSensors, setSensorsDetail}) {
    return (
        <div class="flex lg:flex-row flex-col gap-y-[50px] lg:gap-x-[50px] w-full">
            <StructureDetailNote
              note={note}
              selectedScan={selectedScan}
              structureDetails={structureDetails}
            />
            <StructureDetailCapteurs
              structureId={structureId}
              setSensors={setSensors}
              selectedScan={selectedScan}
              selectedPlanId={selectedPlanId}
              sensors={sensors}
              totalItems={totalItems}
              setTotalItems={setTotalItems}
              setPlanSensors={setPlanSensors}
              setSensorsDetail={setSensorsDetail}
              structureDetails={structureDetails}
            />
        </div>
    );
}

export default StructureDetailRow

