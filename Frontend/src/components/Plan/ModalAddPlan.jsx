import { createSignal } from "solid-js";
import { Check, Pencil, X } from 'lucide-solid';

/**
 * Modal for adding a plan.
 * Displaus a form to enter a name, a section and an image.
 * @param {isOpen, onClose, onSave} param   Props passed to the component
 * @returns The modal component for adding a plan
 */
const Modal = ({ isOpen, onClose, onSave }) => {
  const [selectedSection, setSelectedSection] = createSignal("");
  const [imageData, setImageData] = createSignal(null);

  /**
   * Handles image file input change.
   * @param {*} event - The file input change event
   */
  const handleImageChange = (event) => {
    const file = event.target.files[0];
    const reader = new FileReader();
    reader.onload = () => {
      setImageData(reader.result)
    };
    reader.readAsDataURL(file);
  };

  return (
    <>
      {isOpen && (
        <div class="fixed inset-0 z-50 flex items-center justify-center bg-gray-800 bg-opacity-50">
          <form class="bg-white p-6 rounded-[20px] shadow-lg w-96">
            <ModalHeader onClose={onClose} onSave={onSave} />
            <div class="space-y-4">
              <ModalField label="Nom*" placeholder="Zone 03" />
              <ModalSelect label="Section*" value={selectedSection()} onInput={(e) => setSelectedSection(e.target.value)} />
              <ModalImage imageSignal={[imageData, setImageData]} onImageChange={handleImageChange} />
            </div>
          </form>
        </div>
      )}
    </>
  );
};

/**
 * Header for the modal with cancel and save buttons
 * @param {onClose, onSave} Props for handling modal actions
 */
const ModalHeader = ({ onClose, onSave }) => (
  <div class="flex justify-between items-center mb-4">
    <h2 class="text-lg font-semibold">Ajouter un Plan</h2>
    <div class="flex items-center space-x-2">
      <button title="Annuler" onClick={onClose} class="bg-[#F2F2F4] rounded-[50px] h-[40px] w-[40px] flex items-center justify-center">
        <X color="black" />
      </button>
      <button title="Sauvegarder" onClick={onSave} class="bg-black rounded-[50px] h-[40px] w-[40px] flex items-center justify-center">
        <Check color="white" />
      </button>
    </div>
  </div>
);

/**
 * A form field component for text input
 * @param {label, placeholder} Props for field label and placeholder text
 */
const ModalField = ({ label, placeholder }) => (
  <div>
    <label class="block text-sm font-medium">{label}
      <input
        type="text"
        class="w-full px-3 py-2 border rounded-[10px]"
        placeholder={placeholder}
      />
    </label>
  </div>
);

/**
 * A select component for choosing a section
 * @param {label, value, onInput} Props for section selection
 */
const ModalSelect = ({ label, value, onInput }) => (
  <div>
    <label class="block text-sm font-medium">{label}
      <select
        class="bg-[#F2F2F4] w-full px-3 py-2 border rounded-[10px]"
        value={value}
        onInput={onInput}
      >
        <option value="">Sélectionner une section</option>
        <option value="OA">OA</option>
        <option value="Aval">Aval</option>
        <option value="Zone 04">Zone 04</option>
        <option value="Zone 05">Zone 05</option>
      </select>
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
    <div>
      <span class="block text-sm font-medium">Image*</span>
      <div class="flex items-center justify-between">
        <div class="relative w-full h-48 border-2 border-[#F2F2F4] rounded-[10px] flex justify-center items-center">
          {imageData() ? (
            <img src={imageData()} alt="Plan ajouté" class="w-full h-full object-cover"/>
          ) : (
            <p class="text-center">Pas encore d&apos;image ...</p>
          )}
          <label class="absolute bottom-4 right-4 bg-[#F2F2F4] text-black px-4 py-2 rounded-[50px] flex items-center space-x-2 cursor-pointer" htmlFor="file-input">
            <span>Remplacer</span>
            <Pencil size={20} />
            <input type="file" id="file-input" accept="image/*" onChange={props.onImageChange} class="hidden"/>
          </label>
        </div>
      </div>
    </div>
  );
};

export default Modal;
