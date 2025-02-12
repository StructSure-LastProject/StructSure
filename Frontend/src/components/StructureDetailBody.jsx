import StructureDetailHead from './StructureDetailHead';
import StructureDetailPlans from './StructureDetailPlans';
import StructureDetailRow from './StructureDetailRow';

import { createEffect, createSignal } from "solid-js";

import useFetch from '../hooks/useFetch';


/**
 * Shows the body part of the strcutre detail page
 * @returns component for the body part
 */
function StructureDetailBody() {

    const [sensors, setSensors] = createSignal([]);

    const sensorsFetchRequest = async () => {
        const requestData = {
            method: "GET",
            headers: {
                "Content-Type": "application/json"
            }
        };

        const { fetchData, statusCode, data, errorFetch } = useFetch();
        await fetchData("/api/structures/" + 1 + "/sensors", requestData);
        if (statusCode() === 200) {
            setSensors(data());
            console.log("sensorsFetchRequest : " + data());
        } else if (statusCode() === 404) {
        
        }
    };

    createEffect(() => sensorsFetchRequest());
    
    return (
        
        <div class="flex flex-col gap-y-50px max-w-1250px mx-auto w-full">
            <StructureDetailHead />
            <StructureDetailPlans />
            <StructureDetailRow sensors={sensors} />
        </div>
    );
}

export default StructureDetailBody

