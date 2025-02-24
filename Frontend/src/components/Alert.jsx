import { CircleAlert } from "lucide-solid";
import { createSignal, Show } from "solid-js";

const Alert = (props) => {
  const [visible, setVisible] = createSignal(false);

  const showAlert = () => {
    setVisible(true);
    setTimeout(() => setVisible(false), 3000);
  };

  return (
    <>
      <Show when={visible()}>
        <div class="flex justify-between gap-x-5 itmes-center fixed top-5 left-1/2 transform -translate-x-1/2 bg-red100 border border-red text-red px-4 py-3 rounded">
          <div class="w-5 h-5">
            <CircleAlert color="red" />
          </div>
          <p class="text-red accent">{props.message}</p>
        </div>
      </Show>
      {showAlert()}
    </>
  );
};

export default Alert;