import Header from '../components/Header'
import AdminPanelBody from '../components/AdminPanelBody'
import AdminPanelBottom from '../components/AdminPanelBottom'

/**
 * The admin panel page
 * @returns the page for the Admin panel page
 */
const AdminPanel = () => {
  return (
    <div class="bg-gray-100 h-screen p-[25px]">
        <Header />
        <div class="pt-[3%] flex flex-col gap-[15px] sm:mx-auto lg:max-w-[1250px] h-auto" >
            <AdminPanelBody />
            <AdminPanelBottom />
        </div>
    </div>
  )
}

export default AdminPanel