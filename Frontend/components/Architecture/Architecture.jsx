import {createSignal, onMount} from "solid-js";

const Ouvrages = () => {
    const [architecture, setArchitecture] = createSignal([]);

    const fetchArchitectures = async () => {
        try {
            const response = await fetch("/api/ouvrages");
            if (!response.ok) throw new Error(await response.text());
            const data = await response.json()
            setArchitecture(data);
        } catch (error) {
            console.error("Erreur : ", error);
        }
    };

    onMount(() => {
        fetchArchitectures();
    })

    return (
        <div class={"bg-gray-100 min-h-screen p-4"}>
            <header class={"flex items-center justify-between mb-6"}>
                <h1 class={"text-2xl font-bold"}>Ouvrages</h1>
                <div class={"flex space-x-4"}>
                    <button class={"p-2 bg-gray-200 rounded-full hover:bg-grey-300"}>ASC/DESC</button>
                    <button class={"p-2 bg-gray-200 rounded-full hover:bg-grey-300"}>SORT</button>
                    <button class={"p-2 bg-gray-200 rounded-full hover:bg-grey-300"}>ADD</button>
                </div>
            </header>
            <div class={"grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4"}>
                {architecture().map((architecture) => (
                    <div class={"p-4 bg-white shadow rounded-lg flex items-center"}>
                        <span class={"text-2xl mr-4"}>ICON</span>
                        <div>
                            <h2 class={"font-semibold"}>{architecture.name}</h2>
                            <p class={"text-gray-500"}>{architecture.note}</p>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
};
