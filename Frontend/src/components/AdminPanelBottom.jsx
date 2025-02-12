import LogDetails from "./LogDetails"

/**
 * The admin panel bottom component 
 * @returns the component for the admin panel
 */
const AdminPanelBottom = () => {
  return (
    <>
        <div class="max-w-[1250px] w-full h-auto rounded-[20px] pt-[25px] pl-[20px] gap-[10px]">
            <h1 class="text-2xl sm:text-3xl font-poppins title">Logs</h1>
        </div>
        <div class="flex flex-col mt-[2%] max-w-[1250px] w-full h-auto gap-[10px] mx-auto">
            <LogDetails date={"13/11/2024"} time={"13h56"} logMessage={"Ouvrage supprimé : Pont de Gisclard"}/>
            <LogDetails date={"12/11/2024"} time={"13h00"} logMessage={"Ouvrage supprimé : Pont de test1"}/>
            <LogDetails date={"10/11/2024"} time={"11h00"} logMessage={"Ouvrage supprimé : Pont de test"}/>
        </div>
    </>
  )
}

export default AdminPanelBottom