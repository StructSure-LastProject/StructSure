package fr.uge.structsure.scanPage.data

/**
 * Object containing validation functions for sensor data.
 */
object SensorValidation {
    const val MAX_NAME_LENGTH = 32
    const val MAX_NOTE_LENGTH = 1000
    const val MAX_CHIP_LENGTH = 32
    const val MIN_NAME_LENGTH = 8
    const val MIN_CHIP_LENGTH = 8

    fun validateName(name: String): ValidationResult {
        return when {
            name.isBlank() -> ValidationResult.Error("Le nom est obligatoire")
            name.length < MIN_NAME_LENGTH -> ValidationResult.Error("Le nom doit contenir au moins $MIN_NAME_LENGTH caractère")
            name.length > MAX_NAME_LENGTH -> ValidationResult.Error("Le nom doit contenir au maximum $MAX_NAME_LENGTH caractères")
            else -> ValidationResult.Success
        }
    }

    fun validateChip(chip: String, otherChip: String? = null): ValidationResult {
        return when {
            chip.isBlank() -> ValidationResult.Error("La puce est obligatoire")
            chip.length < MIN_CHIP_LENGTH -> ValidationResult.Error("La puce doit contenir au moins $MIN_CHIP_LENGTH caractère")
            chip.length > MAX_CHIP_LENGTH -> ValidationResult.Error("La puce doit contenir au maximum $MAX_CHIP_LENGTH caractères")
            otherChip != null && chip == otherChip -> ValidationResult.Error("Les puces doivent être différentes")
            else -> ValidationResult.Success
        }
    }

    fun validateNote(note: String?): ValidationResult {
        return when {
            note != null && note.length > MAX_NOTE_LENGTH ->
                ValidationResult.Error("La note doit contenir au maximum $MAX_NOTE_LENGTH caractères")
            else -> ValidationResult.Success
        }
    }
}

/**
 * Sealed class representing the result of a validation.
 */
sealed class ValidationResult {
    data object Success : ValidationResult()
    data class Error(val message: String) : ValidationResult()
}