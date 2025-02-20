import { Pencil, X } from 'lucide-solid';
import Header from '../Header';
import getSensorStatusColor from "../SensorStatusColorGen";
import SensorFieldComponent from './SensorFieldComponent';

/**
 * Shows the sensor panel with extra details of the clicked sensor
 * @param {Object} sensorDetails contains all the information about the clickded sensor 
 * @param {Function} closeSensorPanel Function that close the sensor panel
 * @returns The sensor panel component
 */
const SensorPanel = ({sensorDetails, closeSensorPanel}) => {
  return (
    <div class="min-h-[100vh] bg-[#F2F2F403] backdrop-blur-[25px] z-[100] flex items-end justify-center align-middle w-[100vw] fixed top-0 left-0">
      <div class="w-full p-[25px] fixed top-0 left-0 z-[150]">
        <Header />
      </div>
      <div class="max-w-[963px] max-h-[calc(100vh-7rem)] justify-center flex flex-col gap-[25px] w-[100%] rounded-t-[35px] p-[25px] lg:p-[50px] bg-[#FFFFFF] shadow-[0px_4px_100px_0px_rgba(151,151,167,0.50)] mx-auto">
        <div class="flex justify-between rounded-[20px]">
          <div class="flex flex-wrap justify-center items-center">
            <div class="p-[12px] gap-[10px] w-[39px] h-[39px]">
              <div class={`w-[15px] h-[15px] rounded-[50px] border-2 ${getSensorStatusColor(sensorDetails)}`}></div>
            </div>
            <h1 class="font-poppins font-[600] text-[25px] leading-[37.5px] tracking-[0%] text-[#181818]">{sensorDetails.name}</h1>
          </div>
          <div class="flex flex-wrap gap-[10px]">
            <button class="flex justify-center items-center w-[40px] h-[40px] sm:w-[40px] sm:h-[40px] rounded-[50px] bg-[#F2F2F4]">
                <Pencil color="#181818" size={20} width={16.67} top={1.67} left={1.67} strokeWidth={2} />
            </button>
            <button onClick={closeSensorPanel} class="flex justify-center items-center w-[40px] h-[40px] sm:w-[40px] sm:h-[40px] rounded-[50px] bg-[#F2F2F4]">
                <X color="#181818" size={20} width={16.67} top={1.67} left={1.67} strokeWidth={2} />
            </button>
          </div>
        </div>
        <div class="overflow-auto flex flex-col gap-[25px] rounded-[18px]">
          <div class="lg:flex lg:flex-row lg:gap-[25px] flex flex-col gap-[25px]">
            <div class="lg:flex lg:flex-col lg:gap-[10px]">
              <h1 class="font-poppins font-[600] text-[16px] leading-[24px] tracking-[0%] text-[#181818]">OA/Zone</h1>
              <img src="" alt="" class="w-full h-[156px] lg:min-w-[549px] lg:min-h-[299px]" />
             
            </div>
            <div class="flex flex-col gap-[5px] lg:gap-[10px]">
              <p class="opacity-[75%] font-poppins HeadLineMedium text-[#181818]">Note</p>
                <textarea class="rounded-[18px] w-full px-[16px] py-[8px] flex gap-[10px] bg-[#F2F2F4] font-poppins font-[400] text-[14px] leading-[21px] text-[#181818]"
                  value={sensorDetails.note}  
                >
                </textarea>
            </div>
          </div>
          <div class="lg:flex lg:flex-row lg:gap-[25px] lg:min-w-[863px] lg:min-h-[63px] flex flex-col gap-[25px]">
            <SensorFieldComponent title={"Puce témoin"} value={sensorDetails.controlChip}/>
            <SensorFieldComponent title={"Puce mesure"} value={sensorDetails.measureChip}/>
            <SensorFieldComponent title={"Date d’installation"} value={sensorDetails.installationDate}/>
          </div>
        </div>
      </div>
    </div>
  )
}

export default SensorPanel