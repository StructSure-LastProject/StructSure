
/**
 * Show the note part
 * @returns the component for the note part
 */
function StructureDetailNote() {
    
    return (
        <div class="w-full lg:w-[30%] bg-white rounded-[20px] flex flex-col gay-y-[15px] px-[15px] py-[20px] h-fit">
            <p class="prose font-poppins title">Note</p>
            <div class="rounded-[10px] px-[16px] py-[8px] bg-gray-100">
                <p class="font-poppins normal font-normal">Favoriser des allers-retours du nord vers le sud pour pouvoir scanner tous les capteurs.</p>
            </div>
        </div>
    );
}

export default StructureDetailNote
