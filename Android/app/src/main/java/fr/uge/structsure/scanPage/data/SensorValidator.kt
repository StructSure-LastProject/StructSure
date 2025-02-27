package fr.uge.structsure.scanPage.data

/**
 * Object containing validation functions for sensor data.
 */
object SensorValidator {
    const val MAX_NAME_LENGTH = 32
    const val MAX_NOTE_LENGTH = 1000
    const val MAX_CHIP_LENGTH = 32
    private const val MIN_NAME_LENGTH = 8
    private const val MIN_CHIP_LENGTH = 8

    private fun validateName(name: String): String? = when {
        name.isBlank() -> "Le nom est obligatoire"
        name.length < MIN_NAME_LENGTH -> "Le nom doit contenir au moins $MIN_NAME_LENGTH caractère"
        name.length > MAX_NAME_LENGTH -> "Le nom doit contenir au maximum $MAX_NAME_LENGTH caractères"
        else -> null
    }

    private fun validateChip(chip: String, otherChip: String? = null): String? {
        val raw = chip.replace(" ", "")
        val otherRaw = otherChip?.replace(" ", "")
        return when {
            raw.isBlank() -> "La puce est obligatoire"
            raw.length < MIN_CHIP_LENGTH -> "La puce doit contenir au moins $MIN_CHIP_LENGTH caractère"
            raw.length > MAX_CHIP_LENGTH -> "La puce doit contenir au maximum $MAX_CHIP_LENGTH caractères"
            otherRaw != null && raw == otherRaw -> "Les puces doivent être différentes"
            else -> null
        }
    }

    private fun validateNote(note: String?): String? = when {
        note != null && note.length > MAX_NOTE_LENGTH -> "La note doit contenir au maximum $MAX_NOTE_LENGTH caractères"
        else -> null
    }

    fun validate(controlChip: String, measureChip: String, name: String, note: String): ValidationResult? {
        val nameErr = validateName(name)
        val controlErr = validateChip(measureChip)
        val measureErr = validateChip(measureChip, controlChip)
        val noteErr = validateNote(note)

        return if (nameErr == null && controlErr == null && measureErr == null && noteErr == null) null // no error
        else ValidationResult(controlErr, measureErr, nameErr, noteErr)
    }
}


/**
 * Form state for the add sensor form.
 * @param controlChipError error message for the control chip field
 * @param measureChipError error message for the measure chip field
 * @param nameError error message for the name field
 * @param noteError error message for the note field
 */
data class ValidationResult(
    val controlChipError: String? = null,
    val measureChipError: String? = null,
    val nameError: String? = null,
    val noteError: String? = null
)
