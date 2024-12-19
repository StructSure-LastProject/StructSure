import { createSignal } from "solid-js";
// import solidLogo from './assets/solid.svg'
import Structure from "./components/Structure";

function App() {
  const [count, setCount] = createSignal(0);

  return (
    <>
      <Structure />
    </>
  );
}

export default App;
