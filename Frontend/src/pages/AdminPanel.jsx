import Header from '../components/Header'
import AdminPanelBody from '../components/AdminPanelBody'

const AdminPanel = () => {
  return (
    <div class="bg-gray-100 h-screen p-[1%]">
        <Header />
        <div class="pt-[3%] mx-auto max-w-[1250px] h-[662px]" >
            <AdminPanelBody />
        </div>
    </div>
  )
}

export default AdminPanel