import Header from '../components/Header'
import AdminPanelBody from '../components/AdminPanelBody'

const AdminPanel = () => {
  return (
    <div class="bg-gray-100 h-screen p-[1%]">
        <Header />
        <div class="pt-[3%] ml-[15%] mr-[15%] w-[1250px] h-[360px]" >
            <AdminPanelBody />
        </div>
    </div>
  )
}

export default AdminPanel