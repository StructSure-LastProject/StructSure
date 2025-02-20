/**
 * A form field component for text input
 * @param {label, value, maxLength, onInput, placeholder} Props for field label and placeholder text
 */
const ModalField = ({ label, value, maxLength, onInput, placeholder }) => (
  <div>
    <label class="block text-sm font-medium">
      {label}
      <input
        type="text"
        maxlength={maxLength}
        value={value}
        pattern="[a-zA-Z0-9]+"
        onInput={onInput}
        placeholder={placeholder}
        class="mt-1 w-full px-3 py-2 border rounded-[10px]"
      />
    </label>
  </div>
);
export default ModalField;