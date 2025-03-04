import {createEffect, createSignal, Show} from "solid-js";
import useFetch from "../../hooks/useFetch";
import ModalHeader from "../Modal/ModalHeader";
import ErrorMessage from "../Modal/ErrorMessage";
import ModalField from "../Modal/ModalField";
import ModalComment from "../Modal/ModalComment";
import { useNavigate } from "@solidjs/router";

/**
 * Modal for adding a sensor.
 * Displaus a form to enter a name, a section and an image.
 * @param {isOpen, onClose, onSave, structureId} param Props passed to the component
 * @param {Function} setSensorsDetail setter to set the sensors in structureDetails state
 * @param {Function} structureDetails The structure detail
 * @returns The modal component for adding a plan
 */
const ModalAddSensor = ({ isOpen, onClose, onSave, structureId, setSensorsDetail, structureDetails }) => {
  const [name, setName] = createSignal("");
  const [controlChip, setControlChip] = createSignal("");
  const [measureChip, setMeasureChip] = createSignal("");
  const [note, setNote] = createSignal("");
  const [isSubmitting, setIsSubmitting] = createSignal(false);
  const [errorMsg, setError] = createSignal("");
  const navigate = useNavigate();


  /**
   * Helper function to convert hex to base 10 value
   * @param {String} hexString 
   * @returns The base 10 value
   */
  const hexToBase10 = (hexString) => BigInt('0x' + hexString);


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
    return hex.length % 2 === 0 ? hex : '0' + hex;
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
    return /^[0-9A-F]+$/.test(cleanInput);
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

    const cleanControlChip = controlChip().replace(/\s+/g, '');
    const cleanMeasureChip = measureChip().replace(/\s+/g, '');

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
      controlChip: controlChip().trim()
    };

    const { fetchData, statusCode, error } = useFetch();

    await fetchData(navigate, "/api/sensors", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${localStorage.getItem("token")}`
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
   * Create effect to init and the get the suggestion chip values
   */
  createEffect(() => {
    const controlChipValue  = localStorage.getItem("controlChip");
    const measureChipValue  = localStorage.getItem("measureChip");
    if (controlChipValue !== null && measureChipValue !== null) {
      setControlChip(hexAddOne(controlChipValue));
      setMeasureChip(hexAddOne(measureChipValue));
    }    
  })

  return (
    <Show when={isOpen}>
      <div class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-[10px]">
        <div class="bg-white p-6 rounded-[20px] shadow-lg w-96">
          <ModalHeader title={"Ajouter un Capteur"} onClose={handleClose} onSubmit={handleSubmit} isSubmitting={isSubmitting()} />
          <Show when={errorMsg()}>
            <ErrorMessage message={errorMsg()} />
          </Show>
          <div class="space-y-4">
            <Show when={controlChip() !== "" && measureChip() !== ""} 
              fallback={
                <>
                  <ModalField label="Nom*" value={name()} maxLength={32} onInput={(e) => setName(e.target.value)} placeholder="Capteur 42" />
                  <ModalField label="Puce Témoin*" value={controlChip()} maxLength={32} onInput={(e) => setControlChip(e.target.value)} placeholder="E280 6F12 0000 002 208F FACE" /><ModalField label="Puce Mesure*" value={measureChip()} maxLength={32} onInput={(e) => setMeasureChip(e.target.value)} placeholder="E280 6F12 0000 002 208F FACD" />
                  <ModalComment note={note()} onInput={(e) => setNote(e.target.value)} />
                </>
              }
            >
                <ModalField label="Nom*" value={name()} maxLength={32} onInput={(e) => setName(e.target.value)} placeholder="Capteur 42" />
                <ModalField label="Puce Témoin*" value={controlChip()} maxLength={32} onInput={(e) => setControlChip(e.target.value)} placeholder="E280 6F12 0000 002 208F FACE" /><ModalField label="Puce Mesure*" value={measureChip()} maxLength={32} onInput={(e) => setMeasureChip(e.target.value)} placeholder="E280 6F12 0000 002 208F FACD" />
                <ModalComment note={note()} onInput={(e) => setNote(e.target.value)} />
            </Show>
          </div>
        </div>
      </div>
    </Show>
  );
};

export default ModalAddSensor;