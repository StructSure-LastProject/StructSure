import { createResource, createSignal } from 'solid-js';
import StructureNameCard from '../StructureNameCard';
import useFetch from '../../hooks/useFetch';

export const AccountStructureSection = ({copyOfstructureSelection, setStructureSelection, isAccountCreation}) => {

    const [structures, setStructures] = createSignal();


    const add = (structureId, hasAccess = false) => {
        setStructureSelection((prevArray) => {
            const newItem = {structureId: structureId, hasAcces: hasAccess};
            if (!prevArray.includes(newItem)) {
                return [...prevArray, newItem];
            }
            return newArray;
        });
    }
    
    const remove = (structureId) => {
        setStructureSelection((prevArray) => {
            const newArray = prevArray.filter(item => item.structureId !== structureId);
            return newArray;
        });
    }


    const getStructuresForAccount = async () => {
        const { fetchData, data, statusCode } = useFetch();
        const token = localStorage.getItem("token");
        const login = localStorage.getItem("login");

        const requestData = {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            }
        };

        await fetchData(`/api/accounts/${login}/structures`, requestData);

        if (statusCode() === 200) {
            setStructures(data().structureDetailsList);
            
            structures().forEach(structure => {
                if (structure.hasAccess === true) {
                    add(structure.structureId, true);
                }
            });
        }

    }


    createResource(() => {
        if (!isAccountCreation) {
            getStructuresForAccount()
        }
    });
   
    

    return (
        <div class="w-[100%] h-auto flex flex-wrap gap-[10px]">              
            <Show when={structures() !== undefined}>
                <For each={structures()}>
                    {(item) => (
                        <StructureNameCard add={add} remove={remove} structureId={item.structureId} structureName={item.structureName} isSelected={item.hasAccess}/>
                    )}
                </For>
            </Show>
        </div>
    )
}

export default AccountStructureSection