import {createSignal, onCleanup, onMount, Show} from "solid-js";
import useFetch from "../../hooks/useFetch";
import ModalHeader from "../Modal/ModalHeader.jsx";
import ErrorMessage from "../Modal/ErrorMessage.jsx";
import ModalField from "../Modal/ModalField.jsx";
import ModalImage from "../Modal/ModalImage.jsx";
import { useNavigate } from "@solidjs/router";

/**
 * Modal for adding a plan.
 * Displaus a form to enter a name, a section and an image.
 * @param {isOpen, onClose, onSave, structureId} param Props passed to the component
 * @returns The modal component for adding a plan
 */
const ModalAddPlan = ({ isOpen, onClose, onSave, structureId }) => {
  const [name, setName] = createSignal("");
  const [section, setSection] = createSignal("");
  const [imageData, setImageData] = createSignal(null);
  const [imageFile, setImageFile] = createSignal(null);
  const [errorMsg, setError] = createSignal("");
  const [isSubmitting, setIsSubmitting] = createSignal(false);
  const navigate = useNavigate();
  let modalRef;

  /**
   * Handles image file input change.
   * @param {*} event - The file input change event
   */
  const handleImageChange = (event) => {
    setError("");
    const file = event.target.files[0];
    if (file) {
      const validTypes = ['image/jpeg', 'image/png'];
      if (file.size > 20971520) {
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
   * Handles the form submission for adding a new plan.
   * Validates the form data, creates a FormData object with the plan metadata and file,
   * and sends it to the server.
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
    const RegEx = /^(?:[a-zA-Z0-9_-]+(?:\/[a-zA-Z0-9_-]+)*)?$/;
    if (section().trim() && !RegEx.test(section().trim())) {
      setError("Le format de la section est invalide");
      return;
    }
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

    await fetchData(navigate, `/api/structures/${structureId}/plans`, {
      method: "POST",
      headers: {
        "Authorization": `Bearer ${localStorage.getItem("token")}`
      },
      body: formData
    });

    if (statusCode() === 201) {
      const result = data();
      onSave({
        id: result.id,
        metadata: {
          name: name(),
          section: section().trim()
        }
      });
      handleClose();
    } else {
      setError(error()?.errorData?.error || "Une erreur est survenue");
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
  
  /**
   * Handles the close of the modal when click outside
   * @param {Event} event 
   */
  const handleClickOutside = (event) => {
      if (modalRef && !modalRef.contains(event.target)) {
          onClose();
      }
  };

  onMount(() => {
      document.addEventListener("mousedown", handleClickOutside);
  });

  onCleanup(() => {
      document.removeEventListener("mousedown", handleClickOutside);
  });

  return (
    <Show when={isOpen}>
      <div class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-[10px]">
        <div ref={modalRef} class="bg-white p-6 rounded-[20px] shadow-lg w-96">
          <ModalHeader title={"Ajouter un Plan"} onClose={handleClose} onSubmit={handleSubmit} isSubmitting={isSubmitting()} />
          <Show when={errorMsg()}>
            <ErrorMessage message={errorMsg()} />
          </Show>
          <div class="space-y-4">
            <ModalField label="Nom*" value={name()} maxLength={32} onInput={(e) => setName(e.target.value)} placeholder="Zone 03" />
            <ModalField label="Section" value={section()} maxLength={128}  onInput={(e) => setSection(e.target.value)} placeholder="OA/Aval" />
            <ModalImage imageSignal={[imageData, setImageData]} onImageChange={handleImageChange} />
          </div>
        </div>
      </div>
    </Show>
  );
};

export default ModalAddPlan;