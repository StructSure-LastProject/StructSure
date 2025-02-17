import { createSignal, Show } from "solid-js";
import useFetch from "../../hooks/useFetch.js";
import ModalHeader from "./ModalHeader.jsx";
import ErrorMessage from "./ErrorMessage.jsx";
import ModalField from "./ModalField.jsx";
import ModalImage from "./ModalImage.jsx";

/**
 * Modal for editing a plan.
 * Displays a form to edit name, section and optionally update the image.
 * @param {Object} props Component properties
 * @param {boolean} props.isOpen Whether the modal is open
 * @param {Function} props.onClose Callback to close the modal
 * @param {Function} props.onSave Callback after successful save
 * @param {number} props.structureId ID of the structure
 * @param {Object} props.plan Current plan data
 * @returns {JSX.Element} The modal component for editing a plan
 */
const Modal = ({ isOpen, onClose, onSave, structureId, plan }) => {
  const [name, setName] = createSignal(plan.name || "");
  const [section, setSection] = createSignal(plan.section || "");
  const [imageData, setImageData] = createSignal(null);
  const [imageFile, setImageFile] = createSignal(null);
  const [errorMsg, setError] = createSignal("");
  const [isSubmitting, setIsSubmitting] = createSignal(false);

  /**
   * Handles image file input change.
   * @param {Event} event - The file input change event
   */
  const handleImageChange = (event) => {
    const file = event.target.files[0];
    if (file) {
      const validTypes = ['image/jpeg', 'image/png'];
      if (file.size > 2097152) {
        setError("L'image ne doit pas dépasser 20Mo");
        return;
      }
      if (!validTypes.includes(file.type)) {
        setError("Le format du fichier doit être JPEG ou PNG");
        return;
      }
      setImageFile(file);
      const reader = new FileReader();
      reader.onload = () => {
        setImageData(reader.result);
        setError("");
      };
      reader.readAsDataURL(file);
    }
  };

  /**
   * Handles the form submission for editing a plan.
   * Validates the form data and sends it to the server.
   * @returns {Promise<void>}
   */
  const handleSubmit = async () => {
    setError("");
    if (!name().trim()) {
      setError("Le nom est requis");
      return;
    }
    const RegEx = /^(?:[a-zA-Z0-9_-]+(?:\/[a-zA-Z0-9_-]+)*)?$/;
    if (section().trim() && !RegEx.test(section().trim())) {
      setError("Le format de la section est invalide");
      return;
    }

    setIsSubmitting(true);

    const formData = new FormData();
    const metadata = {
      name: name().trim(),
      section: section().trim()
    };

    formData.append("metadata", new Blob([JSON.stringify(metadata)], {
      type: "application/json"
    }));

    if (imageFile()) {
      formData.append("file", imageFile());
    }

    const { fetchData, data, statusCode, error } = useFetch();

    await fetchData(`/api/structures/${structureId}/plans/${plan.id}`, {
      method: "PUT",
      body: formData
    });

    if (statusCode() === 200) {
      onSave(data());
      handleClose();
    } else {
      setError(error()?.errorData?.error || "Une erreur est survenue");
    }

    setIsSubmitting(false);
  };

  /**
   * Resets the modal state and closes it
   */
  const handleClose = () => {
    setName(plan.name || "");
    setSection(plan.section || "");
    setImageData(null);
    setImageFile(null);
    setError("");
    onClose();
  };

  return (
    <Show when={isOpen}>
      <div class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-[10px]">
        <div class="bg-white p-6 rounded-[20px] shadow-lg w-96">
          <ModalHeader
            title="Modifier le Plan"
            onClose={handleClose}
            onSubmit={handleSubmit}
            isSubmitting={isSubmitting()}
          />
          <Show when={errorMsg()}>
            <ErrorMessage message={errorMsg()} />
          </Show>
          <div class="space-y-4">
            <ModalField
              label="Nom*"
              value={name()}
              maxLength={32}
              onInput={(e) => setName(e.target.value)}
              placeholder="Zone 03"
            />
            <ModalField
              label="Section"
              value={section()}
              maxLength={128}
              onInput={(e) => setSection(e.target.value)}
              placeholder="OA/Aval"
            />
            <ModalImage
              imageSignal={[imageData, setImageData]}
              onImageChange={handleImageChange}
              currentImageUrl={plan.imageUrl}
            />
          </div>
        </div>
      </div>
    </Show>
  );
};

export default Modal;