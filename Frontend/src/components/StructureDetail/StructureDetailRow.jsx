import StructureDetailNote from './StructureDetailNote';
import StructureDetailCapteurs from './StructureDetailCapteurs';

/**
 * Show the structure detail row part
 * @param {function} selectedScanId The selected scan id
 * @param {function} offset The offset of sensors pagination
 * @param {function} setOffset Set the offset of sensors pagination
 * @param {function} limit Get limit of sensors pagination
 * @param {function} setLimit Set limit of sensors pagination
 * @param {function} scanChanged If scan changed
 * @param {Function} note The structure/scan note
 * @param {String} structureId The structure id
 * @param {Function} setSensors The set sonsors function
 * @param {function} selectedPlanId The selected plan id
 * @param {Array} sensors The sensors array
 * @param {Number} totalItems The total number of sensors
 * @param {Function} setTotalItems The setter function
 * @returns the component for the strucutre detail row
 */
function StructureDetailRow({selectedScanId, offset, setOffset, limit, setLimit, scanChanged, note, structureId, setSensors, selectedPlanId, sensors, totalItems, setTotalItems}) {
    return (
        <div class="flex lg:flex-row flex-col gap-y-[50px] lg:gap-x-[50px] w-full">
            <StructureDetailNote
              note={note}
            />
            <StructureDetailCapteurs
              selectedScanId={selectedScanId}
              offset={offset}
              setOffset={setOffset}
              limit={limit}
              setLimit={setLimit}
              scanChanged={scanChanged}
              structureId={structureId}
              setSensors={setSensors}
              selectedPlanId={selectedPlanId}
              sensors={sensors}
              totalItems={totalItems}
              setTotalItems={setTotalItems}
            />
        </div>
    );
}

export default StructureDetailRow

