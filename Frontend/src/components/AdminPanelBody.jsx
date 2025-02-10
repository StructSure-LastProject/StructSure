import add from '/src/assets/add.svg';
import AccountDetails from './AccountDetails';

const AdminPanelBody = () => {
    return (
        <>
            <div class="flex justify-between items-center w-full max-w-[1250px] h-[40px] sm:h-[50px] rounded-[20px] pl-[20px] gap-[10px]">
                <h1 class="text-2xl sm:text-3xl font-poppins title">Comptes</h1>
                <img
                    src={add}
                    alt="Add Button logo"
                    class="cursor-pointer w-[40px] h-[40px] rounded-[50px]"
                />
            </div>

            <div class="m-[2%] flex flex-wrap gap-[15px]">
                <AccountDetails firstName={"Dupont"} lastName={"Jean"} mail={"jean.dupont@test.test"} role={"Opérateur"}/>
                <AccountDetails firstName={"Henry"} lastName={"Tom"} mail={"henry.tom@test.test"} role={"Admin"}/>
                <AccountDetails firstName={"Tom"} lastName={"cat"} mail={"tom.cat@test.test"} role={"Responsable"}/>
                <AccountDetails firstName={"Dupont"} lastName={"Jean"} mail={"jean.dupont@test.test"} role={"Opérateur"}/>
                <AccountDetails firstName={"test"} lastName={"test"} mail={"test.test@test.test"} role={"Opérateur"} isDisabled={true}/>
            </div>
        </>
    )
}

export default AdminPanelBody