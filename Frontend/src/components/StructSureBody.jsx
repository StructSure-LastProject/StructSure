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

    const [error, setError] = createSignal("");

    const navigate = useNavigate();

    /**
     * Fetch the structures
     * @param {String} url the url for the server
     * @param {String} name the name of the structure
     * @param {String} sortBy the sort by string
     * @param {String} orderBy the order by string
     */
    const structuresFetchRequest = async (url) => {
        const token = localStorage.getItem("token");        
        
        const requestData = {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            }
        };

        const { fetchData, statusCode, data, errorFetch } = useFetch();
        await fetchData(url, requestData);
 

        if (statusCode() === 200) {
            const res = data()
            setStructures(res);
        } else if (statusCode() === 401) {
            navigate("/login");
        } else {
            setError(errorFetch());
        }
    };

    createResource(() => structuresFetchRequest("/api/structures"));

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
            <For each={structures()}>
                {(item) => (
                    <A href={`/structures/${item.id}`}>
                        <div class="flex items-center bg-white 2xl:w-300px px-[20px] py-[15px] rounded-[20px] gap-x-[20px] w-full">
                            <div class="w-7 h-7 flex justify-center items-center">
                                { getIconFromStateAndArchived(item.state, item.archived) }
                            </div>
                            <div class="flex flex-col">
                                <h1 class="font-poppins poppins text-base font-semibold text-base">{item.name}</h1>
                                <p class="font-poppins poppins text-base font-normal text-sm text-[#181818]/50">{item.numberOfSensors} capteurs</p>
                            </div>
                        </div>
                    </A>
                )}
            </For>
        </div>
    );
}

export default StructSureBody

