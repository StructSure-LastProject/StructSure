/**
 * A form field component for text input
 * @param {label, value, maxLength, onInput, onChange, placeholder} Props for field label and placeholder text
 */
const ModalField = ({ label, value, maxLength, onInput = () => {}, onChange = () => {}, id = "", placeholder }) => (
  <div>
    <label class="flex flex-col gap-[5px]">
      <p class="normal opacity-75">{label}</p>
      <input
        id={id}
        type="text"
        maxlength={maxLength}
        value={value}
        pattern="[a-zA-Z0-9]+"
        onInput={onInput}
        onChange={onChange}
        placeholder={placeholder}
        class="w-full px-[16px] py-[8px] bg-lightgray normal text-black rounded-[10px]"
      />
    </label>
  </div>
);
export default ModalField;