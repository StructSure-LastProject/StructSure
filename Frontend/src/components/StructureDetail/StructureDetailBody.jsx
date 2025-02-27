import StructureDetailHead from './StructureDetailHead';
import StructureDetailPlans from './StructureDetailPlans';
import StructureDetailRow from './StructureDetailRow';
import { useNavigate } from '@solidjs/router';
import { createEffect, createSignal } from "solid-js";
import useFetch from '../../hooks/useFetch';



/**
 * Will fetch the sensors for the plan
 */
export const planSensorsFetchRequest = async (structureId, setPlanSensors, planId, navigate) => {
    if (planId === null) return;
    const token = localStorage.getItem("token");
    const requestData = {
        method: "GET",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`   
        }
    };

    const { fetchData, statusCode, data, errorFetch } = useFetch();
    await fetchData(navigate, `/api/structures/${structureId}/plan/${planId}/sensors`, requestData);
    if (statusCode() === 200) {
        setPlanSensors(data());
    }
};

/**
 * Fetch sensors from a scan and plan
 * @param structureId The structure id
 * @param scanId The scan id
 * @param planId The plan id
 * @param setPlanSensors The setter of plan sensors
 * @param navigate The navigate object
 */
export const planSensorsScanFetchRequest = async (structureId, scanId, planId, setPlanSensors, navigate) => {
    if (planId === null) {
        return;
    }
    const requestData = {
        method: "GET",
        headers: {
            "Content-Type": "application/json"
        }
    };

    const { fetchData, statusCode, data, errorFetch } = useFetch();
    const requestUrl = `/api/structures/${structureId}/plan/${planId}/sensors${scanId ? `?scanId=${scanId}` : ''}`;

    await fetchData(navigate, requestUrl, requestData);

    if (statusCode() === 200) {
        setPlanSensors((data()));
    }
};

/**
 * Will fetch the list of the sensors of this structure
 */
export const sensorsFetchRequest = async (structureId, setSensors, setTotalItems, navigate, filters = {}) => {
    const token = localStorage.getItem("token");
    const { fetchData, statusCode, data } = useFetch();

    // Construire le body avec les filtres
    const requestBody = {
        orderByColumn: filters.orderByColumn || "STATE",
        orderType: filters.orderType || "ASC",
        limit: filters.limit ?? 5,
        offset: filters.offset ?? 0,
        ...(filters?.stateFilter && { stateFilter: filters.stateFilter }),
        ...(filters?.archivedFilter && {archivedFilter: filters.archivedFilter}),
        ...(filters?.scanFilter && {scanFilter: filters.scanFilter}),
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

    await fetchData(navigate, requestUrl, requestData);

    if (statusCode() === 200) {
        setTotalItems(data().sizeOfResult);
        setSensors((data().sensors));
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
    const [selectedScan, setSelectedScan] = createSignal(-1);
    const navigate = useNavigate();

    const [note, setNote] = createSignal("");

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
        await fetchData(navigate, `/api/structures/${structureId}`, requestData);
        if (statusCode() === 200) {
            setStructureDetails(data());
            setNote(data()?.note);
        } else if (statusCode() === 422) {
            navigate("/")
        }
    };


    createEffect(() => {
        structureDetailsFetchRequest(props.structureId);
        sensorsFetchRequest(props.structureId, setSensors, setTotalItems, navigate);
        planSensorsFetchRequest(props.structureId, setPlanSensors, selectedPlanId(), navigate);
    });

    /**
     * Sets the sensor in the structure details
     * @param {Array} s list of the sensors
     */
    const setSensorsDetail = (s) => {
        setStructureDetails(prev => ({ ...prev, sensors: s }));
    };



    return (
        <div class="flex flex-col gap-y-50px max-w-1250px mx-auto w-full">
            <StructureDetailHead
              setTotalItems={setTotalItems}
              setSensors={setSensors}
              setNote={setNote}
              selectedPlan={selectedPlanId}
              setPlanSensors={setPlanSensors}
              selectedScan={selectedScan}
              setSelectedScan={setSelectedScan}
              structureDetails={structureDetails}
              setStructureDetails={setStructureDetails}
            />
            <StructureDetailPlans
                structureDetails={structureDetails}
                structureId={props.structureId}
                selectedPlanId={selectedPlanId}
                setSelectedPlanId={setSelectedPlanId}
                planSensors={planSensors}
                setPlanSensors={setPlanSensors}
                setSensorsDetail={setSensorsDetail}
            />
            <StructureDetailRow
              note={note}
              structureDetails={structureDetails}
              structureId={props.structureId}
              setSensors={setSensors}
              selectedScan={selectedScan}
              selectedPlanId={selectedPlanId}
              sensors={sensors}
              totalItems={totalItems}
              setTotalItems={setTotalItems}
            />
        </div>
    );
}

export default StructureDetailBody
