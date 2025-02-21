/**
 * Sensor field component
 * @param {String} title The title of the section
 * @param {String} value The note of sensor
 * @param {Boolean} editMode The mode
 * @param {String} type The type of the input
 * @param {String} minLength The minimum length of the input
 * @param {String} maxLength The maximum length of the input
 * @param {Boolean} isRequired The input is required or not
 * @param {Funtion} setter The setter to set the new value
 * @returns The component of sensor field
 */
const SensorFieldComponent = ({
  title, 
  value, 
  editMode, 
  type, 
  minLength, 
  maxLength, 
  isRequired, 
  setter
}) => {
  
if(setter === undefined && type === undefined && maxLength === undefined && minLength === undefined){
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
return (
    <div class="flex flex-col gap-[5px]">
        <p class="opacity-[75%] font-poppins HeadLineMedium text-[#181818]">{title}</p>
        <input class="rounded-[50px] px-[16px] py-[8px] flex gap-[10px] lg:max-w-[271px] font-poppins font-[600] text-[14px] leading-[21px] bg-[#F2F2F4] text-[#181818]" 
          type={type}
          onChange={(e) => setter(e.target.value)}
          value={value()}
          minLength={minLength}
          maxLength={maxLength}
          required={isRequired}
          disabled={!editMode()}
        />
    </div>
  );
}

export default SensorFieldComponent;
