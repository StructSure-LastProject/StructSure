/**
 * 
 * @param {String} title The title of the section
 * @param {String} value The note of sensor 
 * @returns The component of sensor field
 */
function SensorFieldComponent({title, value}) {
  return (
    <div class="flex flex-col gap-[5px] w-full">
        <p class="normal opacity-75">{title}</p>
        <input class="rounded-[50px] px-[16px] py-[8px] w-full bg-light-gray accent" 
        type="text"
        value={value}
        disabled
        />
    </div>
  )
}

export default SensorFieldComponent