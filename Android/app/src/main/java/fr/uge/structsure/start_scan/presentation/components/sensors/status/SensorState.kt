package fr.uge.structsure.start_scan.presentation.components.sensors.status

import androidx.compose.ui.graphics.Color
import fr.uge.structsure.ui.theme.Defaillant
import fr.uge.structsure.ui.theme.Ok
import fr.uge.structsure.ui.theme.Red
import fr.uge.structsure.ui.theme.Unknown

enum class SensorState(val color : Color) {
    OK(Ok),
    NOK(Red),
    DEFECTIVE(Defaillant), // Orange color
    UNSCAN(Unknown)
}