
import logo from '/src/assets/logo.svg';
import loginIconBlack from '/src/assets/loginIconBlack.svg';
import Header from '../../components/Header';
import { createSignal } from "solid-js";
import { useNavigate } from '@solidjs/router';
import StructSure from '../../components/Structure';


function Home() {

    return (
        <div class="p-25px bg-gray-100 h-screen">
            <Header />
            <StructSure />
        </div>
    );
}

export default Home
