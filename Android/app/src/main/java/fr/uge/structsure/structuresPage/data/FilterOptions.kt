package fr.uge.structsure.structuresPage.data

/**
 * Options available to filter sensors with their fancy name to
 * display to the user
 */
enum class FilterOptions(val displayName: String) {
    NOK("Non OK"),
    DEFECTIVE("Défectueux"),
    OK("Ok"),
    UNKNOWN("Inconnu")
}