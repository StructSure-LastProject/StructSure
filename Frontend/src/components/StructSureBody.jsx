import {For, createResource, createSignal, createEffect, Show} from "solid-js";
import { A, useNavigate } from '@solidjs/router';
import useFetch from '../hooks/useFetch';
import { TriangleAlert, CircleAlert, Check, SquareDashed, FolderSync } from 'lucide-solid';
import StructuresFilter from "./StructuresFilter";
import LstStructureHead from "./LstStructureHead";

/**
 * Component body part of the structure
 * @returns component for the structure body
 */
function StructSureBody() {
    // Shared state
    const [structures, setStructures] = createSignal([]);
    const [searchByName, setSearchByName] = createSignal("");
    const [filterValue, setFilterValue] = createSignal("");
    const [orderByColumnName, setOrderByColumnName] = createSignal("NAME");
    const [orderType, setOrderType] = createSignal("ASC");
    const [filterVisible, setFilterVisible] = createSignal(false);

    const [errorStructurePage, setErrorStructurePage] = createSignal("");

    const { fetchData, statusCode, data, error } = useFetch();

    const navigate = useNavigate();

    /**
     * Fetch the structures
     */
    const fetchStructures = async () => {
        await structuresFetchRequest(
          "/api/structures",
          searchByName(),
          filterValue(),
          orderByColumnName(),
          orderType()
        );
    };

    // Watch for changes in filter parameters and refetch
    createEffect(() => {
        searchByName();
        filterValue();
        orderByColumnName();
        orderType();
        fetchStructures();
    });

    /**
     * Fetch the structures
     * @param {String} url the url for the server
     * @param {String} searchByName the name of the structure
     * @param {String} filterValue the sort by state
     * @param {String} orderByColumnName the sort by string
     * @param {String} orderType the order by string
     */
    const structuresFetchRequest = async (url, searchByName, filterValue, orderByColumnName, orderType) => {
        const token = localStorage.getItem("token");

        const params = new URLSearchParams();
        params.append('searchByName', searchByName);
        params.append('searchByState', filterValue);
        params.append('orderByColumnName', orderByColumnName);
        params.append('orderType', orderType);

        if (filterValue === "ARCHIVED") {
            params.append('archived', "true");
            params.delete('searchByState');
        }

        const urlWithParams = `${url}?${params.toString()}`;

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

    // Initial fetch
    createResource(() => fetchStructures());

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
      <>
          <LstStructureHead
            filterVisible={filterVisible}
            setFilterVisible={setFilterVisible}
          />

          <div class={`bg-white rounded-[20px] w-full ${filterVisible() ? 'block' : 'hidden'}`}>
              <StructuresFilter
                searchByName={searchByName}
                setSearchByName={setSearchByName}
                filterValue={filterValue}
                setFilterValue={setFilterValue}
                orderByColumnName={orderByColumnName}
                setOrderByColumnName={setOrderByColumnName}
                orderType={orderType}
                setOrderType={setOrderType}
              />
          </div>

          <div class="flex flex-col lg:grid 2xl:grid lg:grid-cols-3 2xl:grid-cols-4 rounded-[20px] gap-4">
              <Show when={statusCode() === 200} fallback={
                  <h1 class="normal pl-5">{errorStructurePage()}</h1>
              }>
                  <Show when={structures().length <= 0}>
                      <h1 class="normal pl-5">Aucun ouvrage enregistré dans le système</h1>
                  </Show>
                  <For each={structures()}>
                  {(item) => (
                        <A href={`/structures/${item.id}`}>
                            <div
                              class="flex items-center bg-white 2xl:w-300px px-[20px] py-[15px] rounded-[20px] gap-x-[20px] w-full">
                                <div class="w-7 h-7 flex justify-center items-center">
                                    {getIconFromStateAndArchived(item.state, item.archived)}
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
      </>
    );
}

export default StructSureBody;