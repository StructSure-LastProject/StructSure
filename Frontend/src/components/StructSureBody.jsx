import check from '/src/assets/check.svg';
import { For, createResource, createSignal } from "solid-js";
import { useNavigate } from '@solidjs/router';
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

    createResource(() => structuresFetchRequest("http://localhost:8080/api/structures"));

    const getIconFromStateAndArchived = (state, archived) => {
        if (archived == true) {
            return <FolderSync color='#6A6A6A' class="w-full" />;
        }
        switch (state) {
            case "NOK":
                return <TriangleAlert color='#F13327' className="w-full" />;
            case "DEFAULTER":
                return <CircleAlert color='#F19327' className="w-full" />;
            case "OK":
                return <Check color='#25B61F' className="w-full" />;
            case "UNKNOWN":
                return <SquareDashed color='#6A6A6A' className="w-full" />;
        }
    };

    return (
        
        <div class="flex flex-wrap gap-x-4 gap-y-4">
            <For each={structures()}>
                {(item) => (
                    <div class="flex items-center bg-white w-300px px-[20px] py-[15px] rounded-[20px] gap-x-[20px]">
                        <div class="w-7 h-7 flex justify-center items-center">
                            { getIconFromStateAndArchived(item.state, item.archived) }
                        </div>
                        <div class="flex flex-col">
                            <h1 class="font-poppins poppins text-base font-semibold text-base">{item.name}</h1>
                            <p class="font-poppins poppins text-base font-normal text-sm text-[#181818]/50">{item.numberOfSensors} capteurs</p>
                        </div>
                    </div>
                )}
            </For>
        </div>
    );
}

export default StructSureBody

