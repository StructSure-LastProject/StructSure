import { createSignal, onCleanup, onMount, Show } from "solid-js";
import useFetch from "../../hooks/useFetch";
import ErrorMessage from "../Modal/ErrorMessage.jsx";
import ModalField from "../Modal/ModalField.jsx";
import ModalImage from "../Modal/ModalImage.jsx";
import { useNavigate } from "@solidjs/router";
import ArchiveModalHeader from "./ArchiveModalHeader.jsx";
import ArchivePlanModal from "./ArchivePlanModal.jsx";

/**
 * Modal for editing a plan.
 * Displays a form to edit name, section and optionally update the image.
 * @param {Object} props Component properties
 * @param {Function} props.onClose Callback to close the modal
 * @param {Function} props.onSave Callback after successful save
 * @param {number} props.structureId ID of the structure
 * @param {Object} props.plan Current plan data
 * @param {Object} props.setPlan sets the selected plan
 * @param {number} props.selectedPlanId getter for the selected plan id
 * @returns {JSX.Element} The modal component for editing a plan
 */
const ModalEditPlan = ({onClose, onSave, structureId, plan, setPlan, selectedPlanId, onPlanArchive }) => {
  const [name, setName] = createSignal(plan.name || "");
  const [section, setSection] = createSignal(plan.section || "");
  const [imageData, setImageData] = createSignal(null);
  const [imageFile, setImageFile] = createSignal(null);
  const [errorMsg, setError] = createSignal("");
  const [isSubmitting, setIsSubmitting] = createSignal(false);
  const navigate = useNavigate();
  const [tempImageBlob, setTempImageBlob] = createSignal(null);

  // Archive modal state
  const [isArchiveModalOpen, setIsArchiveModalOpen] = createSignal(false);
  const [errorMsgArchivePlan, setErrorMsgArchivePlan] = createSignal("");

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

    await fetchData(navigate, `/api/structures/${structureId}/plans/${plan.id}`, {
      method: "PUT",
      headers: {
        "Authorization": `Bearer ${localStorage.getItem("token")}`
      },
      body: formData
    });

    if (statusCode() === 200) {
      onSave({
        id: plan.id,
        metadata: {
          name: name(),
          section: section()
        }
      })
      if (selectedPlanId() === plan.id) {
        const objectURL = URL.createObjectURL(imageFile());
        setPlan(objectURL);
        if (tempImageBlob()) {
          URL.revokeObjectURL(tempImageBlob());
        }
        setTempImageBlob(objectURL);
      }
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

  /**
   * Opens the archive confirmation modal
   */
  const handleArchive = () => {
    setIsArchiveModalOpen(true);
  }

  /**
   * Handles the successful archive of a plan and closes the modal
   */
  const handleArchiveSuccess = (planId) => {
    setIsArchiveModalOpen(false);
    onPlanArchive(planId);
  }

  let modalRef;

  /**
   * Handles the close of the modal when click outside
   * @param {Event} event
   */
  const handleClickOutside = (event) => {
    if (!isArchiveModalOpen() && modalRef && !modalRef.contains(event.target)) {
      onClose();
    }
  };

  onMount(() => {
    document.addEventListener("mousedown", handleClickOutside);
    if (!isArchiveModalOpen()) {
      document.body.style.overflow = 'hidden';
    }
  });

  onCleanup(() => {
    document.removeEventListener("mousedown", handleClickOutside);
    if (!isArchiveModalOpen()) {
      document.body.style.overflow = 'auto';
    }
  });

  /**
   * Ferme la modale d'archivage
   */
  const handleArchiveClose = () => {
    setIsArchiveModalOpen(false);
    setErrorMsgArchivePlan("");
    document.body.style.overflow = 'hidden';
  }

  return (
    <>
      <Show when={!isArchiveModalOpen()}>
        <div class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-[10px]">
          <div ref={modalRef} class="bg-white p-6 rounded-[20px] shadow-lg w-96">
            <ArchiveModalHeader
              title="Edition du Plan"
              onArchive={handleArchive}
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
      <Show when={isArchiveModalOpen()}>
        <ArchivePlanModal
          plan={plan}
          structureId={structureId}
          onCloseArchive={handleArchiveClose}
          onArchive={handleArchiveSuccess}
          errorMsgArchivePlan={errorMsgArchivePlan}
          setErrorMsgArchivePlan={setErrorMsgArchivePlan}
        />
      </Show>
    </>
  );
};

export default ModalEditPlan;