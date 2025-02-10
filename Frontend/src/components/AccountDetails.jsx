
const AccountDetails = ({firstName, lastName, mail, role, isDisabled}) => {

    const bgColorText = role === "Opérateur" 
        ? "text-green-600 w-[74px] h-[21px] flex items-center justify-center font-poppins font-semibold" 
        : role === "Admin" 
        ? "text-red-600 w-[48px] h-[21px] flex items-center justify-center font-poppins font-semibold" 
        : role === "Responsable" 
        ? "text-orange-400 w-[93px] h-[21px] flex items-center justify-center font-poppins font-semibold" 
        : "";

    const bgColor = role === "Opérateur" 
        ? "bg-green-200 py-[2px] px-[10px] w-[94px] h-[25px] rounded-[20px]" 
        : role === "Admin" 
        ? "bg-red-200 py-[2px] px-[10px] w-[68px] h-[25px] rounded-[20px]" 
        : role === "Responsable" 
        ? "bg-orange-100 py-[2px] px-[10px] w-[113px] h-[25px] rounded-[20px]" 
        : "";




    return (
        <div class={`${isDisabled ? "opacity-[50%]" : ""} flex justify-between py-[10px] px-[25px] gap-x-[15px] bg-white rounded-[20px] w-[378px] h-[60px]`}>
            <div class="flex flex-col justify-center w-[219px] h-[40px]">
                <h2 class="text-lg font-poppins font-semibold">{firstName} {lastName}</h2>
                <span class="text-gray-500 font-poppins font-normal">{mail}</span>
            </div>
            <div class="flex items-center">
            <div class={`${bgColor}`}>
                <p class={`${bgColorText} `}>
                    {role}
                </p>
            </div>
            </div>
        </div>
    )
}

export default AccountDetails