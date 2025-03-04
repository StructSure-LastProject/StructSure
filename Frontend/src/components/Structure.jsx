import LstStructureHead from './LstStructureHead';
import StructureBody from './StructSureBody';


/**
 * Structre component 
 * @returns component for the Strucuture
 */
function StructSure() {

    return (
        <div class="flex flex-col justify-center max-w-[1250px] mx-auto mt-10 gap-y-15px">
            <StructureBody />
        </div>
    );
}

export default StructSure
