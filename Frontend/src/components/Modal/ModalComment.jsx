/**
 * The modal comment section
 * @param {String} note The comment
 * @returns The component contains the comment for modals
 */
const ModalComment = ({note, onInput}) => {
  return (
    <div class="flex flex-col gap-[5px]">
      <p class="normal opacity-75">Note</p>
      <textarea class="rounded-[10px] w-full px-[16px] py-[8px] flex gap-[10px] bg-lightgray normal resize-none [&::-webkit-scrollbar]:hidden [-ms-overflow-style:none] [scrollbar-width:none]"
                value={note}
                maxlength={1000}
                onInput={onInput}
      >
      </textarea>
    </div>
  );
}
export default ModalComment;