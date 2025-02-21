import {createSignal, Show} from "solid-js";
import useFetch from "../../hooks/useFetch.js";
import ModalHeader from "../Modal/ModalHeader.jsx";
import ErrorMessage from "../Modal/ErrorMessage.jsx";
import ModalField from "../Modal/ModalField.jsx";
import ModalComment from "../Modal/ModalComment.jsx";

/**
 * Modal for adding a sensor.
 * Displaus a form to enter a name, a section and an image.
 * @param {isOpen, onClose, onSave, structureId} param Props passed to the component
 * @returns The modal component for adding a plan
 */
const ModalAddSensor = ({ isOpen, onClose, onSave, structureId }) => {
  const [name, setName] = createSignal("");
  const [controlChip, setControlChip] = createSignal("");
  const [measureChip, setMeasureChip] = createSignal("");
  const [note, setNote] = createSignal("");
  const [isSubmitting, setIsSubmitting] = createSignal(false);
  const [errorMsg, setError] = createSignal("");

  /**
   * Handles the form submission for adding a new sensor.
   * Validates the form data, creates a json object and sends it to the server.
   *
   * @returns {Promise<void>} A promise that resolves when the submission is complete
   * @throws Will display an error message if the submission fails
   */
  const handleSubmit = async () => {
    setError("")
    if (!name().trim()) {
      setError("Le nom est requis");
      return;
    }
    if (!controlChip().trim()) {
      setError("La puce témoin est requise");
      return;
    }
    if (!measureChip().trim()) {
      setError("La puce de mesure est requise");
      return;
    }
    setIsSubmitting(true);
    setError("");

    const body = {
      structureId: structureId,
      name: name().trim(),
      note: note().trim(),
      measureChip: measureChip().trim(),
      controlChip: controlChip().trim()
    };

    const { fetchData, data, statusCode, error } = useFetch();

    await fetchData(`/api/sensors`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${localStorage.getItem("token")}`
      },
      body: JSON.stringify(body)
    });

    if (statusCode() === 201) {
      const result = data();
      onSave({
        id: result.id
      });
      handleClose();
    } else {
      setError(error()?.errorData?.error || "Une erreur est survenue");
    }

    setIsSubmitting(false);
  };

  /**
   * Resets the modal state and closes it.
   * Clears all form fields and any error messages before calling the onClose callback.
   */
  const handleClose = () => {
    setName("");
    setControlChip("");
    setMeasureChip("");
    setNote("");
    setError("");
    onClose();
  };

  return (
    <Show when={isOpen}>
      <div class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-[10px]">
        <div class="bg-white p-6 rounded-[20px] shadow-lg w-96">
          <ModalHeader title={"Ajouter un Capteur"} onClose={handleClose} onSubmit={handleSubmit} isSubmitting={isSubmitting()} />
          <Show when={errorMsg()}>
            <ErrorMessage message={errorMsg()} />
          </Show>
          <div class="space-y-4">
            <ModalField label="Nom*" value={name()} maxLength={32} onInput={(e) => setName(e.target.value)} placeholder="Capteur 42" />
            <ModalField label="Puce Témoin*" value={controlChip()} maxLength={32}  onInput={(e) => setControlChip(e.target.value)} placeholder="E280 6F12 0000 002 208F FACE" />
            <ModalField label="Puce Mesure*" value={measureChip()} maxLength={32}  onInput={(e) => setMeasureChip(e.target.value)} placeholder="E280 6F12 0000 002 208F FACD" />
            <ModalComment note={note()} onInput={(e) => setNote(e.target.value)}/>
          </div>
        </div>
      </div>
    </Show>
  );
};

export default ModalAddSensor;