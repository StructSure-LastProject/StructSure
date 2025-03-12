package fr.uge.structsure.scanPage.data

import fr.uge.structsure.MainActivity.Companion.db

/**
 * Object containing validation functions for sensor data.
 */
object SensorValidator {
    const val MAX_NAME_LENGTH = 32
    const val MAX_NOTE_LENGTH = 1000
    const val MAX_CHIP_LENGTH = 32

    private fun validateName(name: String): String? = when {
        name.isBlank() -> "Le nom est obligatoire"
        !name.matches(Regex("^[\\w@-][\\w @-]+\$")) -> "Le nom doit contenir uniquement des lettres, des chiffres, des espaces, des underscores et des @"
        name.length > MAX_NAME_LENGTH -> "Le nom doit contenir au maximum $MAX_NAME_LENGTH caractères"
        else -> null
    }

    private fun validateChip(chip: String, otherChip: String? = null): String? {
        val raw = chip.replace(" ", "")
        val otherRaw = otherChip?.replace(" ", "")
        return when {
            raw.isBlank() -> "La puce est obligatoire"
            !raw.matches(Regex("^[0-9A-Fa-f]+\$")) -> "La puce doit contenir uniquement des chiffres et lettre de A à F"
            otherRaw != null && raw == otherRaw -> "Les puces doivent être différentes"
            db.sensorDao().findSensor(raw) != null -> "Un capteur avec cette puces existe déjà"
            else -> null
        }
    }

    private fun validateNote(note: String?): String? = when {
        note != null && note.length > MAX_NOTE_LENGTH -> "La note doit contenir au maximum $MAX_NOTE_LENGTH caractères"
        else -> null
    }

    fun validate(controlChip: String, measureChip: String, name: String, note: String): ValidationResult? {
        val nameErr = validateName(name)
        val controlErr = validateChip(controlChip)
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
