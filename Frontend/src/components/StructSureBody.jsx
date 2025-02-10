import logo from '/src/assets/logo.svg';
import check from '/src/assets/check.svg';
import { For, createResource, createSignal } from "solid-js";
import { useNavigate } from '@solidjs/router';
import useFetch from '../hooks/useFetch';


function StructSureBody() {
    const [structures, setStructures] = createSignal([]);

    const [error, setError] = createSignal("");

    const navigate = useNavigate();

    const structuresFetchRequest = async (url, name, sortBy, orderBy) => {
        const token = localStorage.getItem("token");
        const params = new URLSearchParams({
            searchByName: name,
            sort: sortBy,
            order: orderBy
        }).toString();
        const requestUrl = `${url}?${params}`;
        
        
        const requestData = {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            }
        };

        const { fetchData, statusCode, data, error } = useFetch();
        await fetchData(requestUrl, requestData);
 

        if (statusCode() === 200) {
            const res = data()
            setStructures(res);
        } else if (statusCode() === 401) {
            navigate("/login");
        } else {
            setError(error());
        }
    };

    createResource(() => structuresFetchRequest("/api/structures", "", "NAME", "ASC"));

    return (
        
        <div class="flex flex-wrap gap-x-4 gap-y-4">
            <For each={structures()}>
                {(item) => (
                    <div class="flex bg-white w-300px px-5 px-15px rounded-20px gap-x-20px">
                        <img src={check} class="" alt="Icon shwoin an alter" />
                        <div class="flex flex-col">
                            <h1 class="text-lg">{item.name}</h1>
                            <p class="text-sm text-gray-500">{item.numberOfSensors}</p>
                        </div>
                    </div>
                )}
            </For>
        </div>
    );
}

export default StructSureBody

