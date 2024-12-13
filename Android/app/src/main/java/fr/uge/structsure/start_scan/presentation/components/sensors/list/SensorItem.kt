package fr.uge.structsure.start_scan.presentation.components.sensors.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import fr.uge.structsure.ui.theme.Defaillant
import fr.uge.structsure.ui.theme.Ok
import fr.uge.structsure.ui.theme.Red
import fr.uge.structsure.ui.theme.Unknown
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import fr.uge.structsure.start_scan.presentation.components.sensors.status.SensorState
import fr.uge.structsure.start_scan.presentation.components.sensors.status.SensorStatus
import fr.uge.structsure.ui.theme.Typography


@Preview(showBackground = true)
@Composable
fun SensorItem(sensorName: String="Default", state: SensorState= SensorState.UNSCAN) {
    SensorStatus(state, sensorName)
    // TODO Turn it into a button that open Sensor's informations
}
