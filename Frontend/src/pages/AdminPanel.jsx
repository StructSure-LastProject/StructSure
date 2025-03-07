import Header from '../components/Header'
import AdminPanelBody from '../components/AdminPanelBody'
import AdminPanelBottom, { fetchLogs } from '../components/AdminPanelBottom'
import { createSignal } from "solid-js";
import { useNavigate, useSearchParams } from '@solidjs/router';

/**
 * The admin panel page
 * @returns the page for the Admin panel page
 */
const AdminPanel = () => {
  const [searchParams, setSearchParams] = useSearchParams();
  const [search, setSearch] = createSignal("");
  const [pageSize, setPageSize] = createSignal(30);
  const [page, setPage] = createSignal(
    searchParams.offset ? parseInt(searchParams.offset, 10) / pageSize() : 0
  );
  const [totalItems, setTotalItems] = createSignal((page() + 1) * pageSize());
  const [logs, setLogs] = createSignal([]);
  const navigate = useNavigate();

  return (
    <div class="bg-lightgray min-h-screen p-[25px]">
        <Header />
        <div class="pt-[3%] flex flex-col gap-[15px] sm:mx-auto lg:max-w-[1250px] h-auto" >
            <AdminPanelBody fetchLogs={() => fetchLogs(navigate, search, page, setPage, setTotalItems, setPageSize, setLogs)} />
            <AdminPanelBottom
              search={search} setSearch={setSearch}
              page={page} setPage={setPage}
              pageSize={pageSize} setPageSize={setPageSize}
              totalItems={totalItems} setTotalItems={setTotalItems}
              logs={logs} setLogs={setLogs}/>
        </div>
    </div>
  )
}

export default AdminPanel