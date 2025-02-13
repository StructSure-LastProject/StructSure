import { useParams } from '@solidjs/router';
import Header from '../components/Header';
import StructureDetailBody from '../components/StructureDetail/StructureDetailBody';


/**
 * Prints the detail of structre
 * @returns The component of structure detail
 */
function StructSureDetail() {
    const params = useParams();

    return (
        <div class="flex flex-col gap-y-35px p-25px bg-gray-100">
            <Header />
            <StructureDetailBody structureId={params.structureId} />
        </div>
    );
}

export default StructSureDetail
