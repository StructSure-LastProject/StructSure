package fr.uge.structsure.startScan.presentation.sensors.list

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import fr.uge.structsure.startScan.presentation.sensors.status.SensorState
import fr.uge.structsure.startScan.presentation.sensors.status.SensorStatus


@Preview(showBackground = true)
@Composable
fun SensorItem(sensorName: String="Default", state: SensorState = SensorState.UNSCAN) {
    SensorStatus(state, sensorName)
    // TODO Turn it into a button that open Sensor's informations
}
