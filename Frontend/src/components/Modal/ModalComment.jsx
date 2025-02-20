/**
 * The sensor comment section
 * @param {String} note The comment
 * @returns The component contains the comment of the sensor
 */
const ModalComment = ({note}) => {
  return (
    <div class="flex flex-col gap-[5px] lg:gap-[10px]">
      <p class="opacity-[75%] font-poppins HeadLineMedium text-[#181818]">Note</p>
      <textarea class="rounded-[18px] w-full px-[16px] py-[8px] flex gap-[10px] bg-[#F2F2F4] font-poppins font-[400] text-[14px] leading-[21px] text-[#181818]"
                value={note}
                maxlength={1000}
      >
      </textarea>
    </div>
  );
}
export default ModalComment;