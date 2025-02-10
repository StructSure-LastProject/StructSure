import StructureDetailNote from './StructureDetailNote';
import StructureDetailCapteurs from './StructureDetailCapteurs';


function StructureDetailRow() {
    
    return (
        <div class="flex gap-x-[50px] w-full">
            <StructureDetailNote />
            <StructureDetailCapteurs />
        </div>
    );
}

export default StructureDetailRow

