/**
 * A form field component for text input
 * @param {label, value, maxLength, onInput, placeholder} Props for field label and placeholder text
 */
const ModalField = ({ label, value, maxLength, onInput, placeholder }) => (
  <div>
    <label class="flex flex-col gap-[5px]">
      <p class="normal opacity-75">{label}</p>
      <input
        type="text"
        maxlength={maxLength}
        value={value}
        pattern="[a-zA-Z0-9]+"
        onInput={onInput}
        placeholder={placeholder}
        class="w-full px-[16px] py-[8px] bg-lightgray normal text-black rounded-[10px]"
      />
    </label>
  </div>
);
export default ModalField;