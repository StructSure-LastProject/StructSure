import { createSignal } from "solid-js";
import { Check, Pencil, X  } from 'lucide-solid'

/**
 * Modal for adding a plan.
 * Displaus a form to enter a name, a section and an image.
 * @param {isOpen, onClose, onSave} param   Props passed to the component
 * @returns The modal component for adding a plan
 */
const Modal = ({ isOpen, onClose, onSave }) => {
  const [selectedSection, setSelectedSection] = createSignal("");
  const [image, setImage] = createSignal(null);

  /**
   * Handles image file input change.
   * @param {*} event - The file input change event
   */
  const handleImageChange = (event) => {
    const file = event.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = () => {
        setImage(reader.result);
      };
      reader.readAsDataURL(file);
    }
  };

  return (
    <>
      {isOpen && (
        <div class="fixed inset-0 z-50 flex items-center justify-center bg-gray-800 bg-opacity-50">
          <form class="bg-white p-6 rounded-[20px] shadow-lg w-96">
            <div class="flex justify-between items-center mb-4">
              <h2 class="text-lg font-semibold">Ajouter un Plan</h2>
              <div class="flex items-center space-x-2">
              <button title="Annuler" onClick={onClose} class="bg-[#F2F2F4] rounded-[50px] h-[40px] w-[40px] flex items-center justify-center">
                    <X color="black"/>
                </button>
                <button title="Sauvegarder" onClick={onSave} class="bg-black rounded-[50px] h-[40px] w-[40px] flex items-center justify-center">
                    <Check color="white"/>
                </button>
              </div>
            </div>
            <div class="space-y-4">
              <div>
                <label class="block text-sm font-medium">Nom*
                    <input
                    type="text"
                    class="w-full px-3 py-2 border rounded-[10px]"
                    placeholder="Zone 03"
                    />
                </label>
              </div>

              <div>
                <label class="block text-sm font-medium">Section*
                    <select
                      class="bg-[#F2F2F4] w-full px-3 py-2 border rounded-[10px]"
                      value={selectedSection()}
                      onInput={(e) => setSelectedSection(e.target.value)}
                    >
                        <option value="">Sélectionner une section</option>
                        <option value="OA">OA</option>
                        <option value="Aval">Aval</option>
                        <option value="Zone 04">Zone 04</option>
                        <option value="Zone 05">Zone 05</option>
                    </select>
                </label>
              </div>

              <div>
                <div class="block text-sm font-medium">Image*
                    <div class="flex items-center justify-between">
                    <div class="relative w-96 h-48 border-2 border-[#F2F2F4] rounded-[10px] flex justify-center items-center">
                        {image() ? (
                            <img src={image()} alt="Plan ajouté" class="w-full h-full object-cover" />
                        ) : (
                            <p>Pas encore d&apos;image ...</p>
                        )}
                        <label
                            class="absolute bottom-4 right-4 bg-[#F2F2F4] text-black px-4 py-2 rounded-[50px] flex items-center space-x-2 cursor-pointer"
                            for="file-input"
                        >
                            <span>Remplacer</span>
                            <Pencil size={20}/>
                            <input
                                type="file"
                                id="file-input"
                                accept="image/*"
                                onChange={handleImageChange}
                                class="hidden"
                            />
                        </label>
                        
                        </div>

                    </div>
                </div>
              </div>
            </div>
          </form>
        </div>
      )}
    </>
  );
};

export default Modal;
