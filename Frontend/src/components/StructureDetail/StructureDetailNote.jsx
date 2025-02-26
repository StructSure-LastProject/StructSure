
/**
 * Show the note part
 * @returns the component for the note part
 */
function StructureDetailNote({selectedScan, structureDetails}) {
    
    return (
        <div class="w-full lg:w-[30%] bg-white rounded-[20px] flex flex-col gap-y-[15px] px-[20px] py-[15px] h-fit">
            <p class="title">Note</p>
            <div class="rounded-[10px] px-[16px] py-[8px] bg-lightgray">
                <p class="font-poppins normal font-normal">
                    <Show when={selectedScan() === -1} fallback={
                        "Favoriser des allers-retours du nord vers le sud pour pouvoir scanner tous les capteurs."
                    }>
                        {structureDetails().note}
                    </Show>
                </p>
            </div>
        </div>
    );
}

export default StructureDetailNote
