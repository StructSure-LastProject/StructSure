package fr.uge.structsure.scanPage.data

/**
 * Form state for the add sensor form.
 * @param controlChipError error message for the control chip field
 * @param measureChipError error message for the measure chip field
 * @param nameError error message for the name field
 * @param dateError error message for the date field
 */
data class AddSensorFormState(
    val controlChipError: String? = null,
    val measureChipError: String? = null,
    val nameError: String? = null,
)
