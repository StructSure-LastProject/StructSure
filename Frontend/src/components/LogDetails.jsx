
/**
 * Display the log details 
 * @param {String} date the date
 * @param {String} time the time
 * @param {String} logMessage the log message 
 * @returns 
 */
const LogDetails = ({date, time, logMessage}) => {
  return (
    <div class="flex flex-wrap sm:flex-nowrap items-center w-full max-w-[1250px] h-auto mx-auto">
        <div class="w-[169px] h-auto px-[15px] gap-[10px] opacity-[50%]">
            <p class="normal opacity-50">{date} - {time}</p>
        </div>
        <div class="flex w-[1081px] min-w-min-[378px] rounded-[10px] py-[5px] px-[15px] bg-white">
            <p class="items-center normal">{logMessage}</p>
        </div>

    </div>
  )
}

export default LogDetails