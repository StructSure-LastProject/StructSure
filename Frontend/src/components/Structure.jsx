import structsureLogo from "../assets/StructSure-Title.svg";
import userRound from "../assets/user-round.svg";
import crown from "../assets/crown.svg";
import download from "../assets/download.svg";
import pencil from "../assets/pencil.svg";
import plus from "../assets/plus.svg";
import trash from "../assets/trash-2.svg";
import filter from "../assets/filter.svg";
import arrowDown from "../assets/arrow-down-narrow-wide.svg";
import { createEffect, createResource, createSignal, onMount, Show } from "solid-js";

const fetchStructure = async (id) => {
  try {
    const response = await fetch("http://localhost:8080/api/structures/1");
    if (!response.ok) {
      throw new Error("Network response was not ok");
    }
    return response.json();
  } catch (err) {
    console.log(err.message);
    // throw err;
  }
};

function Structure(idStructure) {
  const [structureId, setStructureId] = createSignal(null);
  const [structure] = createResource(structureId, fetchStructure);
  const [showEditStructure, setShowEditStructure] = createSignal(true);
  const [nameExisted, setNameExisted] = createSignal(false);

  const toggleEditStructure = () => {
    setShowEditStructure(!showEditStructure());
  };
  const checkNameUnique = async (e) => {
    try {
      const response = await fetch(
        "http://localhost:8080/api/structures/name/" + e.currentTarget.value
      );
      if (!response.ok) {
        throw new Error("Uhhhh!");
      }
      var j = response.json();
      console.log(j.id);
    } catch (err) {
      console.log(err.message);
      // throw err;
    }
  };
  const submitEditStructure = () => {};

  createEffect(() => {
    console.log(showEditStructure());
  });

  onMount(() => {
    setStructureId(1);
  });

  return (
    <>
      <div class="flex flex-col h-screen bg-structsure-light-grey relative">
        <div class="flex flex-row h-[9vh] px-5 relative">
          <img
            src={structsureLogo}
            alt="StructSure Logo"
            class="h-full object-contain"
          />
          <div class="bg-structsure-black rounded-full size-10 flex items-center justify-center my-5 absolute right-10">
            <img
              src={userRound}
              alt="User Round"
              class="size-6 object-contain"
            />
          </div>
          <div class="bg-[#f2dcdd] rounded-full size-10 flex items-center justify-center my-5 absolute right-28">
            <img src={crown} alt="User Round" class="size-6 object-contain" />
          </div>
        </div>
        <div class="flex flex-col w-full justify-center px-48 py-3">
          <h1 class="text-2xl font-semibold">
            {structure() && structure().name}
          </h1>
          <div class="flex flex-row items-center flex-1 py-3">
            <div class="bg-structsure-white flex-1 h-10 rounded-full mr-3"></div>
            <div class="cursor-pointer bg-structsure-white rounded-full size-10 flex items-center justify-center mr-3">
              <img src={download} alt="Download" />
            </div>
            <div
              class="cursor-pointer bg-structsure-white rounded-full size-10 flex items-center justify-center mr-3"
              onClick={toggleEditStructure}
            >
              <img src={pencil} alt="Edit" />
            </div>
            <div class="cursor-pointer bg-[#f2dcdd] rounded-full size-10 flex items-center justify-center mr-3">
              <img src={trash} alt="Trash" />
            </div>
            <div class="cursor-pointer bg-structsure-black rounded-full size-10 flex items-center justify-center">
              <img src={plus} alt="Plus" />
            </div>
          </div>
        </div>
        <div class="flex flex-col w-full flex-1 justify-start px-48 py-3">
          <div class="flex flex-row w-full h-4/6 bg-structsure-dark-grey rounded-lg mb-16">
            <div class="flex flex-col w-1/3 h-full px-6 py-7">
              <h1 class="text-2xl font-semibold">Plans</h1>
            </div>
            <div class="flex flex-col flex-1 h-full bg-structsure-white rounded-lg"></div>
          </div>
          <div class="flex flex-row w-full flex-1">
            <div class="flex flex-col w-1/4 h-full bg-structsure-white rounded-lg px-6 pt-5 pb-4 mr-9">
              <h1 class="text-2xl font-semibold mb-4">Note</h1>
              <textarea
                name=""
                id=""
                class="bg-structsure-light-grey rounded-lg w-full flex-1 mb-2 px-3"
                style="resize:none"
              >
                {structure() && structure().note}
              </textarea>
            </div>
            <div class="flex flex-col flex-1 pt-5 pb-4">
              <div class="flex flex-row items-center">
                <h1 class="text-2xl font-semibold flex-1">Capteurs</h1>
                <div class="cursor-pointer bg-structsure-white rounded-full size-10 flex items-center justify-center mr-3">
                  <img src={arrowDown} alt="Download" />
                </div>
                <div class="cursor-pointer bg-structsure-white rounded-full size-10 flex items-center justify-center mr-3">
                  <img src={filter} alt="Download" />
                </div>
                <div class="cursor-pointer bg-structsure-black rounded-full size-10 flex items-center justify-center">
                  <img src={plus} alt="Plus" />
                </div>
              </div>
              <div class="flex flex-1"></div>
            </div>
          </div>
        </div>
        {showEditStructure() && (
          <>
            <div
              class="fixed inset-0 bg-black opacity-50"
              onClick={() => setShowEditStructure(false)}
            ></div>

            <div class="absolute top-[30%] left-[40%] w-1/5 h-2/5 flex flex-col bg-structsure-white text-white p-4 rounded-lg shadow-md z-50 text-black">
              <h1 class="text-2xl font-semibold mb-2">Modifier l'ouvrage</h1>
              
              <Show
                when={nameExisted()}
              >
                <p class="text-structsure-red mb-2">Nom deja utilise</p>
              </Show>
              <p class="text-gray-700 mb-2">Nom*</p>
              <textarea
                name="nameEdit"
                id=""
                maxlength="64"
                class="h-8 mb-2 px-3 py-1 resize-none bg-structsure-light-grey rounded-lg"
                onInput={checkNameUnique}
              ></textarea>
              <p class="text-gray-700 mb-2">Note</p>
              <textarea
                name="noteEdit"
                id=""
                maxlength="1000"
                class="flex-1 mb-2 px-3 py-1 resize-none bg-structsure-light-grey rounded-lg"
              ></textarea>
              <div class="flex flex-row justify-center w-full">
                <button
                  onClick={() => setShowEditStructure(false)}
                  class="w-[50%] mt-2 mx-2 py-1 bg-structsure-dark-grey rounded-full font-semibold"
                >
                  Annuler
                </button>
                <button
                  onClick={() => setShowEditStructure(false)}
                  class="w-[50%] mt-2 mx-2 px-2 py-1 bg-structsure-black text-white rounded-full font-semibold"
                >
                  Mettre à jour
                </button>
              </div>
            </div>
          </>
        )}
      </div>
    </>
  );
}

export default Structure;
