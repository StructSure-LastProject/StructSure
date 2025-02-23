import {Pencil} from 'lucide-solid';

/**
 * Component for handling image upload and preview functionality
 * @param {Array} props.imageSignal - A tuple containing [imageData, setImageData] signals
 * @param {Function} props.onImageChange - Callback function triggered when the image file is selected
 * @param {string} props.currentImageUrl - URL of the existing image (optional)
 * @returns {JSX.Element} A form section for image upload and preview
 */
const ModalImage = (props) => {
  const [imageData] = props.imageSignal;

  return (
    <div class="space-y-2">
      <span class="normal opacity-75">Image*</span>
      <div class="relative w-full h-48 border-2 border-[#F2F2F4] rounded-[10px]">
        <ImagePreview imageData={imageData} currentImageUrl={props.currentImageUrl}/>
        <UploadButton onImageChange={props.onImageChange} />
      </div>
    </div>
  );
};

/**
 * Displays the uploaded image or a placeholder message if no image is present
 * @param {Object} props - Component properties
 * @param {Function} props.imageData - Signal function returning the image data URL
 * @param {string} props.currentImageUrl - URL of the existing image
 * @returns {JSX.Element} Image preview or placeholder message
 */
const ImagePreview = ({ imageData, currentImageUrl }) => (
  <div class="h-full w-full flex justify-center items-center">
    {imageData() ? (
      <img
        src={imageData()}
        alt="Plan modifié"
        class="w-full h-full rounded-[10px] object-cover"
      />
    ) : currentImageUrl ? (
      <img
        src={currentImageUrl}
        alt="Plan actuel"
        class="w-full h-full rounded-[10px] object-cover"
      />
    ) : (
      <p class="text-center normal text-black opacity-50">Pas d&apos;image sélectionnée</p>
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
    class="absolute bottom-4 right-4 bg-lightgray px-4 py-2 rounded-[50px] flex items-center gap-[10px] cursor-pointer transition-colors accent"
    for="file-input"
  >
    <span>Remplacer</span>
    <Pencil class="w-5 h-5 stroke-[2.5px]" />
    <input
      type="file"
      id="file-input"
      accept="image/*"
      onChange={onImageChange}
      class="hidden"
    />
  </label>
);
export default ModalImage;