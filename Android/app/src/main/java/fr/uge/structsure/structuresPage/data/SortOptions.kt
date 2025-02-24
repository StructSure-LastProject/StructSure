package fr.uge.structsure.structuresPage.data

/**
 * List of all allowed sort options for sensors with their display
 * name that can be displayed to the user.
 */
enum class SortOptions(val displayName: String) {
    NAME("Nom"),
    STATE("Etat"),
    INSTALLATION_DATE("Date d'installation")
}