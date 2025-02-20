import { Pencil, X } from 'lucide-solid';
import Header from '../Header';
import getSensorStatusColor from "../SensorStatusColorGen";

/**
 * Shows the sensor panel with extra details of the clicked sensor
 * @param {Object} sensorDetails contains all the information about the clickded sensor 
 * @param {Function} closeSensorPanel Function that close the sensor panel
 * @returns The sensor panel component
 */
const SensorPanel = ({sensorDetails, closeSensorPanel}) => {
  return (
    <div class="min-h-[100vh] items-center bg-[#F2F2F403] backdrop-blur-[25px] z-[100] flex justify-center align-middle w-[100vw] fixed top-0 left-0">
      <div class="w-full p-[25px] fixed top-0 left-0 z-[150]">
        <Header />
      </div>
      <div class="max-w-[963px] min-h-[586px] lg:h-auto lg:flex justify-center absolute top-[30%] md:top-[40%] left-0 right-0 bottom-0 flex flex-col w-[100%] rounded-t-[35px] p-[25px] md:p-[25px] lg:p-[50px] gap-[25px] bg-[#FFFFFF] shadow-[0px 4px 100px 0px #9797A780] mx-auto">
        <div class="flex justify-between rounded-[20px] gap-[10px]">
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
        <div class="overflow-auto flex flex-col gap-[25px] pb-[35%] sm:pb-[0%]">
          <div class="lg:flex lg:flex-row lg:gap-[25px] flex flex-col gap-[25px]">
            <div class="lg:flex lg:flex-col lg:gap-[10px]">
              <h1 class="font-poppins font-[600] text-[16px] leading-[24px] tracking-[0%] text-[#181818]">OA/Zone</h1>
              <img src="" alt="" class="w-full h-[156px] lg:min-w-[549px] lg:min-h-[299px]" />
            </div>
            <div class="flex flex-col gap-[5px] lg:gap-[10px]">
              <p class="opacity-[75%] font-poppins HeadLineMedium text-[#181818]">Note</p>
              <div class="rounded-[18px] px-[16px] py-[8px] flex gap-[10px] bg-[#F2F2F4]">
                <p class="font-poppins font-[400] text-[14px] leading-[21px] text-[#181818]">{sensorDetails.note}</p>
              </div>
            </div>
          </div>
          <div class="lg:flex lg:flex-row lg:gap-[25px] lg:min-w-[863px] lg:min-h-[63px] flex flex-col gap-[25px]">
            <div class="flex flex-col gap-[5px]">
              <p class="opacity-[75%] font-poppins HeadLineMedium text-[#181818]">Puce témoin</p>
              <input class="rounded-[50px] px-[16px] py-[8px] flex gap-[10px] lg:max-w-[271px] font-poppins font-[600] text-[14px] leading-[21px] bg-[#F2F2F4] text-[#181818]" 
                type="text"
                value={sensorDetails.controlChip}
                disabled
              />
            </div>
            <div class="flex flex-col gap-[5px] ">
              <p class="opacity-[75%] font-poppins HeadLineMedium text-[#181818]">Puce mesure</p>
              <input class="rounded-[50px] px-[16px] py-[8px] flex gap-[10px] lg:max-w-[271px] font-poppins font-[600] text-[14px] leading-[21px] bg-[#F2F2F4] text-[#181818]"
                type="text"
                value={sensorDetails.measureChip}
                disabled
              />
            </div>
            <div class="flex flex-col gap-[5px] ">
              <p class="opacity-[75%] font-poppins HeadLineMedium text-[#181818]">Date d’installation</p>
              <input class="rounded-[50px] px-[16px] py-[8px] flex gap-[10px] lg:max-w-[271px] font-poppins font-[600] text-[14px] leading-[21px] bg-[#F2F2F4] text-[#181818]"
                type="text"
                value={sensorDetails.installationDate}
                disabled
              />
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default SensorPanel