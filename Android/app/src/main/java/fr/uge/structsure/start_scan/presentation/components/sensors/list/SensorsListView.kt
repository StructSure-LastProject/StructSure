package fr.uge.structsure.start_scan.presentation.components.sensors.list

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import fr.uge.structsure.R
import fr.uge.structsure.bluetoothConnection.presentation.SmallButton
import fr.uge.structsure.start_scan.presentation.components.plans.Variables
import fr.uge.structsure.start_scan.presentation.components.sensors.status.SensorState
import fr.uge.structsure.ui.theme.Typography

// Temporary variables for the sensors list
const val SENSORS_NUMBER = 30
val SENSORS_STATES_LIST = List(SENSORS_NUMBER) { SensorState.entries.toTypedArray().random() }

@Preview(showBackground = true)
@Composable
fun SensorsListView(
    modifier: Modifier = Modifier
) {
    var isSensorListVisible by remember { mutableStateOf(true) }

    Column(
        verticalArrangement = Arrangement.spacedBy(15.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.Start,
        modifier = modifier.padding(20.dp)

    ) {
        SensorListHeader()
        if (isSensorListVisible) {
            SensorList(modifier = modifier)
        }
    }
}
