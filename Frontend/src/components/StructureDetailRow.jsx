import StructureDetailNote from './StructureDetailNote';
import StructureDetailCapteurs from './StructureDetailCapteurs';
import { JSX } from 'solid-js';


/**
 * Show the structre detail row part
 * @returns {JSX.Element} the component for the strucutre detail row
 */
function StructureDetailRow() {
    
    return (
        <div class="flex gap-x-[50px] w-full">
            <StructureDetailNote />
            <StructureDetailCapteurs />
        </div>
    );
}

export default StructureDetailRow

