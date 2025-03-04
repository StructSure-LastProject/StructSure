import { Download, FolderSync, Plus, Trash2 } from 'lucide-solid';
import {createEffect, createSignal, For, Show} from 'solid-js';
import SensorPanel from '../SensorPanel/SensorPanel';
import getSensorStatusColor from "../SensorStatusColorGen"
import ModalAddSensor from "../Sensor/ModalAddSensor.jsx";
import SensorFilter from '../SensorFilter';
import { Pagination } from '../Pagination.jsx';
import {useNavigate, useSearchParams} from "@solidjs/router";
import {planSensorsFetchRequest, sensorsFetchRequest, sensorsWithoutLimitAndOffsetFetchRequest} from "./StructureDetailBody.jsx";
import useFetch from '../../hooks/useFetch.js';
import Papa from "papaparse";
import Alert from '../Alert.jsx';

/**
 * Show the sensors part of the structure detail page
 * @param {String} structureId The structure id
 * @param {Function} setSensors The set sonsors function
 * @param {function} selectedScan The selected scan id
 * @param {String} selectedPlanId The selected plan id
 * @param {Array} sensors The sensors array
 * @param {Number} totalItems Total number of sensors
 * @param {Function} setTotalItems setter to set the total number of sensor
 * @param {Function} setSensorsDetail setter to set the sensors in structureDetails state
 * @param {Function} structureDetails The structure detail
 * @returns the component for the sensors part
 */
function StructureDetailCapteurs({structureId, setSensors, selectedScan, selectedPlanId, sensors, totalItems, setTotalItems, setPlanSensors, setSensorsDetail, structureDetails}) {
    const [openSensorPanel, setOpenSensorPanel] = createSignal(false);
    const [clickedSensor, setClickedSensor] = createSignal({});

    const [isAddModalOpen, setIsAddModalOpen] = createSignal(false);
    
    const [isAuthorized, setIsAuthorized] = createSignal(false);

    const [sensorsForCSV, setSensorsForCSV] = createSignal(null);
    const [sensorsSizeForCSV, setSensorsSizeForCSV] = createSignal(null);

    const [limit, setLimit] = createSignal(30);
    const [offset, setOffset] = createSignal(0);
    const [searchParams, setSearchParams] = useSearchParams();

    const [errorFront, setErrorFront] = createSignal("");


    const SORT_VALUES = {
        "Tout" : "Tout", "Nom": "NAME", "Etat": "STATE", "Date d'installation": "INSTALLATION_DATE"
    };
    const FILTER_VALUES = {"Tout" : "Tout", "OK" : "OK", "NOK" : "NOK", "Défaillant" : "DEFECTIVE", "Non détecté" : "UNKNOWN"};


    const [orderByColumn, setOrderByColumn] = createSignal(
        searchParams.orderByColumn && SORT_VALUES[searchParams.orderByColumn] 
            ? searchParams.orderByColumn
            : "Tout"
    );
    
    const [stateFilter, setStateFilter] = createSignal(
        searchParams.stateFilter && FILTER_VALUES[searchParams.stateFilter] 
            ? searchParams.stateFilter 
            : "Tout"
    );

    const [orderType, setOrderType] = createSignal(
        searchParams.orderType ? searchParams.orderType === "true" : true
    );
    
    const [isCheckedPlanFilter, setIsCheckedPlanFilter] = createSignal(
        searchParams.isCheckedPlanFilter ? searchParams.isCheckedPlanFilter === "true" : false
    );
    
    const [isCheckedArchivedFilter, setIsCheckedArchivedFilter] = createSignal(
        searchParams.isCheckedArchivedFilter ? searchParams.isCheckedArchivedFilter === "true" : false
    );


    const [startDate, setStartDate] = createSignal(
        searchParams.startDate ? searchParams.startDate : ""
    );
    const [endDate, setEndDate] = createSignal(
        searchParams.endDate ? searchParams.endDate : ""
    );

    const navigate = useNavigate();
    const { fetchData, statusCode } = useFetch();
    const token = localStorage.getItem("token");
    

    /**
     * Open the sensor panel
     * @param {Object} sensor Sensor object that contains all the details about the clicked sensor
     */
    const openSensorPanelHandler = (sensor) => {
        setClickedSensor(sensor);
        setOpenSensorPanel(true);
        document.body.style.overflow = 'hidden';
    }

    /**
     * Opens the add sensor modal
     */
    const openAddModal = () => setIsAddModalOpen(true);

    /**
     * Closes the add sensor modal
     */
    const closeAddModal = () => setIsAddModalOpen(false);

    /**
     * Handles saving a newly added sensor
     */
    const handleAddSave = () => {
        sensorsFetchRequest(structureId, setSensors, setTotalItems, navigate, {limit: limit(), offset: offset()});
        closeAddModal();
    };

    /**
     * Close the sensor panel
     */
    const closeSensorPanelHandler = () => {
        setClickedSensor({});
        setOpenSensorPanel(false);
        document.body.style.overflow = "auto";
    }


    /**
     * Archive a sensor
     * @param {Object} sensorDetails The sensor details
     * @param {Boolean} isArchive want to archive or not
     */
    const toggleArchiveSensor = async (sensorDetails, isArchiveValue) => {
        const requestData = {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            },
            body: JSON.stringify({
                controlChip: sensorDetails.controlChip,
                measureChip: sensorDetails.measureChip,
                isArchive: isArchiveValue
              }
            )
        };

        await fetchData(navigate, "/api/sensors/archive", requestData);

        if (statusCode() === 200) {
            sensorsFetchRequest(structureId, setSensors, setTotalItems, navigate, {
                orderByColumn: orderByColumn() !== "Tout" ? SORT_VALUES[orderByColumn()] : "STATE",
                orderType: orderType() ? "ASC" : "DESC",
                limit: limit(),
                offset: offset(),
                ...(selectedScan() > -1 && {scanFilter: selectedScan()}),
                ...(stateFilter() !== "Tout" && {stateFilter: FILTER_VALUES[stateFilter()] }),
                ...(isCheckedArchivedFilter() ? {archivedFilter: isCheckedArchivedFilter()} : false),
                ...(isCheckedPlanFilter() && selectedPlanId() !== undefined && {planFilter: selectedPlanId()}),
                ...(startDate() !== "" && {minInstallationDate: startDate()}),
                ...(endDate() !== "" && {maxInstallationDate: endDate()})
            })
            planSensorsFetchRequest(structureId, setPlanSensors, selectedPlanId(), navigate);
        }
    }   


    /**
     * Effect that updates plans based on props and user role
     */
    createEffect(() => {
        setIsAuthorized(selectedScan() <= -1);
    });

    /**
     * 
     */
    const sendDownloadCsvRequest = () => {
        sensorsWithoutLimitAndOffsetFetchRequest(structureId, setSensorsForCSV, setSensorsSizeForCSV, navigate, {
            orderByColumn: orderByColumn() !== "Tout" ? SORT_VALUES[orderByColumn()] : "STATE",
            orderType: orderType() ? "ASC" : "DESC",
            ...(selectedScan() > -1 && {scanFilter: selectedScan()}),
            ...(stateFilter() !== "Tout" && {stateFilter: FILTER_VALUES[stateFilter()] }),
            ...(isCheckedArchivedFilter() ? {archivedFilter: isCheckedArchivedFilter()} : false),
            ...(isCheckedPlanFilter() && selectedPlanId() !== undefined && {planFilter: selectedPlanId()}),
            ...(startDate() !== "" && {minInstallationDate: startDate()}),
            ...(endDate() !== "" && {maxInstallationDate: endDate()})
        });
    };

    createEffect(() => {
        if (sensorsForCSV()) {
            const sensorsData = sensorsForCSV();
    
            if (!sensorsData || sensorsData.length === 0) {
                setErrorFront("Aucune donnée à exporter.");
                return;
            }

            const headers = ["controlChip", "measureChip", "name", "state", "archived", "installationDate", "plan", "x", "y", "note"];
    
            // Map des données des capteurs, ici je prends en compte tous les champs demandés
            const data = sensorsData.map(sensor => ({
                controlChip: sensor.controlChip,
                measureChip: sensor.measureChip,
                name: sensor.name,
                state: sensor.state,
                archived: sensor.archived,
                installationDate: sensor.installationDate, 
                plan: sensor.plan,
                x: sensor.x,
                y: sensor.y,
                note: sensor.note
            }));
            const csv = Papa.unparse({
                fields: headers,
                data: data
            });
            const blob = new Blob([csv], { type: "text/csv" });
            const url = URL.createObjectURL(blob);
            const a = document.createElement("a");
            a.href = url;
            const now = new Date();
            const formatter = new Intl.DateTimeFormat("fr-FR", {
                year: "numeric",
                month: "2-digit",
                day: "2-digit",
                hour: "2-digit",
                minute: "2-digit",
                hour12: false,
                timeZone: "Europe/Paris"
            });
            const formattedDate = formatter.format(now).replace(/\//g, "-").replace(", ", "_").replace(":", "-").replace(" ", "_");
            a.download = `${structureDetails().name}_${formattedDate}.csv`;
            document.body.appendChild(a);
            a.click();
            document.body.removeChild(a);
            setSensorsForCSV(null);
            setSensorsSizeForCSV(null);
        }
    });
    

    return (
        <>
            <Show when={errorFront().length > 0}>
                <Alert message={errorFront()}/>
            </Show>
            <div class="w-full flex flex-col gap-y-[15px]">
                <div class="flex justify-between">
                    <p class="title">{(selectedScan() <= -1) ? "Capteurs" : "Résultats"}</p>
                    <div class="flex justify-between gap-x-[10px]">
                        <button class="w-10 h-10 rounded-[50px] bg-white flex justify-center items-center">
                            <Download size={20} color='black' onClick={sendDownloadCsvRequest} />
                        </button>
                        <Show when={isAuthorized()}>
                            <button
                                title="Ajouter un capteur"
                                onClick={openAddModal}
                                class="w-10 h-10 rounded-[50px] bg-black flex justify-center items-center"
                            >
                                <Plus size={20} color='white'/>
                            </button>
                        </Show>
                        <Show when={isAddModalOpen()}>
                            <ModalAddSensor
                            isOpen={isAddModalOpen()}
                            onClose={closeAddModal}
                            structureId={structureId}
                            onSave={handleAddSave}
                            setSensorsDetail={setSensorsDetail}
                            structureDetails={structureDetails}
                            />
                        </Show>
                    </div>
                </div>
                <SensorFilter
                    selectedScan={selectedScan}
                    structureId={structureId}
                    setSensors={setSensors}
                    limit={limit}
                    offset={offset}
                    setTotalItems={setTotalItems}
                    selectedPlanId={selectedPlanId}
                    orderByColumn={orderByColumn} 
                    setOrderByColumn={setOrderByColumn}
                    orderType={orderType}
                    setOrderType={setOrderType}
                    isCheckedPlanFilter={isCheckedPlanFilter}
                    setIsCheckedPlanFilter={setIsCheckedPlanFilter}
                    isCheckedArchivedFilter={isCheckedArchivedFilter}
                    setIsCheckedArchivedFilter={setIsCheckedArchivedFilter}
                    startDate={startDate}
                    setStartDate={setStartDate}
                    endDate={endDate}
                    setEndDate={setEndDate}
                    stateFilter={stateFilter}
                    setStateFilter={setStateFilter}
                    SORT_VALUES={SORT_VALUES}
                    FILTER_VALUES={FILTER_VALUES}
                />
                <div class="flex flex-col lg:grid lg:grid-cols-3 rounded-[20px] gap-4">
                    <For each={sensors()}>
                        {(sensor) => (
                            <Show 
                                when={!sensor.archived} 
                                fallback={
                                    <div class="flex justify-between rounded-[50px] px-[25px] py-[10px] bg-white">    
                                        <button class="flex gap-x-[15px] items-center" onClick={() => openSensorPanelHandler(sensor)} >
                                            <div class={`w-[16px] min-w-[16px] h-[16px] rounded-[50px] border-2 ${getSensorStatusColor(sensor.state)}`}></div>
                                            <p class="subtitle text-left w-full text-[#6A6A6A]">{sensor.name}</p>
                                        </button>
                                        <Show when={localStorage.getItem("role") === "RESPONSABLE" || localStorage.getItem("role") === "ADMIN" }>
                                            <button onClick={() => toggleArchiveSensor(sensor, false)} class="w-5 h-5 rounded-[50px] flex justify-center items-center">
                                                <FolderSync color='#6A6A6A' class="w-full" />
                                            </button>
                                        </Show>
                                    </div>
                                }
                            >
                                <div class="flex justify-between rounded-[50px] px-[25px] py-[10px] bg-white group">    
                                    <button class="flex gap-x-[15px] items-center" onClick={() => openSensorPanelHandler(sensor)} >
                                        <div class={`w-[16px] min-w-[16px] h-[16px] rounded-[50px] border-2 ${getSensorStatusColor(sensor.state)}`}></div>
                                        <p class="subtitle text-left w-full">{sensor.name}</p>
                                    </button>
                                    <Show when={localStorage.getItem("role") === "RESPONSABLE" || localStorage.getItem("role") === "ADMIN" }>
                                        <button onClick={() => toggleArchiveSensor(sensor, true)} class="invisible group-hover:visible w-5 h-5 rounded-[50px] flex justify-center items-center">
                                            <Trash2 size={20} />
                                        </button>
                                    </Show>
                                </div>
                            </Show>
                        )}
                    </For>
                </div>
                <Show when={totalItems() !== null}>
                    <Pagination
                        limit={limit}
                        offset={offset}
                        setOffset={setOffset}
                        totalItems={totalItems}
                    />
                </Show> 
                {
                    openSensorPanel() && (
                        <SensorPanel structureId={structureId} sensors={sensors} setSensors={setSensors} selectedPlanId={selectedPlanId} sensorDetails={clickedSensor()} closeSensorPanel={closeSensorPanelHandler} setTotalItems={setTotalItems}/>
                    )
                }
            </div>
        </>
    );
}

export default StructureDetailCapteurs

