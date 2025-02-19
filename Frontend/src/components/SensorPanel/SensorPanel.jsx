import { Pencil, X } from 'lucide-solid';


const SensorPanel = () => {
  return (
    <div>
      <div class="min-h-[100vh] items-center bg-[#F2F2F403] backdrop-blur-[25px] z-[100] flex justify-center align-middle w-[100vw] h-[100vh] fixed top-0 left-0">
        <div class="max-w-[963px] max-h-[586px] sm:flex justify-center fixed top-[30%] left-0 right-0 bottom-0 flex flex-col w-[100%] rounded-t-[35px] p-[25px] sm:p-[50px] gap-[25px] bg-[#FFFFFF] shadow-[0px 4px 100px 0px #9797A780] sm:top-[30%] mx-auto">
          <div class="flex justify-between rounded-[20px] gap-[10px]">
            <div class="flex flex-wrap justify-center items-center">
              <div class="p-[12px] gap-[10px] w-[39px] h-[39px]">
                <div class="w-[15px] h-[15px] rounded-[50px] border-2 bg-[#F13327] border-red-200"></div>
              </div>
              <h1 class="font-poppins font-[600] text-[25px] leading-[37.5px] tracking-[0%] text-[#181818]">Capteur 01</h1>
            </div>
            <div class="flex flex-wrap gap-[10px]">
              <button class="flex justify-center items-center w-[40px] h-[40px] sm:w-[40px] sm:h-[40px] rounded-[50px] bg-[#F2F2F4]">
                  <Pencil color="#181818" size={20} width={16.67} top={1.67} left={1.67} strokeWidth={2} />
              </button>
              <button class="flex justify-center items-center w-[40px] h-[40px] sm:w-[40px] sm:h-[40px] rounded-[50px] bg-[#F2F2F4]">
                  <X color="#181818" size={20} width={16.67} top={1.67} left={1.67} strokeWidth={2} />
              </button>
            </div>
          </div>
          <div class="overflow-auto flex flex-col gap-[25px]">
            <div>
              <h1 class="font-poppins font-[600] text-[16px] leading-[24px] tracking-[0%] text-[#181818]">OA/Zone</h1>
              <img src="" alt="" class="w-[378px] h-[156px]" />
            </div>
            <div class="flex flex-col gap-[5px] ">
              <p class="opacity-[75%] font-poppins HeadLineMedium text-[#181818]">Note</p>
              <div class="rounded-[18px] px-[16px] py-[8px] flex gap-[10px] bg-[#F2F2F4]">
                <p class="font-poppins font-[400] text-[14px] leading-[21px] text-[#181818]">Capteur caché derrière la poutre métallique à environ 30cm du point d’ancrage.</p>
              </div>
            </div>
            <div class="flex flex-col gap-[5px] ">
              <p class="opacity-[75%] font-poppins HeadLineMedium text-[#181818]">Puce témoin</p>
              <div class="rounded-[50px] px-[16px] py-[8px] flex gap-[10px] bg-[#F2F2F4]">
                <input class="font-poppins font-[600] text-[14px] leading-[21px] bg-[#F2F2F4] text-[#181818]" 
                  type="text"
                  value={"EB000035A"}
                />
              </div>
            </div>
            <div class="flex flex-col gap-[5px] ">
              <p class="opacity-[75%] font-poppins HeadLineMedium text-[#181818]">Puce mesure</p>
              <div class="rounded-[50px] px-[16px] py-[8px] flex gap-[10px] bg-[#F2F2F4]">
                <input class="font-poppins font-[600] text-[14px] leading-[21px] bg-[#F2F2F4] text-[#181818]" 
                  type="text"
                  value={"EB000035B"}
                />
              </div>
            </div>
            <div class="flex flex-col gap-[5px] ">
              <p class="opacity-[75%] font-poppins HeadLineMedium text-[#181818]">Date d’installation</p>
              <div class="rounded-[50px] px-[16px] py-[8px] flex gap-[10px] bg-[#F2F2F4]">
                <input class="font-poppins font-[600] text-[14px] leading-[21px] bg-[#F2F2F4] text-[#181818]" 
                  type="text"
                  value={"16/02/2020"}
                />
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

  )
}

export default SensorPanel