import { createSignal, onMount, onCleanup, Show, createEffect } from "solid-js";
import { Check, ChevronDown, Plus, Trash2 } from 'lucide-solid';
import planImg from '/src/assets/plan.png';
import ModalEditPlan from '../Plan/ModalEditPlan';
import ModalAddPlan from '../Plan/ModalAddPlan';
import DropdownsSection from "../Plan/DropdownsSection.jsx";
import StructureDetailSection from './StructureDetailSection';

/**
 * Shows the plans part
 * @returns the component for the plans part
 */
function StructureDetailPlans(props) {
    const imageMoveLimit = 10;
    const SENSOR_POINT_SIZE = 10;
    const ZOOM_LIMIT = 20;
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
    const [error, setError] = createSignal("");

    const [isPopupVisible, setIsPopupVisible] = createSignal(false);
    const [popupX, setPopupX] = createSignal(0);
    const [popupY, setPopupY] = createSignal(0);
    const [posX, setPosX] = createSignal(-1);
    const [posY, setPosY] = createSignal(-1);
    const [clickExistingPoint, setClickExistingPoint] = createSignal(null);

    const [cClickX, setCClickX] = createSignal(0);
    const [cClickY, setCClickY] = createSignal(0);

    // Gestion des plans
    const [plans, setPlans] = createSignal([]);
    const [selectedPlanId, setSelectedPlanId] = createSignal(null);
    const [isAddModalOpen, setIsAddModalOpen] = createSignal(false);
    const [isEditModalOpen, setIsEditModalOpen] = createSignal(false);
    const [selectedPlan, setSelectedPlan] = createSignal(null);

    const img = new Image();
    let canvasRef;
    let isMouseDown = false;
    let startX = 0;
    let startY = 0;

    // Gestion des modaux
    const openAddModal = () => setIsAddModalOpen(true);
    const closeAddModal = () => setIsAddModalOpen(false);
    const closeEditModal = () => {
        setIsEditModalOpen(false);
        setSelectedPlan(null);
    };

    const getImageUrl = (planId) => {
        const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';
        return `${API_BASE_URL}/api/structures/plans/${planId}/image`;
    };

    const handleEdit = (planId) => {
        const plan = plans().find(p => p.id === planId);
        if (plan) {
            setSelectedPlan({
                ...plan,
                imageUrl: getImageUrl(plan.id)
            });
            setIsEditModalOpen(true);
        }
    };

    const handleEditSave = (updatedPlan) => {
        setPlans(prev => prev.map(plan =>
          plan.id === updatedPlan.id
            ? { ...plan, ...updatedPlan }
            : plan
        ));
        closeEditModal();
    };

    // Gestion de l'ajout d'un nouveau plan
    const handleAddSave = (result) => {
        const newPlan = {
            id: result.id,
            name: result.name || "",
            section: result.section || "",
            type: "plan", // Type par défaut pour les nouveaux plans
            createdAt: result.createdAt || Date.now()
        };

        setPlans(prev => [...prev, newPlan]);
        closeAddModal();
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
        props.planSensors.forEach(sensor => {
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
                    setError("L'etat du sensor inconnu");
                    break;
            }

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
        });
    };

    /**
     * Handles the resize event
     */
    const handleResize = () => {
        fixDpi();
        drawImage();
    };

    /**
     * Fixes the dpi for the canvas
     */
    const fixDpi = () => {
        if (!canvasRef || !canvasRef.parentElement) return;
        const dpi = window.devicePixelRatio;
        
        // Récupérer la vraie taille du parent du canvas
        const parent = canvasRef.parentElement;
        const width = parent.clientWidth;
        const height = parent.clientHeight;
    
        // Appliquer ces dimensions au canvas
        canvasRef.width = width * dpi;
        canvasRef.height = height * dpi;
        canvasRef.style.width = `${width}px`;
        canvasRef.style.height = `${height}px`;
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
     * Handles the canvas click
     * @param {Event} event the event of the click in the canvas
     */
    const handleCanvasClick = (event) => {
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
        const clickedSensor = findClickedSensor(px, py);
        if (clickedSensor) {
            setClickExistingPoint(clickedSensor);
            let [sensorX, sensorY] = canvasPositionFromOriginal(clickedSensor.x, clickedSensor.y);
            setPopupX(sensorX);
            setPopupY(sensorY);
        } else {
            setClickExistingPoint(null);
            setPopupX(x);
            setPopupY(y);
        }
        setPosX(Math.round(px));
        setPosY(Math.round(py));
        setIsPopupVisible(true);
    };

    /**
     * Returns the clicked existing point if it exists, otherwise returns null
     * @param {number} x - The x position of the click in the original image
     * @param {number} y - The y position of the click in the original image
     * @returns {object|null} - The clicked sensor object or null if no match
     */
    const findClickedSensor = (x, y) => {
        return props.planSensors.find(sensor => {
            const distance = Math.sqrt(
                Math.pow(x - sensor.x, 2) + Math.pow(y - sensor.y, 2)
            );

            return distance <= SENSOR_POINT_SIZE;
        }) || null;
    };

    /**
     * Loads and draws the image from it's link
     * @param {String} imgLink the link of the image
     */
    const loadAndDrawImage = (imgLink) => {
        img.src = imgLink;
        img.onload = () => drawImage();
    }

    /**
     * Handles the mouse wheel event
     * @param {MouseEvent} event the mouse event
     */
    const handleWheel = (event) => {
        event.preventDefault();
        if (event.ctrlKey) {
            const zoomChange = event.deltaY;
            const newZoom = Math.max(0, zoomFactor() + (-1 * zoomChange));
            const imgStartX = getImgStartX(baseOffsetX(), offsetX(), newZoom);
            const imgStartY = getImgStartY(baseOffsetY(), offsetY(), newZoom);
            const [zoomX, zoomY] = getZoomRationFromZoomNumber(newZoom);
            const imgWidth = drawWidth() + zoomX;
            const imgHeight = drawHeight() + zoomY;
            if ((imgStartX + imgWidth) < imageMoveLimit || imgStartX > canvasRef.width - imageMoveLimit
             || (imgStartY + imgHeight) < imageMoveLimit || imgStartY > canvasRef.height - imageMoveLimit
             || zoomLimitReached(imgWidth, imgHeight)) {
                return;
            }
            setZoomFactor(newZoom);
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
     * Handles the mouse move event
     * @param {MouseEvent} event the event of the mouse
     */
    const handleMouseMove = (event) => {
        event.preventDefault();
        if (isMouseDown) {
            const dx = event.clientX - startX;
            const dy = event.clientY - startY;
            const imgStartX = getImgStartX(baseOffsetX(), dx, zoomFactor());
            const imgStartY = getImgStartY(baseOffsetY(), dy, zoomFactor());
            const [zoomX, zoomY] = getZoomRationFromZoomNumber(zoomFactor());
            const imgWidth = drawWidth() + zoomX;
            const imgHeight = drawHeight() + zoomY;
            if ((imgStartX + imgWidth) < imageMoveLimit || imgStartX > canvasRef.width - imageMoveLimit
             || (imgStartY + imgHeight) < imageMoveLimit || imgStartY > canvasRef.height - imageMoveLimit) {
                return;
            }
            setOffsetX(dx);
            setOffsetY(dy);
            drawImage();
        }
    };

    /**
     * Changes state of isMouseDown variable when mouse is up
     */
    const handleMouseUp = () => {
        isMouseDown = false;
    };

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
     * Handles the click outside the canvas
     * @param {Event} event the click in the page
     */
    const handleOutsideClick = (event) => {
        if (canvasRef && !canvasRef.contains(event.target)) {
            setIsPopupVisible(false);
        }
    };

    /**
     * Loads the details (images and draw it)
     */
    const loadDetails = () => {
        loadAndDrawImage(planImg);
    };

    // Ajouter cet effet juste avant le return
    createEffect(() => {
        console.log("Plans reçus:", props.plans);
        console.log("Structure des plans:", JSON.stringify(props.plans, null, 2));
        if (props?.plans) {
            // Initialise les plans avec ceux reçus des props
            setPlans(props.plans.map(plan => ({
                ...plan,
                type: plan.type || "plan" // Assure qu'il y a toujours un type
            })));
            console.log("plan saved:", plans())
        }
    });

    return (
      <>
        <div class="flex flex-col lg:flex-row rounded-[20px] max-h-[436px] bg-E9E9EB">
            <div class="flex flex-col gap-y-[15px] lg:w-[25%] m-5">
                <div class="flex items-center justify-between">
                    <p class="prose font-poppins title">Plans</p>
                    <button
                      title="Ajouter un plan"
                      onClick={openAddModal}
                      class="bg-white rounded-[50px] h-[40px] w-[40px] flex items-center justify-center"
                    >
                        <Plus color="black"/>
                    </button>
                </div>
                <div class="flex flex-col gap-y-[5px] overflow-y-auto [&::-webkit-scrollbar]:hidden [-ms-overflow-style:none] [scrollbar-width:none]">
                    <DropdownsSection
                      data={plans()}
                      selectedPlanId={selectedPlanId()}
                      onEdit={handleEdit}
                      onPlanEdit={handleEditSave}
                      structureId={props.structureId}
                    />
                </div>
                <Show when={isAddModalOpen()}>
                    <ModalAddPlan
                      isOpen={isAddModalOpen()}
                      onSave={handleAddSave}
                      onClose={closeAddModal}
                      structureId={props.structureId}
                    />
                </Show>

                <Show when={isEditModalOpen() && selectedPlan()}>
                    <ModalEditPlan
                      isOpen={isEditModalOpen()}
                      onSave={handleEditSave}
                      onClose={closeEditModal}
                      structureId={props.structureId}
                      plan={selectedPlan()}
                    />
                </Show>
            </div>
          <div class="lg:w-[75%] rounded-[20px] bg-white">
            <div class="w-full m-[20px] relative">
              <canvas
                ref={canvasRef}
                class="w-full"
              ></canvas>
              <Show when={isPopupVisible()}>
                <Show when={!clickExistingPoint()}>
                  <div class="absolute z-20 border-4 border-black w-5 h-5 bg-white rounded-[50px]"
                       style={{
                         top: `${popupY()-10}px`,
                         left: `${popupX()-10}px`,
                       }}>
                  </div>
                </Show>
                <div
                  class="absolute z-10 w-[351px] h-[275px] rounded-tr-[20px] rounded-b-[20px] bg-white px-5 py-[15px] flex-col gap-y-[15px] shadow-[0_0_100px_0_rgba(151,151,167,0.5)]"
                  style={{
                    top: `${popupY()}px`,
                    left: `${popupX()}px`,
                  }}
                >
                  <div class="w-full flex justify-between items-center">
                    <h1 class="title poppins text-[25px] font-semibold">Ouvrages</h1>
                    <div class="flex gap-x-[10px]">
                      <button class="bg-E9E9EB rounded-[50px] h-[40px] w-[40px] flex items-center justify-center">
                        <Check color="black"/>
                      </button>
                      <button class="bg-[#F133271A] rounded-[50px] h-[40px] w-[40px] flex items-center justify-center">
                        <Trash2 color="red"/>
                      </button>
                    </div>
                  </div>
                  <div class="flex flex-col gap-y-[5px]">
                    <p class="HeadLineMedium poppins font-normal">Capteur</p>
                    <div class="bg-E9E9EB px-[16px] py-[8px] rounded-[20px] flex justify-between items-center">
                      <h1 class="font-poppins poppins text-[16px] font-semibold">Capteur P</h1>
                      <button class="rounded-[50px] h-[24px] w-[24px] flex items-center justify-center">
                        <ChevronDown color="black" />
                      </button>
                    </div>
                    <div class="rounded-[10px] py-[10px] px-[20px] flex flex-col gap-y-[10px]">
                      <p class="font-poppins poppins font-normal text-14px/[21px]">Capteur PA</p>
                      <div class="w-full h-[1px] bg-[#F6F6F8]"></div>
                      <p class="font-poppins poppins font-normal text-14px/[21px]">Capteur P8S</p>
                      <div class="w-full h-[1px] bg-[#F6F6F8]"></div>
                      <p class="font-poppins poppins font-normal text-14px/[21px]">Capteur P8N</p>
                    </div>
                  </div>
                </div>
              </Show>
            </div>
          </div>
        </div>
      </>
    );
}


export default StructureDetailPlans