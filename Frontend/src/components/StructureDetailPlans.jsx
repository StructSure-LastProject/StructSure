import { createSignal, onMount, onCleanup, createEffect } from "solid-js";
import plan from '/src/assets/plan.png';
import StructureDetailSection from './StructureDetailSection';
import ModalAddPlan from './Plan/ModalAddPlan';
import { Plus } from 'lucide-solid';

/**
 * Shows the plans part
 * @returns the component for the plans part
 */
function StructureDetailPlans() {
    const [ctxCanvas, setCtxCanvas] = createSignal("");
    const [zoomFactor, setZoomFactor] = createSignal(0);
    const [offsetX, setOffsetX] = createSignal(0);
    const [offsetY, setOffsetY] = createSignal(0);

    const [isOpen, setIsOpen] = createSignal(false);

    /**
     * Opens the modal by setting the `isOpen` state to `true`.
     * This will trigger the modal to become visible.
     * @returns {void}
     */
    const openModal = () => setIsOpen(true);
    /**
     * Closes the modal by setting the `isOpen` state to `false`.
     * This will hide the modal from view.
     * @returns {void}
     */
    const closeModal = () => setIsOpen(false);
    const [cClickX, setCClickX] = createSignal(0);
    const [cClickY, setCClickY] = createSignal(0);
    
    const img = new Image();
    let canvasRef;
    let isMouseDown = false;
    let startX = 0;
    let startY = 0;

    const lstSensors = [{x: 10, y: 200, state: "OK"}, {x : 20, y: 20, state: "NOK"}, 
        {x : 60, y: 60, state: "DEFECTIVE"}, {x : 40, y: 40, state: "UNKNOWN"}
    ];

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
        // Appliquer le zoom
        const zoom = zoomFactor();
        ctx.clearRect(0, 0, canvasRef.width, canvasRef.height);
        const [zoomX, zoomY] = zoomRatioFromZoomNumber(imgRatio, canvasRatio, zoom);
        const imgStartX = baseOffsetX + offsetX() - zoomX / 2;
        const imgStartY = baseOffsetY + offsetY() - zoomY / 2;
        ctx.drawImage(
            img,
            imgStartX,
            imgStartY,
            drawWidth + zoomX,
            drawHeight + zoomY
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
        // Facteurs d'échelle pour les capteurs
        const scaleX = (drawWidth + zoomX) / drawWidth;
        const scaleY = (drawHeight + zoomY) / drawHeight;
        lstSensors.forEach(sensor => {
            // La position relative du capteur est mise à l'échelle de la même manière que l'image
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
            }
            
            const sensorCanvasX = imgStartX + sensor.x * scaleX;
            const sensorCanvasY = imgStartY + sensor.y * scaleY;
            ctx.beginPath();
            ctx.arc(sensorCanvasX, sensorCanvasY, 10, 0, Math.PI * 2);
            ctx.fillStyle = bgColor;
            ctx.fill();
            ctx.arc(sensorCanvasX, sensorCanvasY, 12, 0, Math.PI * 2);
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
    });

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
        console.log("Clicked at:", x, y);
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
            setZoomFactor((prev) => Math.max(0, prev + (-1 * zoomChange)));
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
     * Handles the mouse move event
     * @param {MouseEvent} event the event of the mouse
     */
    const handleMouseMove = (event) => {
        event.preventDefault();
        if (isMouseDown) {
            const dx = event.clientX - startX;
            const dy = event.clientY - startY;
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
     * Loads the details (images and draw it)
     */
    const loadDetails = () => {
        loadAndDrawImage(plan);
    };
    
    return (
        <div class="flex flex-col lg:flex-row rounded-[20px] bg-E9E9EB">
            <div class="flex flex-col gap-y-[15px] lg:w-[25%] m-5">
                <div class="flex items-center justify-between">
                    <p class="prose font-poppins title">Plans</p>
                    <button 
                      title="Ajouter un plan" 
                      onClick={openModal} 
                      class="bg-white rounded-[50px] h-[40px] w-[40px] flex items-center justify-center"
                    >
                        <Plus color="black"/>
                    </button>
                </div>
                <Show when={isOpen()}>
                    <ModalAddPlan isOpen={isOpen()} onClose={closeModal} />
                </Show>
                <StructureDetailSection />
            </div>
            <div class="lg:w-[75%] rounded-[20px] bg-white">
                <canvas
                    ref={canvasRef}
                    class="p-[20px] w-full"
                ></canvas>
            </div>
        </div>
    );
}


export default StructureDetailPlans
