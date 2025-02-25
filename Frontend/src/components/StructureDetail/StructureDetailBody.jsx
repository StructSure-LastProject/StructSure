import StructureDetailHead from './StructureDetailHead';
import StructureDetailPlans from './StructureDetailPlans';
import StructureDetailRow from './StructureDetailRow';

import { createEffect, createSignal } from "solid-js";

import useFetch from '../../hooks/useFetch';


/**
 * Will fetch the sensors for the plan
 */
export const planSensorsFetchRequest = async (structureId, setPlanSensors, planId = 1) => {
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
    } // Uncomment this when error barre is developped
    // else if (statusCode() === 404) {
    // }
};



/**
 * Will fetch the list of the sensors of this structure
 */
export const sensorsFetchRequest = async (structureId, setSensors, setTotalItems, filters = {}) => {
    const { fetchData, statusCode, data, errorFetch } = useFetch();

    // Construire le body avec les filtres
    const requestBody = {
        orderByColumn: filters.orderByColumn || "STATE",
        orderType: filters.orderType || "ASC",
        limit: filters.limit ?? 5,
        offset: filters.offset ?? 0,
        ...(filters?.stateFilter && { stateFilter: filters.stateFilter }),
        ...(filters?.archivedFilter && {archivedFilter: filters.archivedFilter}),
        ...(filters?.planFilter && {planFilter: filters.planFilter}),
        ...(filters?.minInstallationDate && {minInstallationDate: filters.minInstallationDate}),
        ...(filters?.maxInstallationDate && {maxInstallationDate: filters.maxInstallationDate})
    };


    const requestUrl = `/api/structures/${structureId}/sensors`;

    const requestData = {
        method: "POST",  // Changer GET en POST
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(requestBody) // Ajouter les paramètres dans le body
    };

    await fetchData(requestUrl, requestData);

    if (statusCode() === 200) {
        setTotalItems(data().sizeOfResult);
        setSensors((data().sensors));
    }// Uncomment this when error barre is developped
    // else if (statusCode() === 404) {
    // }
};

/**
 * Shows the body part of the strcutre detail page
 * @returns component for the body part
 */
function StructureDetailBody(props) {

    const [sensors, setSensors] = createSignal({});
    const [structureDetails, setStructureDetails] = createSignal({"scans": [], "plans": []});
    const [planSensors, setPlanSensors] = createSignal([]);
    const [selectedPlanId, setSelectedPlanId] = createSignal(null);
    const [totalItems, setTotalItems] = createSignal(0);

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


    createEffect(() => {
        structureDetailsFetchRequest(props.structureId);
        sensorsFetchRequest(props.structureId, setSensors, setTotalItems);
        planSensorsFetchRequest(props.structureId, setPlanSensors);
    });
    
    return (
        <div class="flex flex-col gap-y-50px max-w-1250px mx-auto w-full">
            <StructureDetailHead scans={structureDetails().scans}/>
            <StructureDetailPlans
              plans={structureDetails().plans}
              structureId={props.structureId}
              planSensors={planSensors()}
              selectedPlanId={selectedPlanId}
              setSelectedPlanId={setSelectedPlanId}
            />
            <StructureDetailRow structureId={props.structureId} setSensors={setSensors} selectedPlanId={selectedPlanId} sensors={sensors} totalItems={totalItems} setTotalItems={setTotalItems} />
        </div>
    );
}

export default StructureDetailBody
