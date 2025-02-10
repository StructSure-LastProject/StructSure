import logo from '/src/assets/logo.svg';

function NeedsAuthentification() {

    return (
        <div class="p-25px mb-35px">
            <div class="flex justify-center">
                <img src={logo} class="w-236px" alt="Vite logo" />
            </div>
            <p>{localStorage.getItem("token")}</p>
        </div>
    );
}

export default NeedsAuthentification
