package fr.uge.structsure.startScan.presentation.sensors.status

import androidx.compose.ui.graphics.Color
import fr.uge.structsure.ui.theme.Gray
import fr.uge.structsure.ui.theme.Ok
import fr.uge.structsure.ui.theme.Red
import fr.uge.structsure.ui.theme.Unknown

enum class SensorState(val color : Color) {
    OK(Ok),
    NOK(Red),
    DEFECTIVE(Gray), // Orange color
    UNSCAN(Unknown)
}