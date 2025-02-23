import Header from '../components/Header';
import StructSure from '../components/Structure';

/**
 * Component for the home page
 * @returns component for the home page
 */
function Home() {

    return (
        <div class="p-25px bg-lightgray min-h-screen">
            <Header />
            <StructSure />
        </div>
    );
}

export default Home
