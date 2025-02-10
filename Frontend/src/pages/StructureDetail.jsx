import Header from '../components/Header';
import StructureDetailBody from '../components/StructureDetailBody';


/**
 * Prints the detail of structre
 * @returns The component of structure detail
 */
function StructSureDetail() {

    return (
        <div class="flex flex-col gap-y-35px p-25px bg-gray-100">
            <Header />
            <StructureDetailBody />
        </div>
    );
}

export default StructSureDetail
