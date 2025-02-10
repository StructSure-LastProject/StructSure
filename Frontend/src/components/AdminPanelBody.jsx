import add from '/src/assets/add.svg';
import AccountDetails from './AccountDetails';

const AdminPanelBody = () => {
    return (
        <>
            <div class="flex justify-between">
                <h1 class="text-2xl font-bold" >Comptes</h1>
                <img
                    src={add}
                    alt="Add Button logo"
                    class="cursor-pointer"
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