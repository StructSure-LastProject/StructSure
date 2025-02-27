import StructureNameCard from '../StructureNameCard';

/**
 * The structure access section of user account
 * @param {Array} structures The list of structures
 * @param {Function} setStructureSelection The setter for structure access change
 * @returns The Account structure section component
 */
export const AccountStructureSection = ({structures, setStructureSelection}) => {


    /**
     * The toogle function
     * @param {String} structureId The structure id
     */
    const toggle = (structureId) => {
        setStructureSelection((prevArray = []) => {
            const newItemIndex = prevArray.findIndex(item => item.structureId === structureId);
            if (newItemIndex !== -1) {
                const updatedArray = [...prevArray];
                updatedArray[newItemIndex].hasAccess = !updatedArray[newItemIndex].hasAccess;
                return updatedArray;
            }
            return prevArray;
        });
    }
    

    return (
        <div class="w-[100%] h-auto flex flex-wrap gap-[10px]">             
            <Show when={structures() !== undefined}>
                <For each={structures()}>
                    {(item) => (
                        <StructureNameCard toggle={toggle} structureId={item.structureId} structureName={item.structureName} isSelected={item.hasAccess}/>
                    )}
                </For>
            </Show>
        </div>
    )
}

export default AccountStructureSection