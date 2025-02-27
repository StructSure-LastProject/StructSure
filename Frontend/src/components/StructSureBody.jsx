import { For, createResource, createSignal } from "solid-js";
import { A, useNavigate } from '@solidjs/router';
import useFetch from '../hooks/useFetch';
import { TriangleAlert, CircleAlert, Check, SquareDashed, FolderSync } from 'lucide-solid';


/**
 * Component body part of the structure
 * @returns component for the structure body
 */
function StructSureBody() {
    const [structures, setStructures] = createSignal([]);
    const [errorStructurePage, setErrorStructurePage] = createSignal("");

    const { fetchData, statusCode, data, error } = useFetch();
    
    const navigate = useNavigate();

    /**
     * Fetch the structures
     * @param {String} url the url for the server
     * @param {String} containsName the name of the structure
     * @param {String} orderBy the sort by string
     * @param {String} orderBy the order by string
     */
    const structuresFetchRequest = async (url, searchByName, orderByColumnName, orderType) => {
        const token = localStorage.getItem("token");       
        const urlWithParams = `${url}?searchByName=${encodeURIComponent(searchByName)}&orderByColumnName=${encodeURIComponent(orderByColumnName)}&orderType=${encodeURIComponent(orderType)}`;
        
        const requestData = {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            }
        };

        await fetchData(navigate, urlWithParams, requestData);

        if (statusCode() === 200) {
            const res = data()
            setStructures(res);   
        } else {
            setErrorStructurePage(error().errorData.error);
        }
    };

    createResource(() => structuresFetchRequest("/api/structures", "", "STATE", "DESC"));

    /**
     * Return the corresponding icon based on the state and if the structure is archived or not
     * @param {String} state the state of the strucutre
     * @param {Boolean} archived true if archived and false if not
     * @returns  the componenent containing the icon
     */
    const getIconFromStateAndArchived = (state, archived) => {
        if (archived == true) {
            return <FolderSync color='#6A6A6A' class="w-full" />;
        }
        switch (state) {
            case "NOK":
                return <TriangleAlert color='#F13327' className="w-full" />;
            case "DEFECTIVE":
                return <CircleAlert color='#F19327' className="w-full" />;
            case "OK":
                return <Check color='#25B61F' className="w-full" />;
            case "UNKNOWN":
                return <SquareDashed color='#6A6A6A' className="w-full" />;
        }
    };

    return (
        
        <div class="flex flex-col lg:grid 2xl:grid lg:grid-cols-3 2xl:grid-cols-4 rounded-[20px] gap-4">
            <Show when={statusCode() === 200} fallback={
                <h1 class="normal pl-5">{errorStructurePage()}</h1>
            }>
                <For each={structures()}>
                    {(item) => (
                        <A href={`/structures/${item.id}`}>
                            <div class="flex items-center bg-white 2xl:w-300px px-[20px] py-[15px] rounded-[20px] gap-x-[20px] w-full">
                                <div class="w-7 h-7 flex justify-center items-center">
                                    { getIconFromStateAndArchived(item.state, item.archived) }
                                </div>
                                <div class="flex flex-col">
                                    <h1 class="subtitle">{item.name}</h1>
                                    <p class="normal opacity-50">{item.numberOfSensors} capteurs</p>
                                </div>
                            </div>
                        </A>
                    )}
                </For>
            </Show>
        </div>
    );
}

export default StructSureBody

