package fr.uge.structsure.structuresPage.data

/**
 * List of all allowed sort options for sensors with their display
 * name that can be displayed to the user.
 */
enum class SortOption(val displayName: String, val sort: (List<SensorDB>) -> List<SensorDB>) {
    NAME("Nom", { list -> list.sortedBy { it.name } }),
    STATE("Etat", { list -> list.sortedBy { it.state } }),
    INSTALLATION_DATE("Date d'installation", { list -> list.sortedBy { it.installationDate } });

    companion object {
        /**
         * Tries to retrieves the enum entry that has the given
         * display name.
         * @param displayName the display name of the entry to search
         * @return the SortOption corresponding or [NAME] by default
         */
        fun from(displayName: String): SortOption =
            SortOption.entries.firstOrNull { it.displayName.equals(displayName, ignoreCase = true) } ?: NAME
    }
}