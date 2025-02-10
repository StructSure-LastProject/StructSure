import { JSX } from 'solid-js';
import StructureDetailHead from './StructureDetailHead';
import StructureDetailPlans from './StructureDetailPlans';
import StructureDetailRow from './StructureDetailRow';


/**
 * Shows the body part of the strcutre detail page
 * @returns {JSX.Element} component for the body part
 */
function StructureDetailBody() {
    
    return (
        
        <div class="flex flex-col gap-y-50px max-w-1250px mx-auto w-full">
            <StructureDetailHead />
            <StructureDetailPlans />
            <StructureDetailRow />
        </div>
    );
}

export default StructureDetailBody

