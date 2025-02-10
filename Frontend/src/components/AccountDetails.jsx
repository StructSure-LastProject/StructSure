
const AccountDetails = ({firstName, lastName, mail, role, isDisabled}) => {

    const roleStyles = {
        "Opérateur": {
            text: "text-green-600 w-[74px] h-auto flex items-center justify-center font-poppins user-role",
            bg: "bg-green-200 py-[2px] px-[10px] w-[94px] h-auto rounded-[20px]"
        },
        "Admin": {
            text: "text-red-600 w-[48px] h-auto flex items-center justify-center font-poppins user-role",
            bg: "bg-red-200 py-[2px] px-[10px] w-[68px] h-auto rounded-[20px]"
        },
        "Responsable": {
            text: "text-orange-400 w-[93px] h-auto flex items-center justify-center font-poppins user-role",
            bg: "bg-orange-100 py-[2px] px-[10px] w-[113px] h-auto rounded-[20px]"
        }
    };
    
    const bgColorText = roleStyles[role]?.text || "";
    const bgColor = roleStyles[role]?.bg || "";
    
    return (
        <div class={`${isDisabled ? "opacity-[50%]" : ""} flex justify-between py-[10px] px-[25px] gap-x-[15px] bg-white rounded-[20px] w-[378px] h-auto`}>
            <div class="flex flex-col justify-center w-[219px] h-auto">
                <h2 class="text-lg font-poppins title-medium">{firstName} {lastName}</h2>
                <span class="font-poppins HeadLineMedium text-gray-500">{mail}</span>
            </div>
            <div class="flex items-center">
                <div class={`${bgColor}`}>
                    <p class={`${bgColorText}`}>
                        {role}
                    </p>
                </div>
            </div>
        </div>
    );
}

export default AccountDetails