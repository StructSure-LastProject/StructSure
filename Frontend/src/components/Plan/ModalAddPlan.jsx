import { createSignal, Show } from "solid-js";
import { Check, Pencil, X } from 'lucide-solid';
import useFetch from "../../hooks/useFetch.js";

/**
 * Modal for adding a plan.
 * Displaus a form to enter a name, a section and an image.
 * @param {isOpen, onClose, onSave, structureId} param Props passed to the component
 * @returns The modal component for adding a plan
 */
const Modal = ({ isOpen, onClose, onSave, structureId }) => {
  const [name, setName] = createSignal("");
  const [section, setSection] = createSignal("");
  const [imageData, setImageData] = createSignal(null);
  const [imageFile, setImageFile] = createSignal(null);
  const [error, setError] = createSignal("");
  const [isSubmitting, setIsSubmitting] = createSignal(false);

  /**
   * Handles image file input change.
   * @param {*} event - The file input change event
   */
  const handleImageChange = (event) => {
    const file = event.target.files[0];
    if (file) {
      const validTypes = ['image/jpeg', 'image/png'];
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
   * Handles the form submission for adding a new plan.
   * Validates the form data, creates a FormData object with the plan metadata and file,
   * and sends it to the server.
   *
   * @returns {Promise<void>} A promise that resolves when the submission is complete
   * @throws Will display an error message if the submission fails
   */
  const handleSubmit = async () => {
    if (!name().trim()) {
      setError("Le nom est requis");
      return;
    }
    setError("");
    if (!imageFile()) {
      setError("Une image est requise");
      return;
    }

    setIsSubmitting(true);
    setError("");

    const formData = new FormData();
    const metadata = {
      name: name().trim(),
      section: section().trim()
    };

    formData.append("metadata", new Blob([JSON.stringify(metadata)], {
      type: "application/json"
    }));
    formData.append("file", imageFile());

    const { fetchData, data, statusCode, error } = useFetch();

    await fetchData(`/api/structures/${structureId}/plans`, {
      method: "POST",
      body: formData
    });

    if (statusCode() === 201) {
      onSave(data());
      handleClose();
    } else {
      setError(error() || "Une erreur est survenue");
    }

    setIsSubmitting(false);
  };

  /**
   * Resets the modal state and closes it.
   * Clears all form fields including name, section, image data,
   * and any error messages before calling the onClose callback.
   */
  const handleClose = () => {
    setName("");
    setSection("");
    setImageData(null);
    setImageFile(null);
    setError("");
    onClose();
  };

  return (
    <Show when={isOpen}>
      <div class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-[10px]">
        <div class="bg-white p-6 rounded-[20px] shadow-lg w-96">
          <ModalHeader onClose={handleClose} onSubmit={handleSubmit} isSubmitting={isSubmitting()} />
          <Show when={error()}>
            <ErrorMessage message={error()} />
          </Show>
          <div class="space-y-4">
            <ModalField label="Nom*" value={name()} onInput={(e) => setName(e.target.value)} placeholder="Zone 03" />
            <ModalField label="Section" value={section()} onInput={(e) => setSection(e.target.value)} placeholder="OA/Aval" />
            <ModalImage imageSignal={[imageData, setImageData]} onImageChange={handleImageChange} />
          </div>
        </div>
      </div>
    </Show>
  );
};

/**
 * Header for the modal with cancel and save buttons
 * @param {onClose, onSubmit, isSubmitting} Props for handling modal actions
 */
const ModalHeader = ({ onClose, onSubmit, isSubmitting }) => (
  <div class="flex justify-between items-center mb-4">
    <h2 class="text-lg font-semibold">Ajouter un Plan</h2>
    <div class="flex items-center space-x-2">
      <button
        type="button"
        onClick={onClose}
        disabled={isSubmitting}
        class="bg-[#F2F2F4] rounded-[50px] h-[40px] w-[40px] flex items-center justify-center"
      >
        <X class="w-5 h-5" />
      </button>
      <button
        type="button"
        onClick={onSubmit}
        disabled={isSubmitting}
        class="bg-black rounded-[50px] h-[40px] w-[40px] flex items-center justify-center"
      >
        <Check class="w-5 h-5 text-white" />
      </button>
    </div>
  </div>
);

/**
 * Displays a message (typically an error) in red text
 * @param {Object} props - Component properties
 * @param {string} props.message - The message to display
 */
const ErrorMessage = ({ message }) => (
  <div class="mb-4 text-[#F13327] text-sm font-medium">
    {message}
  </div>
);

/**
 * A form field component for text input
 * @param {label, value, onInput, placeholder} Props for field label and placeholder text
 */
const ModalField = ({ label, value, onInput, placeholder }) => (
  <div>
    <label class="block text-sm font-medium">
      {label}
      <input
        type="text"
        value={value}
        onInput={onInput}
        placeholder={placeholder}
        class="mt-1 w-full px-3 py-2 border rounded-[10px]"
      />
    </label>
  </div>
);

/**
 * Component for handling image upload and preview functionality
 * @param {Array} props.imageSignal - A tuple containing [imageData, setImageData] signals
 * @param {Function} props.onImageChange - Callback function triggered when the image file is selected
 * @returns {JSX.Element} A form section for image upload and preview
 */
const ModalImage = (props) => {
  const [imageData] = props.imageSignal;

  return (
      <div class="space-y-2">
        <span class="block text-sm font-medium">Image*</span>
        <div class="relative w-full h-48 border-2 border-[#F2F2F4] rounded-[10px]">
          <ImagePreview imageData={imageData} />
          <UploadButton onImageChange={props.onImageChange} />
        </div>
      </div>
  );
};

/**
 * Displays the uploaded image or a placeholder message if no image is present
 * @param {Object} props - Component properties
 * @param {Function} props.imageData - Signal function returning the image data URL
 * @returns {JSX.Element} Image preview or placeholder message
 */
const ImagePreview = ({ imageData }) => (
  <div class="h-full w-full flex justify-center items-center">
    {imageData() ? (
      <img
        src={imageData()}
        alt="Plan ajouté"
        class="w-full h-full rounded-[10px] object-fill"
      />
    ) : (
      <p class="text-center text-gray-500">Pas d'image sélectionnée</p>
    )}
  </div>
);

/**
 * A styled button component that handles file uploads
 * @param {Object} props - Component properties
 * @param {Function} props.onImageChange - Callback function triggered when a file is selected
 * @returns {JSX.Element} Upload button with hidden file input
 */
const UploadButton = ({ onImageChange }) => (
  <label
    class="absolute bottom-4 right-4 bg-[#F2F2F4] hover:bg-gray-200 px-4 py-2 rounded-[50px] flex items-center space-x-2 cursor-pointer transition-colors"
    htmlFor="file-input"
  >
    <span>Remplacer</span>
    <Pencil class="w-5 h-5" />
    <input
      type="file"
      id="file-input"
      accept="image/*"
      onChange={onImageChange}
      class="hidden"
    />
  </label>
);

export default Modal;