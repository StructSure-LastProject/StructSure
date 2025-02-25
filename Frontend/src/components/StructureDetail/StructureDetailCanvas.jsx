import { createSignal, onMount, onCleanup, Show, createEffect, createMemo } from "solid-js";
import { Check, ChevronDown, Trash2 } from 'lucide-solid';
import useFetch from '../../hooks/useFetch';
import Alert from '../Alert';
import getSensorStatusColor from "../SensorStatusColorGen";

/**
 * Shows the plans part
 * @returns the component for the plans part
 */
function StructureDetailCanvas(props) {
    const IMAGE_MOVE_LIMIT = 10;
    const SENSOR_POINT_SIZE = 10;
    const ZOOM_LIMIT = 5;
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
    const [isOpen, setIsOpen] = createSignal(false);

    const [selectedSensor, setSelectedSensor] = createSignal(null);

    const filteredOptions = createMemo(() => {
        if (!props.structureDetails().sensors) return [];
        return props.structureDetails().sensors.filter(detailSensor =>
            detailSensor.x == null && detailSensor.y == null & detailSensor.name?.includes(inputValue() || "")
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
            props.setPlanSensors(props.planSensors().map(sensor =>
                sensor.controlChip === selectedSensor().controlChip && sensor.measureChip === selectedSensor().measureChip ? { ...sensor, x: parseInt(newSensor.x), y: parseInt(newSensor.y) } : sensor
            ));
            props.setSensors(props.structureDetails().sensors.map(sensor =>
                sensor.controlChip === selectedSensor().controlChip && sensor.measureChip === selectedSensor().measureChip ? { ...sensor, x: parseInt(newSensor.x), y: parseInt(newSensor.y) } : sensor
            ));
            setSelectedSensor(null);
            setInputValue("");
            setIsPopupVisible(false);    
            drawImage();
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
    
        await fetchData(requestUrl, requestData);
    
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
         * Loads and draws the image from it's link
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
        loadAndDrawImage(props.plan);
    };

    /**
     * This function is called in the begning when this component is mounted in the DOM
     */
    onMount(() => {
        const ctx = canvasRef.getContext('2d');
        fixDpi();
        setCtxCanvas(ctx);
        loadDetails();
        addMouseEvents();
        createEffect(() => {
            drawImage();
        });
    })

    /**
     * Handles the click outside the canvas
     * @param {Event} event the click in the page
     */
    const handleOutsideClick = (event) => {
        if ((canvasRef && !canvasRef.contains(event.target)) && (popupRef && !popupRef.contains(event.target))
        && (refLstOfSensors && !refLstOfSensors.contains(event.target))) {
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
     * Handles the mouse wheel event
     * @param {MouseEvent} event the mouse event
     */
    const handleWheel = (event) => {
        event.preventDefault();
        if (event.ctrlKey) {
            setIsPopupVisible(false);
            const zoomChange = event.deltaY;
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
            <Show when={isPopupVisible() && !popupOutOfCanvas() && props.interactiveMode === true}>
                <div ref={popupRef}>
                    <Show when={!clickExistingPoint()}>
                        <div class="absolute z-20 border-4 border-black w-5 h-5 bg-white rounded-[50px]"
                            style={{
                                top: `${popupCanvasY()-10}px`,
                                left: `${popupCanvasX()-10}px`,
                            }}>
                        </div>
                    </Show>
                    <Show when={clickExistingPoint()}>
                        <DrawClickedSensor state={clickExistingPoint().state} />
                    </Show>
                    <div 
                        class="absolute z-10 w-[351px] rounded-tr-[20px] rounded-b-[20px] flex flex-col gap-5  bg-white px-5 py-[15px] shadow-[0_0_100px_0_rgba(151,151,167,0.5)]"
                        style={{
                            top: `${popupCanvasY()}px`,
                            left: `${popupCanvasX()}px`,
                        }}
                    >
                        <div class="w-full flex justify-between items-center">
                            <h1 class="title">{clickExistingPoint() != null ? clickExistingPoint().name : "Nouveau point"}</h1>
                            <div class="flex gap-x-[10px]">
                                <Show when={!clickExistingPoint()}>
                                    <button class="bg-lightgray rounded-[50px] h-[40px] w-[40px] flex items-center justify-center" onClick={() => {
                                        if (selectedSensor() != null) {
                                            positionSensorFetchRequest(props.structureId, selectedSensor().controlChip, selectedSensor().measureChip, 1, popupX(), popupY());
                                        }
                                    }}>
                                        <Check color="black" stroke-width="2.5" width="20px" height="20px"/>
                                    </button>
                                </Show>
                                <button class="bg-[#F133271A] rounded-[50px] h-[40px] w-[40px] flex items-center justify-center">
                                    <Trash2 color="red" stroke-width="2.5" width="20px" height="20px"/>
                                </button>
                            </div>
                        </div>
                        <Show when={!clickExistingPoint()}>
                            <div class="flex flex-col gap-y-[5px]">
                                <p class="normal opacity-50">Capteur</p>

                                <div class="bg-lightgray px-[16px] py-[8px] rounded-[20px] flex justify-between items-center">
                                    <input
                                        type="text"
                                        class="bg-transparent subtitle w-full"
                                        value={inputValue()}
                                        onInput={(e) => setInputValue(e.currentTarget.value)}
                                        onFocus={() => setIsOpen(true)} // Open dropdown on focus
                                    />
                                    <button
                                        class="rounded-[50px] h-[24px] w-[24px] flex items-center justify-center"
                                        onClick={() => setIsOpen(!isOpen())}
                                    >
                                        <ChevronDown class={`transition-transform ${isOpen() ? "rotate-180" : ""}`} color="black" />
                                    </button>
                                </div>
                                <Show when={isOpen() && filteredOptions().length > 0}>
                                    <div class="rounded-[10px] py-[10px] px-[20px] flex flex-col gap-y-[10px]">
                                        {filteredOptions().map((option, index) => (
                                        <>
                                            <p
                                                class="normal cursor-pointer"
                                                onClick={(event) => {
                                                    event.stopImmediatePropagation();
                                                    setInputValue(option.name);
                                                    setSelectedSensor(option);
                                                    setIsOpen(false);
                                                }}
                                            >
                                            {option.name}
                                            </p>
                                            <Show when={index < filteredOptions().length - 1}>
                                                <div class="w-full h-[1px] bg-lightgray"></div>
                                            </Show>
                                        </>
                                        ))}
                                    </div>
                                </Show>
                            </div>
                        </Show>
                    </div>
                </div>
            </Show>
        </>
    );
}


export default StructureDetailCanvas