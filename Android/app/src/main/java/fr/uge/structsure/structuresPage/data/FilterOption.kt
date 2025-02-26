package fr.uge.structsure.structuresPage.data

import fr.uge.structsure.scanPage.presentation.components.SensorState

/**
 * Options available to filter sensors with their fancy name to
 * display to the user
 */
enum class FilterOption(val displayName: String, val filter: (SensorDB) -> Boolean) {
    ALL("Tout", { true }),
    NOK("Non OK", { sensor -> sensor.state == SensorState.NOK.name }),
    DEFECTIVE("Défectueux", { sensor -> sensor.state == SensorState.DEFECTIVE.name }),
    OK("Ok", { sensor -> sensor.state == SensorState.OK.name }),
    UNKNOWN("Inconnu", { sensor -> sensor.state == SensorState.UNKNOWN.name || sensor.state.isEmpty() });

    companion object {
        /**
         * Tries to retrieves the enum entry that has the given
         * display name.
         * @param displayName the display name of the entry to search
         * @return the FilterOption corresponding or [ALL] by default
         */
        fun from(displayName: String): FilterOption =
            FilterOption.entries.firstOrNull { it.displayName.equals(displayName, ignoreCase = true) } ?: ALL
    }
}