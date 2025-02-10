import { createSignal, onMount, onCleanup } from "solid-js";
import plan from '/src/assets/plan.png';
import StructureDetailSection from './StructureDetailSection';

function StructureDetailPlans() {
    const [ctxCanvas, setCtxCanvas] = createSignal("");
    const [zoomFactor, setZoomFactor] = createSignal(0);
    const [offsetX, setOffsetX] = createSignal(0);
    const [offsetY, setOffsetY] = createSignal(0);
    
    let img = new Image();
    let canvasRef;
    let isMouseDown = false;
    let startX = 0;
    let startY = 0;

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
        let [zoomX, zoomY] = zoomRatioFromZoomNumber(imgRatio, canvasRatio, zoom);
        ctx.drawImage(
            img,
            baseOffsetX + offsetX() - zoomX / 2,
            baseOffsetY + offsetY() - zoomY / 2,
            drawWidth + zoomX,
            drawHeight + zoomY
        );
    }

    const fixDpi = () => {
        if (!canvasRef) return;
        const dpi = window.devicePixelRatio;
        const styles = getComputedStyle(canvasRef);
        // Extract width and height from styles
        const width = parseFloat(styles.width);
        const height = parseFloat(styles.height);
        // Set canvas attributes for high-DPI screens
        canvasRef.width = width * dpi;
        canvasRef.height = height * dpi;
        canvasRef.style.width = `${width}px`;
        canvasRef.style.height = `${height}px`;
    };

    onMount(() => {
        const ctx = canvasRef.getContext('2d');
        fixDpi();
        setCtxCanvas(ctx);
        loadDetails();
        addMouseEvents();
    })

    const addMouseEvents = () => {
        canvasRef.addEventListener("wheel", handleWheel);
        canvasRef.addEventListener("mousedown", handleMouseDown);
        canvasRef.addEventListener("mousemove", handleMouseMove);
        canvasRef.addEventListener("mouseup", handleMouseUp);
        canvasRef.addEventListener("mouseout", handleMouseUp);
        window.addEventListener("resize", fixDpi);
    }

    onCleanup(() => {
        canvasRef.removeEventListener("wheel", handleWheel);
        canvasRef.removeEventListener("mousedown", handleMouseDown);
        canvasRef.removeEventListener("mousemove", handleMouseMove);
        canvasRef.removeEventListener("mouseup", handleMouseUp);
        canvasRef.removeEventListener("mouseout", handleMouseUp);
        window.removeEventListener("resize", fixDpi);
    });

    const loadAndDrawImage = (imgLink) => {
        img.src = imgLink;
        img.onload = () => drawImage();
    }

    const handleWheel = (event) => {
        event.preventDefault();
        if (event.ctrlKey) {
            const zoomChange = event.deltaY;
            setZoomFactor((prev) => Math.max(0, prev + (-1 * zoomChange)));
            drawImage();
        }
    };

    const handleMouseDown = (event) => {
        event.preventDefault();
        if (event.button === 0) {
            isMouseDown = true;
            startX = event.clientX - offsetX();
            startY = event.clientY - offsetY();
        }
    };

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

    const handleMouseUp = () => {
        isMouseDown = false;
    };

    const zoomRatioFromZoomNumber = (imgRatio, canvasRatio, zoomNumber) => {
        let zoomX, zoomY;
        if (imgRatio > canvasRatio) {
            zoomX = zoomNumber;
            zoomY = zoomNumber / imgRatio;
        } else {
            zoomX = zoomNumber * imgRatio;
            zoomY = zoomNumber;
        }
        return [zoomX, zoomY];
    };

    const loadDetails = () => {
        loadAndDrawImage(plan);
    };
    
    return (
        <div class="flex rounded-[20px] bg-E9E9EB">
            <div class="flex flex-col gap-y-[15px] w-[25%] m-5">
                <p class="prose font-poppins title">Plans</p>
                <StructureDetailSection />
            </div>
            <div class="w-[75%] rounded-[20px] bg-white">
                <canvas
                    ref={canvasRef}
                    class="p-[20px] w-full"
                ></canvas>
            </div>
        </div>
    );
}


export default StructureDetailPlans
