import {For, createResource, createEffect} from "solid-js";
import { X } from 'lucide-solid';
import LogDetails from "./LogDetails"
import { useNavigate } from '@solidjs/router';
import useFetch from '../hooks/useFetch';
import { Pagination } from './Pagination.jsx';

/**
 * Fetch the logs
 */
export const fetchLogs = async (navigate, search, page, setTotalItems, setPageSize, setLogs) => {
    const { fetchData, statusCode, data } = useFetch();

    const requestData = {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({
            search: search(),
            page: page()
        })
    };

    await fetchData(navigate, "/api/logs", requestData);

    if (statusCode() === 200) {
        setTotalItems(data().total);
        setPageSize(data().pageSize);
        setLogs(data().logs);
    }
};

/**
 * The admin panel bottom component 
 * @param {Function} search getter for the search field value
 * @param {Function} setSearch setter for the search fieldl value
 * @returns the component for the admin panel
 */
const AdminPanelBottom = ({
    search, setSearch,
    page, setPage,
    pageSize, setPageSize,
    totalItems, setTotalItems,
    logs, setLogs
}) => {
    const navigate = useNavigate();

    /**
     * Alias to get offset value from the page variable
     * @return {Integer} the current offset
     */
    const offset = () => {
        return page() * pageSize();
    }

    /**
     * Alias to set offset value in the page variable
     * @param {Integer} offset the number of entries to skip
     */
    const setOffset = (offset) => {
        setPage(offset / pageSize())
    }
    
    // Watch for changes in filter parameters and refetch
    createEffect(() => fetchLogs(navigate, search, page, setTotalItems, setPageSize, setLogs));
    createResource(() => fetchLogs(navigate, search, page, setTotalItems, setPageSize, setLogs));

    return (
        <>
            <div class="max-w-[1250px] w-full flex flex-row flex-wrap justify-between pt-[25px] pl-[20px] gap-[10px]">
                <h1 class="title">Logs</h1>
                <div class="flex justify-between bg-white min-w-[200px] gap-[10px] py-[8px] px-[16px] rounded-full">
                    <input
                        class="text-[#181818] bg-white w-full" 
                        type="text"
                        placeholder="Rechercher"
                        minLength="1"
                        maxLength="128"
                        value={search()}
                        onInput={(e) => setSearch(e.target.value)} 
                    />
                    <button onClick={(e) => {
                            e.preventDefault();
                            setSearch("")
                        }
                    } class="flex justify-end items-center w-[24px] h-[24px] sm:w-[24px] sm:h-[24px]">
                        <X/>
                    </button>
                </div>
            </div>
            <div class="flex flex-col max-w-[1250px] w-full lg:px-10 sm:px-4 gap-[10px] mx-auto">
                <For each={logs()}>
                    {
                        (item) => (
                            <LogDetails time={item.time} author={item.author} logMessage={item.message}/>
                        )
                    }
                </For>
            </div>
            <Pagination limit={pageSize} offset={offset} setOffset={setOffset} totalItems={totalItems}/>
        </>
    )
}

export default AdminPanelBottom