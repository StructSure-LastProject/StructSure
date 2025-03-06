
/**
 * Display the log details 
 * @param {String} time the time
 * @param {String} author the login of the author
 * @param {String} logMessage the log message 
 * @returns 
 */
const LogDetails = ({time, author, logMessage}) => {
  return (
    <div class="flex flex-col justify-center items-center w-full gap-[5px]">
        <div class="flex px-[5px] justify-between items-center self-stretch opacity-50">
            <p class="accent">{author}</p>
            <p class="normal opacity-75">{time}</p>
        </div>
        <p class="flex flex-col items-start self-stretch px-[16px] py-[8px] bg-white rounded-[10px] rounded-tl-none normal">{logMessage}</p>
    </div>
  )
}

export default LogDetails