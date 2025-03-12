import {createSignal, onCleanup, onMount, Show} from "solid-js";
import useFetch from "../../hooks/useFetch";
import ModalHeader from "../Modal/ModalHeader";
import ErrorMessage from "../Modal/ErrorMessage";
import ModalField from "../Modal/ModalField";
import ModalComment from "../Modal/ModalComment";
import { useNavigate } from "@solidjs/router";
import SensorFieldComponent from "../SensorPanel/SensorFieldComponent.jsx";

/**
 * Modal for adding a sensor.
 * Displaus a form to enter a name, a section and an image.
 * @param {isOpen, onClose, onSave, structureId} param Props passed to the component
 * @param {Function} setSensorsDetail setter to set the sensors in structureDetails state
 * @param {Function} structureDetails The structure detail
 * @returns The modal component for adding a plan
 */
const ModalAddSensor = ({ isOpen, onClose, nextChip, setNextChip, onSave, structureId, setSensorsDetail, structureDetails }) => {
  const [name, setName] = createSignal("");
  const [controlChip, setControlChip] = createSignal("");
  const [measureChip, setMeasureChip] = createSignal("");
  const [controlHint, setControlHint] = createSignal(nextChip() === "" ? "E280 6F12 0000 002 208F FACD" : nextChip());
  const [measureHint, setMeasureHint] = createSignal(nextChip() === "" ? "E280 6F12 0000 002 208F FACE" : nextChip());
  const [note, setNote] = createSignal("");
  const [installationDate, setInstallationDate] = createSignal("");
  const [isSubmitting, setIsSubmitting] = createSignal(false);
  const [errorMsg, setError] = createSignal("");
  const navigate = useNavigate();
  let modalRef;


  /**
   * Helper function to convert hex to base 10 value
   * @param {String} hexString 
   * @returns The base 10 value
   */
  const hexToBase10 = (hexString) => {
    const cleanHex = hexString.replace(/\s+/g, '');
    if (!/^[0-9A-Fa-f]+$/.test(cleanHex)) {
      return BigInt(0); // Return 0 for invalid input
    }
    return BigInt('0x' + cleanHex);
  }

  /**
   * Helper function to insert a space every 4 character in tag chips
   * @param {String} inputString 
   * @returns The chip value with spaces betwwen 4 caracters
   */
  const addSpaces = (inputString) => {
    return inputString.replace(/(.{4})(?=.)/g, '$1 ');
  }

  /**
   * Helper function to convert Base 10 to hexa value
   * @param {String} base10Number 
   * @returns The hex value
   */
  const base10ToHex = (base10Number) => {
    const hex = base10Number.toString(16).toUpperCase();
    return hex;
  };

  /**
   * Add +1 to the hexa value
   * @param {String} hexString The hexa value
   * @returns The new hexa value suggestion
   */
  const hexAddOne = (hexString) => {
    const base10Value = hexToBase10(hexString);
    const newBase10Value = base10Value + 1n;
    return addSpaces(base10ToHex(newBase10Value));
  }

  /**
   * Validates that the input contains only hexadecimal characters (ignoring spaces)
   * @param {String} input The input to validate
   * @returns {Boolean} True if the input is valid, false otherwise
   */
  const validateHexInput = (input) => {
    const cleanInput = input.replace(/\s+/g, '');
    return /^[0-9A-Fa-f]+$/.test(cleanInput);
  };

  /**
   * Save the chips in local storage
   * @param {String} controlChip The control chip
   * @param {String} measureChip The measure chip
   */
  const saveTagOnLocalStorage = (controlChip, measureChip) => {
    localStorage.setItem("controlChip", controlChip);
    localStorage.setItem("measureChip", measureChip);
  }

  /**
   * Updates the sensor details by adding a new sensor to the existing list.
   *
   * This function retrieves the current list of sensors from `structureDetails()` 
   * and appends a new sensor object with trimmed values from input functions.
   *
   * @function
   * @returns {void} Updates the sensor details state.
   */
  const updateDataWhenNewSensor = () => {
    setSensorsDetail([...structureDetails().sensors, {
      name: name().trim(),
      controlChip: controlChip().trim(),
      measureChip: measureChip().trim(),
      installationDate: installationDate(),
      x: null,
      y: null
    }]);
  }

  /**
   * Handles the form submission for adding a new sensor.
   * Validates the form data, creates a json object and sends it to the server.
   *
   * @returns {Promise<void>} A promise that resolves when the submission is complete
   * @throws Will display an error message if the submission fails
   */
  const handleSubmit = async () => {
    setError("")
    if (!name().trim()) {
      setError("Le nom est requis");
      return;
    }

    const regex = /^[\w@-][\w @-]+$/;
    if(!regex.test(name())) {
      setError("Le nom doit contenir uniquement des lettres, des chiffres, des espaces, des underscores et des @");
      return;
    }

    const cleanControlChip = controlChip().replace(/\s+/g, '').toUpperCase();
    const cleanMeasureChip = measureChip().replace(/\s+/g, '').toUpperCase();

    if (!cleanControlChip) {
      setError("La puce témoin est requise");
      return;
    }
    if (!cleanMeasureChip) {
      setError("La puce de mesure est requise");
      return;
    }
    if (!validateHexInput(cleanControlChip)) {
      setError("La puce témoin doit contenir uniquement des caractères hexadécimaux (0-9, A-F)");
      return;
    }
    if (!validateHexInput(cleanMeasureChip)) {
      setError("La puce de mesure doit contenir uniquement des caractères hexadécimaux (0-9, A-F)");
      return;
    }
    if (cleanControlChip === cleanMeasureChip) {
      setError("Les deux puces ne peuvent pas avoir la même valeur");
      return;
    }

    setIsSubmitting(true);
    setError("");

    const body = {
      structureId: structureId,
      name: name().trim(),
      note: note().trim(),
      measureChip: measureChip().trim(),
      controlChip: controlChip().trim(),
      installationDate: installationDate()
    };

    const { fetchData, statusCode, error } = useFetch();

    await fetchData(navigate, "/api/sensors", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(body)
    });

    if (statusCode() === 201) {
      updateDataWhenNewSensor();
      onSave();
      saveTagOnLocalStorage(cleanControlChip, cleanMeasureChip);
      handleClose();
    } else {
      setError(error()?.errorData?.error || "Une erreur est survenue");
    }

    setIsSubmitting(false);
  };

  /**
   * Updates the nextChip value and all the placeholders.
   * @param {String} value of the current field
   * @returns value
   */
  const updateChip = (value) => {
    if (validateHexInput(value)) {
      setNextChip(hexAddOne(value.replace(/\s+/g, '')));
      setControlHint(nextChip())
      setMeasureHint(nextChip())

      const controlInput = document.getElementById("addSensorControl");
      const measureInput = document.getElementById("addSensorMeasure");

      if (controlInput) controlInput.placeholder = nextChip();
      if (measureInput) measureInput.placeholder = nextChip();
    }
    return value;
  }

  /**
   * Resets the modal state and closes it.
   * Clears all form fields and any error messages before calling the onClose callback.
   */
  const handleClose = () => {
    setName("");
    setControlChip("");
    setMeasureChip("");
    setNote("");
    setError("");
    onClose();
  };

  /**
   * Put the auto-complete value in the field when pressing tab.
   * @param {Event} e the event to check
   * @returns false to cancel the event
   */
  const autoComplete = (e) => {
    if (e.target.value === "" && e.keyCode == 9) {
        e.preventDefault();
        e.target.value = nextChip();
        if (e.target.id === "addSensorControl") {
            setControlChip(e.target.value);
        } else {
            setMeasureChip(e.target.value);
        }
        updateChip(nextChip());
        return false;
    }
  }
    
  /**
   * Handles the close of the modal when click outside
   * @param {Event} event 
   */
  const handleClickOutside = (event) => {
      if (modalRef && !modalRef.contains(event.target)) {
          onClose();
      }
  };

  onMount(() => {
      document.addEventListener("mousedown", handleClickOutside);
      document.getElementById("addSensorControl").addEventListener("keydown", autoComplete)
      document.getElementById("addSensorMeasure").addEventListener("keydown", autoComplete)
  });

  onCleanup(() => {
      document.removeEventListener("mousedown", handleClickOutside);
  });

  return (
    <Show when={isOpen}>
      <div class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-[10px]">
        <div ref={modalRef} class="bg-white p-6 rounded-[20px] shadow-lg w-96">
          <ModalHeader title={"Ajouter un Capteur"} onClose={handleClose} onSubmit={handleSubmit} isSubmitting={isSubmitting()} />
          <Show when={errorMsg()}>
            <ErrorMessage message={errorMsg()} />
          </Show>
          <div class="space-y-4">
            <ModalField label="Nom*" value={name()} maxLength={32} onInput={(e) => setName(e.target.value)} placeholder="Capteur 42" />
            <ModalField id="addSensorControl" label="Puce Témoin*" value={controlChip()} maxLength={32} onChange={(e) => setControlChip(updateChip(e.target.value))} placeholder={ controlHint() } />
            <ModalField id="addSensorMeasure" label="Puce Mesure*" value={measureChip()} maxLength={32} onChange={(e) => setMeasureChip(updateChip(e.target.value))} placeholder={ measureHint() } />
            <SensorFieldComponent
              title={"Date d’installation"}
              value={installationDate}
              editMode={()=>{return true;}}
              type={"date"}
              setter={setInstallationDate}
              styles="rounded-[10px] px-[16px] py-[8px] w-full bg-lightgray normal text-black"
            />
            <ModalComment note={note()} onInput={(e) => setNote(e.target.value)} />
          </div>
        </div>
      </div>
    </Show>
  );
};

export default ModalAddSensor;