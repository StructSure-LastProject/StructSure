import StructureDetailHead from './StructureDetailHead';
import StructureDetailPlans from './StructureDetailPlans';
import StructureDetailRow from './StructureDetailRow';

import { createEffect, createSignal } from "solid-js";

import useFetch from '../../hooks/useFetch';
import { useNavigate } from '@solidjs/router';


/**
 * Will fetch the sensors for the plan
 */
export const planSensorsFetchRequest = async (structureId, setPlanSensors, planId) => {
    if (planId === null) return;
    const token = localStorage.getItem("token");
    const navigate = useNavigate();
    const requestData = {
        method: "GET",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`   
        }
    };

    const { fetchData, statusCode, data, errorFetch } = useFetch();
    await fetchData(`/api/structures/${structureId}/plan/${planId}/sensors`, requestData);
    if (statusCode() === 200) {
        setPlanSensors(data());
    } else if (statusCode() === 401) {
        navigate("/login");
    }
};



/**
 * Will fetch the list of the sensors of this structure
 */
export const sensorsFetchRequest = async (structureId, setSensors, setTotalItems, filters = {}) => {
    const navigate = useNavigate();
    const token = localStorage.getItem("token");
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
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify(requestBody) // Ajouter les paramètres dans le body
    };

    await fetchData(requestUrl, requestData);

    if (statusCode() === 200) {
        setTotalItems(data().sizeOfResult);
        setSensors((data().sensors));
    } else if (statusCode() === 401) {
        navigate("/login");
    }
};

/**
 * Shows the body part of the strcutre detail page
 * @returns component for the body part
 */
function StructureDetailBody(props) {

    const [sensors, setSensors] = createSignal({});
    const [structureDetails, setStructureDetails] = createSignal({"id": null, "name": null, "note": null, "scans": [], "plans": [], "sensors": []});
    const [planSensors, setPlanSensors] = createSignal([]);
    const [selectedPlanId, setSelectedPlanId] = createSignal(null);

    const [totalItems, setTotalItems] = createSignal(0);
    const navigate = useNavigate();

    const [selectedScan, setSelectedScan] = createSignal(-1);


    /**
     * Will fetch the structure details
     */
    const structureDetailsFetchRequest = async (structureId) => {
        const token = localStorage.getItem("token");
        const requestData = {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            }
        };
        const { fetchData, statusCode, data, errorFetch } = useFetch();
        await fetchData(`/api/structures/${structureId}`, requestData);
        if (statusCode() === 200) {
            setStructureDetails(data());
        } else if (statusCode() === 401) {
            navigate("/login");
        }
    };


    createEffect(() => {
        structureDetailsFetchRequest(props.structureId);
        sensorsFetchRequest(props.structureId, setSensors, setTotalItems);
        planSensorsFetchRequest(props.structureId, setPlanSensors, selectedPlanId());
    });

    /**
     * Sets the sensor in the structure details
     * @param {list} s list of the sensors
     */
    const setSensorsDetail = (s) => {
        setStructureDetails(prev => ({ ...prev, s }));
    };


    return (
        <div class="flex flex-col gap-y-50px max-w-1250px mx-auto w-full">
            <StructureDetailHead selectedScan={selectedScan} setSelectedScan={setSelectedScan} scans={structureDetails().scans} structureDetails={structureDetails} setStructureDetails={setStructureDetails}/>
            <StructureDetailPlans
                structureDetails={structureDetails}
                structureId={props.structureId}
                selectedPlanId={selectedPlanId}
                setSelectedPlanId={setSelectedPlanId}
                planSensors={planSensors}
                setPlanSensors={setPlanSensors}
                setSensors={setSensorsDetail}
            />
            <StructureDetailRow selectedScan={selectedScan} structureDetails={structureDetails} structureId={props.structureId} setSensors={setSensors} selectedPlanId={selectedPlanId} sensors={sensors} totalItems={totalItems} setTotalItems={setTotalItems} />
        </div>
    );
}

export default StructureDetailBody
