import { CircleAlert } from "lucide-solid";
import { createSignal, Show } from "solid-js";

/**
 * Component the shows an alert in the page for 3 seconds
*/
const Alert = (props) => {
  const [visible, setVisible] = createSignal(false);

  /**
   * Shows the alert in the page for 3 seconds
   */
  const showAlert = () => {
    setVisible(true);
    setTimeout(() => setVisible(false), 3000);
  };

  return (
    <>
      <Show when={visible()}>
        <div class="flex justify-between gap-x-5 items-center fixed top-5 left-1/2 transform -translate-x-1/2 bg-red text-white px-7 py-3 rounded-[20px] shadow-lg">
          <CircleAlert color="white" />
          <p class="accent">{props.message}</p>
        </div>
      </Show>
      {showAlert()}
    </>
  );
};

export default Alert;