package fr.uge.structsure.scanPage.presentation.components

import androidx.compose.ui.graphics.Color
import fr.uge.structsure.ui.theme.Gray
import fr.uge.structsure.ui.theme.Ok
import fr.uge.structsure.ui.theme.Red
import fr.uge.structsure.ui.theme.Unknown

/**
 * Represents the state of a sensor with an associated color for UI representation.
 *
 * @param color The color associated with the sensor state.
 */
enum class SensorState(val displayName: String, val color: Color) {
    OK("OK", Ok),
    NOK("NOk", Red),
    DEFECTIVE("Défaillant", Gray),
    UNKNOWN("Non scanné", Unknown);

    companion object {
        /**
         * Converts a string to the corresponding `SensorState`.
         *
         * @param name The string representing the sensor state.
         * @return The corresponding `SensorState`, or `UNKNOWN` if no match is found.
         */
        fun from(name: String): SensorState =
            entries.firstOrNull { it.name.equals(name, ignoreCase = true) } ?: UNKNOWN

        /**
         * Gets the display name for a sensor state
         */
        fun getStateDisplayName(state: String): String = SensorState.from(state).displayName
    }
}
