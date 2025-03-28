import { createSignal, onMount, onCleanup, Show, createEffect, createMemo } from "solid-js";
import { Minus, Plus } from 'lucide-solid';
import useFetch from '../../hooks/useFetch';
import Alert from '../Alert';
import { PointTooltip, PlacePoint } from "./StructureDetailCanvasModals";
import getSensorStatusColor from "../SensorStatusColorGen";
import { planSensorsFetchRequest } from "./StructureDetailBody";
import { useNavigate } from "@solidjs/router";

/**
 * Shows the plans part
 * @returns the component for the plans part
 */
function StructureDetailCanvas(props) {
    const ZOOM = 114;
    const IMAGE_MOVE_LIMIT = 10;
    const SENSOR_POINT_SIZE = 10;
    const ZOOM_LIMIT = 5;
    const [canvasWidth, setCanvasWidth] = createSignal(null);
    const [canvasHeight, setCanvasHeight] = createSignal(null);
    const [ctxCanvas, setCtxCanvas] = createSignal("");
    const [zoomFactor, setZoomFactor] = createSignal(0);
    const [offsetX, setOffsetX] = createSignal(0);
    const [offsetY, setOffsetY] = createSignal(0);
    const [baseOffsetX, setBaseOffsetX] = createSignal(0);
    const [baseOffsetY, setBaseOffsetY] = createSignal(0);
    const [imgRatio, setImgRatio] = createSignal(0);
    const [canvasRatio, setCanvasRatio] = createSignal(0);
    const [drawWidth, setDrawWidth] = createSignal(0);
    const [drawHeight, setDrawHeight] = createSignal(0);
    const [errorFront, setErrorFront] = createSignal("");

    const [isPopupVisible, setIsPopupVisible] = createSignal(false);
    const [popupX, setPopupX] = createSignal(0);
    const [popupY, setPopupY] = createSignal(0);
    const [posX, setPosX] = createSignal(-1);
    const [posY, setPosY] = createSignal(-1);
    const [clickExistingPoint, setClickExistingPoint] = createSignal(null);

    const [cClickX, setCClickX] = createSignal(0);
    const [cClickY, setCClickY] = createSignal(0);

    const [inputValue, setInputValue] = createSignal("");

    const [selectedSensor, setSelectedSensor] = createSignal(null);
    const navigate = useNavigate();


    /**
     * Reset the values when a plan changed
     */
    const initCanvasWhenPlanChanged = () => {
        setZoomFactor(0);
        setOffsetX(0);
        setOffsetY(0);
        setBaseOffsetX(0);
        setBaseOffsetY(0);
        setIsPopupVisible(false);
        isMouseDown = false;
        hasMoved = false;
    };

    const filteredOptions = createMemo(() => {
        if (!props.localSensors()) return [];
        return props.localSensors().filter(detailSensor =>
            detailSensor.x == null && detailSensor.y == null && detailSensor.archived === false && detailSensor.name?.toLowerCase().includes(inputValue().toLowerCase() || "")
        );
    });
    

    /**
     * Updates data when a sensor is placed in the canvas
     */
    const updateWhenSensorPlaced = () => {
        if (selectedSensor()) {
            const newSensor = {
                ...selectedSensor(),
                x: popupX(),
                y: popupY()
            };
            props.setLocalSensors(props.localSensors().map(sensor =>
                sensor.controlChip === selectedSensor().controlChip && sensor.measureChip === selectedSensor().measureChip 
                ? { ...sensor, x: parseInt(newSensor.x), y: parseInt(newSensor.y) } : sensor
            ));
            props.setSensors(
                props.sensors().map(sensor =>
                    sensor.controlChip === selectedSensor().controlChip && sensor.measureChip === selectedSensor().measureChip 
                    ? { ...sensor, x: parseInt(newSensor.x), y: parseInt(newSensor.y) } : sensor
                )
            );
            planSensorsFetchRequest(props.structureId, props.setPlanSensors, props.selectedPlanId(), navigate);
            setSelectedSensor(null);
            setInputValue("");
            setIsPopupVisible(false);    
            drawImage();
        }
    };

     /**
     * Updates data when a sensor is deleted from canvas
     */
    const updateWhenSensorPositionDeleted = () => {
        if (clickExistingPoint()) {

            planSensorsFetchRequest(props.structureId, props.setPlanSensors, props.selectedPlanId(), navigate);
            const newDetailSensors = props.localSensors().map(sensor => {
                return sensor.controlChip === clickExistingPoint().controlChip && sensor.measureChip === clickExistingPoint().measureChip
                    ? { ...sensor, x: null, y: null }
                    : sensor
            }
            );
            props.setLocalSensors(newDetailSensors);
            props.setSensorsDetail(props.structureDetails().sensors.map(sensor =>
                sensor.controlChip === clickExistingPoint().controlChip && sensor.measureChip === clickExistingPoint().measureChip 
                ? { ...sensor, x: null, y: null } : sensor
            ));
            setClickExistingPoint(null);
            setInputValue("");
            setIsPopupVisible(false);
            drawImage();
        }
    };


    /**
     * Will call the endpoint that deletes a position of sensor in a plan
     */
    const deletePositionRequest = async (controlChip, measureChip) => {
        const { fetchData, statusCode, error } = useFetch();

        const url = `/api/sensors/${controlChip}/${measureChip}/position/delete`;

        const requestData = {
            method: "DELETE",  // Changer GET en POST
            headers: {
                "Content-Type": "application/json",
            }
        };

        await fetchData(navigate, url, requestData);

        if (statusCode() === 200) {
            updateWhenSensorPositionDeleted();
        } else if (statusCode() === 404 || statusCode() === 422) {
            setErrorFront(error().errorData.error);
        }
    };


    /**
     * Will call the endpoint that places a sensor in a plan
     */
    const positionSensorFetchRequest = async (structureId, controlChip, measureChip, planId, x, y) => {
        const { fetchData, statusCode, error } = useFetch();
    
        const requestBody = {
            structureId: structureId,
            controlChip: controlChip,
            measureChip: measureChip,
            planId: planId,
            x: parseInt(x),
            y: parseInt(y)
        };
    
        const requestUrl = "/api/sensors/position";
    
        const requestData = {
            method: "POST",  // Changer GET en POST
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(requestBody) // Ajouter les paramètres dans le body
        };
    
        await fetchData(navigate, requestUrl, requestData);
    
        if (statusCode() === 200) {
            updateWhenSensorPlaced();
        } else if (statusCode() === 404 || statusCode() === 422) {
            setErrorFront(error().errorData.error);
        }
    };
    
    const img = new Image();
    let canvasRef;
    let isMouseDown = false;
    let startX = 0;
    let startY = 0;
    let popupRef;
    let refLstOfSensors;
    let hasMoved = false;

    /**
     * Calculates the zoom ratio
     * @param {number} imgRatio the ratio of the image
     * @param {number} canvasRatio the ration of the canvas
     * @param {number} zoomNumber the zoom number
     * @returns {number[]} in index 0 zoom for x, and in index 1 zoom for y
     */
    const zoomRatioFromZoomNumber = (imgRatio, canvasRatio, zoomNumber) => {
        let zoomX = 0, zoomY = 0;
        if (imgRatio > canvasRatio) {
            zoomX = zoomNumber;
            zoomY = zoomNumber / imgRatio;
        } else {
            zoomX = zoomNumber * imgRatio;
            zoomY = zoomNumber;
        }
        return [zoomX, zoomY];
    };

    /**
     * Returns the start image position x
     * @param {Number} baseOffsetX the base offset x of the image
     * @param {Number} offsetX the offset x of the image
     * @param {Number} zoom the zoom number
     * @returns start image position x
     */
    const getImgStartX = (baseOffsetX, offsetX, zoom) => {
        const [zoomX, zoomY] = zoomRatioFromZoomNumber(imgRatio(), canvasRatio(), zoom);
        return baseOffsetX + offsetX - zoomX / 2;
    };

    /**
     * Returns the start image position y
     * @param {Number} baseOffsetX the base offset y of the image
     * @param {Number} offsetX the offset y of the image
     * @param {Number} zoom the zoom number
     * @returns start image position y
     */
    const getImgStartY = (baseOffsetY, offsetY, zoom) => {
        const [zoomX, zoomY] = zoomRatioFromZoomNumber(imgRatio(), canvasRatio(), zoom);
        return baseOffsetY + offsetY - zoomY / 2;
    };

    /**
     * Returns the zoom with ratio from zoom number
     * @param {Number} zoom zoom number
     * @returns the zoom with ratio
     */
    const getZoomRationFromZoomNumber = (zoom) => {
        return zoomRatioFromZoomNumber(imgRatio(), canvasRatio(), zoom);
    }

    /**
     * Returns the background and border colors
     * @param {object} sensor the sensor
     * @returns {Array[str, str]} [0] = the background color, [1] = the border color
     */
    const getColorFromSensor = (sensor) => {
        let bgColor = "";
        let borderColor = "";
        switch (sensor.state) {
            case "OK":
                bgColor = "#25B61F";
                borderColor = "#25b51f40";
                break;
            case "NOK":
                bgColor = "#F13327";
                borderColor = "#f1332740";
                break;
            case "DEFECTIVE":
                bgColor = "#F19327";
                borderColor = "#f1932740";
                break;
            case "UNKNOWN":
                bgColor = "#6A6A6A";
                borderColor = "#6a6a6a40";
                break;
            default:
                setErrorFront("L'etat du sensor inconnu");
                break;
        }
        return [bgColor, borderColor];
    };

    /**
     * Draws the sensors in the canvas
     * @param {number} imgStartX the image start position y
     * @param {number} imgStartY the image start position x
     * @param {number} drawWidth the width of the image
     * @param {number} drawHeight the height of the image
     * @param {number} zoomX the zoom with ratio for x axis
     * @param {number} zoomY the zoom with ratio for y axis
     */
    const drawSensors = (imgStartX, imgStartY, drawWidth, drawHeight, zoomX, zoomY) => {
        const ctx = ctxCanvas();
        if (ctx === null || ctx === "") return;
        const scaleX = (drawWidth + zoomX) / img.width;
        const scaleY = (drawHeight + zoomY) / img.height;
        props.planSensors().forEach(sensor => {
            if (sensor.x != null && sensor.y != null) {
                const [bgColor, borderColor] = getColorFromSensor(sensor);
                const sensorCanvasX = imgStartX + sensor.x * scaleX;
                const sensorCanvasY = imgStartY + sensor.y * scaleY;
                ctx.beginPath();
                ctx.arc(sensorCanvasX, sensorCanvasY, SENSOR_POINT_SIZE - 2, 0, Math.PI * 2);
                ctx.fillStyle = bgColor;
                ctx.fill();
                ctx.beginPath();
                ctx.arc(sensorCanvasX, sensorCanvasY, SENSOR_POINT_SIZE, 0, Math.PI * 2);
                ctx.fillStyle = borderColor;
                ctx.fill();
                ctx.closePath();   
            }
        });
    };

    /**
     * Draws the image in the canvas
     */
    const drawImage = () => {
        const ctx = ctxCanvas();
        if (ctx === null || ctx === "") return;
        const imgRatio = img.width / img.height;
        const canvasRatio = canvasRef.width / canvasRef.height;
        let drawWidth = 0, drawHeight = 0, baseOffsetX = 0, baseOffsetY = 0;
        if (imgRatio > canvasRatio) {
            // Image plus large que le canvas (paysage) -> Ajuster en largeur
            drawWidth = canvasRef.width;
            drawHeight = canvasRef.width / imgRatio;
            baseOffsetX = 0;
            baseOffsetY = (canvasRef.height - drawHeight) / 2;
        } else {
            // Image plus haute que le canvas (portrait) -> Ajuster en hauteur
            drawWidth = canvasRef.height * imgRatio;
            drawHeight = canvasRef.height;
            baseOffsetX = (canvasRef.width - drawWidth) / 2;
            baseOffsetY = 0;
        }
        setImgRatio(imgRatio);
        setCanvasRatio(canvasRatio);
        setBaseOffsetX(baseOffsetX);
        setBaseOffsetY(baseOffsetY);
        ctx.clearRect(0, 0, canvasRef.width, canvasRef.height);
        const imgStartX = getImgStartX(baseOffsetX, offsetX(), zoomFactor());
        const imgStartY = getImgStartY(baseOffsetY, offsetY(), zoomFactor());
        const [zoomX, zoomY] = getZoomRationFromZoomNumber(zoomFactor());
        const imgWidth = drawWidth + zoomX;
        const imgHeight = drawHeight + zoomY;
        setDrawWidth(drawWidth);
        setDrawHeight(drawHeight);
        ctx.drawImage(
            img,
            imgStartX,
            imgStartY,
            imgWidth,
            imgHeight
        );
        drawSensors(imgStartX, imgStartY, drawWidth, drawHeight, zoomX, zoomY);
    }

    /**
     * Fixes the dpi for the canvas
     */
    const fixDpi = () => {
        if (!canvasRef || !canvasRef.parentElement) return;
        // Récupérer la vraie taille du parent du canvas
        const parent = canvasRef.parentElement;
        const width = parent.clientWidth;
        const height = parent.clientHeight;
        // Appliquer ces dimensions au canvas
        canvasRef.width = width;
        canvasRef.height = height;
        canvasRef.style.width = `${canvasRef.width}px`;
        canvasRef.style.height = `${canvasRef.height}px`;
        setCanvasWidth(width);
        setCanvasHeight(height);
    };

    /**
     * Handles the resize event
     */
    const handleResize = () => {
        fixDpi();
        drawImage();
    };

    /**
     * Adds the mouse events to the canvas
     */
    const addMouseEvents = () => {
        canvasRef.addEventListener("wheel", handleWheel);
        canvasRef.addEventListener("mousedown", handleMouseDown);
        canvasRef.addEventListener("mousemove", handleMouseMove);
        canvasRef.addEventListener("mouseup", handleMouseUp);
        canvasRef.addEventListener("mouseout", handleMouseUp);
        canvasRef.addEventListener("click", handleCanvasClick);
        window.addEventListener("resize", handleResize);
        document.addEventListener("click", handleOutsideClick);
    }

    /**
     * Loads and draws the image from its link
     * @param {String} imgLink the link of the image
     */
    const loadAndDrawImage = (imgLink) => {
        img.src = imgLink;
        img.onload = () => drawImage();
    }

    /**
     * Loads the details (images and draw it)
     */
    const loadDetails = () => {
        loadAndDrawImage(props.plan());
    };

    createEffect(() => {
        loadDetails();
        drawSensors();
    });

    createEffect(() => {
        if (props.plan() != null) {
            initCanvasWhenPlanChanged();
        }
    });

    /**
     * This function is called in the beginning when this component is mounted in the DOM
     */
    onMount(() => {
        const ctx = canvasRef.getContext('2d');
        fixDpi();
        setCtxCanvas(ctx);
        loadDetails();
        addMouseEvents();
        drawImage();
    })

    /**
     * Handles the click outside the canvas
     * @param {Event} event the click in the page
     */
    const handleOutsideClick = (event) => {
        const refLst = refLstOfSensors && !refLstOfSensors.contains(event.target);
        if ((canvasRef && !canvasRef.contains(event.target)) && (popupRef && !popupRef.contains(event.target))
        && (refLst === undefined || refLst === true)) {
            setIsPopupVisible(false);
        }
    };

     /**
     * Returns the position in the canvas from the position in the original image
     * @param {number} imgX the position x in the original image
     * @param {number} imgY the position y in the original image
     * @returns {[number, number]} - Position (X, Y) in the canvas
     */
     const canvasPositionFromOriginal = (imgX, imgY) => {
        const imgStartX = getImgStartX(baseOffsetX(), offsetX(), zoomFactor());
        const imgStartY = getImgStartY(baseOffsetY(), offsetY(), zoomFactor());
        const [zoomX, zoomY] = getZoomRationFromZoomNumber(zoomFactor());
        const scaleX = (drawWidth() + zoomX) / img.width;
        const scaleY = (drawHeight() + zoomY) / img.height;
        const canvasX = imgStartX + imgX * scaleX;
        const canvasY = imgStartY + imgY * scaleY;
        return [canvasX, canvasY];
    };

    /**
     * Returns the clicked existing point if it exists, otherwise returns null
     * @param {number} x - The x position of the click in the canvas
     * @param {number} y - The y position of the click in the canvas
     * @returns {object|null} - The clicked sensor object or null if no match
     */
    const findClickedSensor = (x, y) => {
        return props.planSensors().find(sensor => {
            const [scx, scy] = canvasPositionFromOriginal(sensor.x, sensor.y);
            const distance = Math.sqrt(
                Math.pow(x - scx, 2) + Math.pow(y - scy, 2)
            );

            return distance <= SENSOR_POINT_SIZE;
        }) || null;
    };

    /**
     * Checks is the position is in the image original dimensions
     * @param {number} x the position x in the original dimensions of the image
     * @param {number} y the position y in the original dimensions of the image
     * @returns
     */
    const isPositionOutOfImage = (x, y) => {
        return !(x < 0 || x > img.width || y < 0 || y > img.height);
    };

    /**
         * Returns the position in the original image from the canvas click position
         * @param {number} clickX the position x of the click
         * @param {number} clickY the position y of the click
         * @returns {[number, number]} - Position (X, Y) in the original image
         */
    const orignalPositionFromCanvasClick = (clickX, clickY) => {
        const imgStartX = getImgStartX(baseOffsetX(), offsetX(), zoomFactor());
        const imgStartY = getImgStartY(baseOffsetY(), offsetY(), zoomFactor());
        const [zoomX, zoomY] = getZoomRationFromZoomNumber(zoomFactor());
        const scaleX = (drawWidth() + zoomX) / img.width;
        const scaleY = (drawHeight() + zoomY) / img.height;
        const px = (clickX - imgStartX) / scaleX;
        const py = (clickY - imgStartY) / scaleY;
        return [px, py];
    };

    /**
     * Handles the canvas click
     * @param {Event} event the event of the click in the canvas
     */
    const handleCanvasClick = (event) => {
        if (hasMoved) return;
        const rect = canvasRef.getBoundingClientRect();
        const x = event.clientX - rect.left;
        const y = event.clientY - rect.top;
        setCClickX(x);
        setCClickY(y);
        const [px, py] = orignalPositionFromCanvasClick(x, y);
        if (!isPositionOutOfImage(px, py)) {
            setIsPopupVisible(false);
            return;
        }
        const clickedSensor = findClickedSensor(x, y);
        if (clickedSensor) {
            setClickExistingPoint(clickedSensor);
            setPopupX(clickedSensor.x);
            setPopupY(clickedSensor.y);
        } else {
            setClickExistingPoint(null);
            setPopupX(px);
            setPopupY(py);
        }
        setPosX(parseInt(px));
        setPosY(parseInt(py));
        setIsPopupVisible(true);
    };

    /**
     * Changes state of isMouseDown variable when mouse is up
     */
    const handleMouseUp = () => {
        isMouseDown = false;
    };

    /**
     * Handles the mouse move event
     * @param {MouseEvent} event the event of the mouse
     */
    const handleMouseMove = (event) => {
        event.preventDefault();
        if (isMouseDown) {
            hasMoved = true;
            setIsPopupVisible(false);
            const dx = event.clientX - startX;
            const dy = event.clientY - startY;
            const imgStartX = getImgStartX(baseOffsetX(), dx, zoomFactor());
            const imgStartY = getImgStartY(baseOffsetY(), dy, zoomFactor());
            const [zoomX, zoomY] = getZoomRationFromZoomNumber(zoomFactor());
            const imgWidth = drawWidth() + zoomX;
            const imgHeight = drawHeight() + zoomY;
            if ((imgStartX + imgWidth) < IMAGE_MOVE_LIMIT || imgStartX > canvasRef.width - IMAGE_MOVE_LIMIT
             || (imgStartY + imgHeight) < IMAGE_MOVE_LIMIT || imgStartY > canvasRef.height - IMAGE_MOVE_LIMIT) {
                return;
            }
            setOffsetX(dx);
            setOffsetY(dy);
            drawImage();
        }
    };

    /**
     * Handles the mouse down
     * @param {MouseEvent} event the mouse event
     */
    const handleMouseDown = (event) => {
        event.preventDefault();
        if (event.button === 0) {
            isMouseDown = true;
            startX = event.clientX - offsetX();
            startY = event.clientY - offsetY();
            hasMoved = false;
        }
    };

    /**
     * Checks if the zoom limit is reached
     * @param {number} newImgWidth the new image width (after zoom)
     * @param {number} newImgHeight the new image height (after zoom)
     * @returns true if limit reached and false if not
     */
    const zoomLimitReached = (newImgWidth, newImgHeight) => {
        return newImgWidth > ZOOM_LIMIT * drawWidth() || newImgHeight > ZOOM_LIMIT * drawHeight();
    };


    /**
     * Applies the zoom if we can
     * @param {number} zoomChange the zoom number
     */
    const zoom = (zoomChange) => {
        setIsPopupVisible(false);
        const newZoom = Math.max(0, zoomFactor() + (-1 * zoomChange));
        const imgStartX = getImgStartX(baseOffsetX(), offsetX(), newZoom);
        const imgStartY = getImgStartY(baseOffsetY(), offsetY(), newZoom);
        const [zoomX, zoomY] = getZoomRationFromZoomNumber(newZoom);
        const imgWidth = drawWidth() + zoomX;
        const imgHeight = drawHeight() + zoomY;
        if ((imgStartX + imgWidth) < IMAGE_MOVE_LIMIT || imgStartX > canvasRef.width - IMAGE_MOVE_LIMIT
            || (imgStartY + imgHeight) < IMAGE_MOVE_LIMIT || imgStartY > canvasRef.height - IMAGE_MOVE_LIMIT
            || zoomLimitReached(imgWidth, imgHeight)) {
            return;
        }
        setZoomFactor(newZoom);
        drawImage();
    }


    /**
     * Handles the mouse wheel event
     * @param {MouseEvent} event the mouse event
     */
    const handleWheel = (event) => {
        event.preventDefault();
        if (event.ctrlKey) {
            setIsPopupVisible(false);
            const zoomChange = event.deltaY;
            zoom(zoomChange);
        }
    };

    /**
     * This function is called at the end of the lifecycle of this component
     */
    onCleanup(() => {
        canvasRef.removeEventListener("wheel", handleWheel);
        canvasRef.removeEventListener("mousedown", handleMouseDown);
        canvasRef.removeEventListener("mousemove", handleMouseMove);
        canvasRef.removeEventListener("mouseup", handleMouseUp);
        canvasRef.removeEventListener("mouseout", handleMouseUp);
        window.removeEventListener("resize", handleResize);
        canvasRef.removeEventListener("click", handleCanvasClick);
        document.removeEventListener("click", handleOutsideClick);
    });

    /**
     * Returns the position x of the pop up in the canvas
     * @returns {number} the position x of the popup in the canvas
     */
    const popupCanvasX = () => {
        return canvasPositionFromOriginal(popupX(), popupY())[0];
    };

    /**
     * Returns the position y of the pop up in the canvas
     * @returns {number} the position y of the popup in the canvas
     */
    const popupCanvasY = () => {
        return canvasPositionFromOriginal(popupX(), popupY())[1];
    };

   
    /**
     * Detects if the popup is out of the canvas
     * @returns true if yes and false if not
     */
    const popupOutOfCanvas = () => {
        const [x, y] = canvasPositionFromOriginal(popupX(), popupY());
        return x < 0 || x > canvasRef.width || y < 0 || y > canvasRef.height;
    };

    /**
     * Draws the clicked sensor in the top of the popup
     * @param {Array[str, str]} props an array of 2 elements the [0] = background color, [1] = border color
     * @returns 
     */
    function DrawClickedSensor(props) {
        return (
            <div class={`absolute z-20 border-[2px] w-5 h-5 rounded-[50px] ${getSensorStatusColor(props.state)}`}
                style={{
                    top: `${popupCanvasY()-10}px`,
                    left: `${popupCanvasX()-10}px`
                }}>
            </div>
        );
    };
    
    return (
        <>
            <Show when={errorFront().length > 0}>
                <Alert message={errorFront()}/>
            </Show>
            <canvas
                ref={canvasRef}
                class={props.styles === undefined ? `w-full bg-white` : props.styles }
            ></canvas>
            <div class="absolute z-9 right-0 bottom-0 w-fit rounded-full flex flex-col items-center bg-white shadow-[0_0_50px_0_rgba(151,151,167,0.5)]">
                <button onClick={() => zoom(ZOOM * -1)} class="p-[10px] flex items-center justify-center">
                    <Plus class="w-5 h-5" color="black" />
                </button>

                <div class="w-full h-[1px] bg-lightgray my-auto"></div>

                <button onClick={() => zoom(ZOOM)} class="p-[10px] flex items-center justify-center">
                    <Minus class="w-5 h-5" color="black" />
                </button>
            </div>

            <Show when={isPopupVisible() && !popupOutOfCanvas() && props.interactiveMode === true}>
                <div ref={popupRef}>
                    <Show when={!clickExistingPoint()}>
                        <PlacePoint
                            top={popupCanvasY} left={popupCanvasX}
                            onSubmit={() => { if (selectedSensor() != null) positionSensorFetchRequest(props.structureId, selectedSensor().controlChip, selectedSensor().measureChip, props.selectedPlanId(), popupX(), popupY()); }}
                            inputValue={inputValue} setInputValue={setInputValue}
                            filteredOptions={filteredOptions}
                            setSelectedSensor={setSelectedSensor}
                            />
                    </Show>
                    <Show when={clickExistingPoint()}>
                        <DrawClickedSensor state={clickExistingPoint().state} />
                        <PointTooltip top={popupCanvasY} left={popupCanvasX} sensor={clickExistingPoint} onClick={() => {
                            deletePositionRequest(clickExistingPoint().controlChip, clickExistingPoint().measureChip);
                        }}/>
                    </Show>
                </div>
            </Show>
        </>
    );
}


export default StructureDetailCanvas