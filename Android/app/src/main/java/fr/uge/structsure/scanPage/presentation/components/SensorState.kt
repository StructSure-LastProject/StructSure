package fr.uge.structsure.scanPage.presentation.components

import androidx.compose.ui.graphics.Color
import fr.uge.structsure.ui.theme.Gray
import fr.uge.structsure.ui.theme.Ok
import fr.uge.structsure.ui.theme.Red
import fr.uge.structsure.ui.theme.Unscanned

/**
 * Represents the state of a sensor with an associated color for UI representation.
 *
 * @param color The color associated with the sensor state.
 */
enum class SensorState(val color: Color) {
    OK(Ok),
    NOK(Red),
    DEFECTIVE(Gray),
    UNSCANNED(Unscanned);

    companion object {
        /**
         * Converts a string to the corresponding `SensorState`.
         *
         * @param name The string representing the sensor state.
         * @return The corresponding `SensorState`, or `UNKNOWN` if no match is found.
         */
        fun from(name: String): SensorState =
            entries.firstOrNull { it.name.equals(name, ignoreCase = true) } ?: UNSCANNED
    }
}
