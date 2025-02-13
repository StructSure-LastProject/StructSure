import StructureDetailHead from './StructureDetailHead';
import StructureDetailPlans from './StructureDetailPlans';
import StructureDetailRow from './StructureDetailRow';

import { createEffect, createSignal } from "solid-js";

import useFetch from '../../hooks/useFetch';


/**
 * Shows the body part of the strcutre detail page
 * @returns component for the body part
 */
function StructureDetailBody(props) {

    const [sensors, setSensors] = createSignal([]);
    const [structureDetails, setStructureDetails] = createSignal({"scans": []}); 
    const [planSensors, setPlanSensors] = createSignal([]);

    /**
     * Will fetch the list of the sensors of this structure
     */
    const sensorsFetchRequest = async (structureId) => {
        const requestData = {
            method: "GET",
            headers: {
                "Content-Type": "application/json"
            }
        };

        const { fetchData, statusCode, data, errorFetch } = useFetch();
        await fetchData(`/api/structures/${structureId}/sensors`, requestData);
        if (statusCode() === 200) {
            setSensors(data());
        } else if (statusCode() === 404) {
        
        }
    };

    /**
     * Will fetch the structure details
     */
    const structureDetailsFetchRequest = async (structureId) => {
        const requestData = {
            method: "GET",
            headers: {
                "Content-Type": "application/json"
            }
        };

        const { fetchData, statusCode, data, errorFetch } = useFetch();
        await fetchData(`/api/structures/${structureId}`, requestData);
        if (statusCode() === 200) {
            setStructureDetails(data());
        } 
        // Uncomment this when error barre is developped
        // else if (statusCode() === 404) {
        // }
    };

    /**
     * Will fetch the sensors for the plan
     */
    const planSensorsFetchRequest = async (structureId, planId = 1) => {
        const requestData = {
            method: "GET",
            headers: {
                "Content-Type": "application/json"
            }
        };

        const { fetchData, statusCode, data, errorFetch } = useFetch();
        await fetchData(`/api/structures/${structureId}/plan/${planId}/sensors`, requestData);
        if (statusCode() === 200) {
            setPlanSensors(data());
        } else if (statusCode() === 404) {
        }
    };

    createEffect(() => {
        structureDetailsFetchRequest(props.structureId);
        sensorsFetchRequest(props.structureId);
        planSensorsFetchRequest(props.structureId);
    });
    
    return (
        
        <div class="flex flex-col gap-y-50px max-w-1250px mx-auto w-full">
            <StructureDetailHead scans={structureDetails().scans}/>
            <StructureDetailPlans planSensors={planSensors()} />
            <StructureDetailRow sensors={sensors} />
        </div>
    );
}

export default StructureDetailBody
