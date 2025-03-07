import {Check, Trash2} from 'lucide-solid';

/**
 * Header for the modal with cancel and save buttons
 * @param {onClose, onSubmit, isSubmitting} Props for handling modal actions
 */
const ArchiveModalHeader = ({ onArchive, onSubmit, isSubmitting, title }) => (
  <div class="flex justify-between items-center mb-4">
    <h2 class="title">{title}</h2>
    <div class="flex items-center space-x-2">
      <button
        class="bg-[#F133271A] rounded-[50px] h-[40px] w-[40px] flex items-center justify-center"
        onClick={onArchive}
      >
        <Trash2 color="red" size={20}/>
      </button>
      <button
        type="button"
        onClick={onSubmit}
        disabled={isSubmitting}
        class="bg-lightgray rounded-[50px] h-[40px] w-[40px] flex items-center justify-center"
      >
        <Check class="w-5 h-5"/>
      </button>
    </div>
  </div>
);
export default ArchiveModalHeader;