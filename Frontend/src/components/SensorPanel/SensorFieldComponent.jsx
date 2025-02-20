/**
 * 
 * @param {String} title The title of the section
 * @param {String} value The note of sensor 
 * @returns The component of sensor field
 */
function SensorFieldComponent({title, value}) {
  return (
    <div class="flex flex-col gap-[5px]">
        <p class="opacity-[75%] font-poppins HeadLineMedium text-[#181818]">{title}</p>
        <input class="rounded-[50px] px-[16px] py-[8px] flex gap-[10px] lg:max-w-[271px] font-poppins font-[600] text-[14px] leading-[21px] bg-[#F2F2F4] text-[#181818]" 
        type="text"
        value={value}
        disabled
        />
    </div>
  )
}

export default SensorFieldComponent