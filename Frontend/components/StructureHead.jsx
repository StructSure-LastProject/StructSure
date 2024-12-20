import add from '/src/assets/add.svg';
import filter from '/src/assets/filter.svg';
import order from '/src/assets/order.svg';
import log_in from '/src/assets/log_in.svg';
import { createSignal } from "solid-js";
import { useNavigate } from '@solidjs/router';


function StructSureHead() {

    return (
        <div class="flex justify-between w-full">
            <h1 class="text-2xl font-medium	">Ouvrages</h1>
            <div class="flex space-x-4">
                <img src={order} class="" alt="Order Elements logo" />
                <img src={filter} class="" alt="Filter logo" />
                <img src={add} class="" alt="Add Button logo" />
            </div>
        </div>
    );
}

export default StructSureHead

