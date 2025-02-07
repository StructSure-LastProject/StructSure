import logo from '/src/assets/logo.svg';
import check from '/src/assets/check.svg';
import { For, createResource, createSignal } from "solid-js";
import { useNavigate } from '@solidjs/router';
import { ArrowDownNarrowWide, Filter, Plus, Trash2 } from 'lucide-solid';


function StructureDetailCapteurs() {
    
    return (
        
        <div class="w-full flex flex-col gap-y-[15px]">
            <div class="flex justify-between">
                <p class="prose font-poppins title">Capteurs</p>
                <div class="flex justify-between gap-x-[10px]">
                    <div class="w-10 h-10 rounded-[50px] bg-white flex justify-center items-center">
                        <ArrowDownNarrowWide size={20}/>
                    </div>
                    <div class="w-10 h-10 rounded-[50px] bg-white flex justify-center items-center">
                        <Filter size={20}/>
                    </div>
                    <div class="w-10 h-10 rounded-[50px] bg-black flex justify-center items-center">
                        <Plus size={20} color='white'/>
                    </div>
                </div>
            </div>
            <div class="grid grid-cols-3 rounded-[20px] gap-4">
                <div class="flex justify-between gap-x-[15px] rounded-[50px] px-[25px] py-[10px] bg-white items-center">
                    <div class="w-[12px] h-[12px] rounded-[50px] bg-[#F13327] border-2 border-red-200"></div>
                    <p class="prose font-poppins poppins font-semibold w-[138px]">Capteur 01</p>
                    <div class="w-5 h-5 rounded-[50px] flex justify-center items-center">
                        <Trash2 size={20} />
                    </div>
                </div>
                <div class="flex justify-between gap-x-[15px] rounded-[50px] px-[25px] py-[10px] bg-white items-center">
                    <div class="w-[12px] h-[12px] rounded-[50px] bg-[#25B61F] border-2 border-green-200"></div>
                    <p class="prose font-poppins poppins font-semibold w-[138px]">Capteur 02</p>
                    <div class="w-5 h-5 rounded-[50px] flex justify-center items-center">
                        <Trash2 size={20} />
                    </div>
                </div>
                <div class="flex justify-between gap-x-[15px] rounded-[50px] px-[25px] py-[10px] bg-white items-center">
                    <div class="w-[12px] h-[12px] rounded-[50px] bg-[#6A6A6A] border-2 border-grey-200"></div>
                    <p class="prose font-poppins poppins font-semibold w-[138px]">Capteur 05</p>
                    <div class="w-5 h-5 rounded-[50px] flex justify-center items-center">
                        <Trash2 size={20} />
                    </div>
                </div>
                <div class="flex justify-between gap-x-[15px] rounded-[50px] px-[25px] py-[10px] bg-white items-center">
                    <div class="w-[12px] h-[12px] rounded-[50px] bg-[#F19327] border-2 border-yellow-200"></div>
                    <p class="prose font-poppins poppins font-semibold w-[138px]">Capteur 06</p>
                    <div class="w-5 h-5 rounded-[50px] flex justify-center items-center">
                        <Trash2 size={20} />
                    </div>
                </div>
                <div class="flex justify-between gap-x-[15px] rounded-[50px] px-[25px] py-[10px] bg-white items-center">
                    <div class="w-[12px] h-[12px] rounded-[50px] bg-[#25B61F] border-2 border-green-200"></div>
                    <p class="prose font-poppins poppins font-semibold w-[138px]">Capteur 10</p>
                    <div class="w-5 h-5 rounded-[50px] flex justify-center items-center">
                        <Trash2 size={20} />
                    </div>
                </div>
                <div class="flex justify-between gap-x-[15px] rounded-[50px] px-[25px] py-[10px] bg-white items-center">
                    <div class="w-[12px] h-[12px] rounded-[50px] bg-[#25B61F] border-2 border-green-200"></div>
                    <p class="prose font-poppins poppins font-semibold w-[138px]">Capteur 04</p>
                    <div class="w-5 h-5 rounded-[50px] flex justify-center items-center">
                        <Trash2 size={20} />
                    </div>
                </div>
            </div>
        </div>
    );
}

export default StructureDetailCapteurs

