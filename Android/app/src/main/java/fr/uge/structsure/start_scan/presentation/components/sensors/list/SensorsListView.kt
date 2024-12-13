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
import fr.uge.structsure.start_scan.presentation.components.Variables
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
    val sensorBackgroundColors = remember { mutableStateListOf(*Array(5) { Variables.White }) }

    Column(
        verticalArrangement = Arrangement.spacedBy(15.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.Start,
        modifier = modifier.padding(start = 20.dp)

    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = "Capteurs",
                style = Typography.titleLarge
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier
            ) {
                SmallButton(R.drawable.arrow_down_narrow_wide, "Sort")
                SmallButton(R.drawable.filter, "Filter")
                SmallButton(R.drawable.plus, "Add", Color.White, Color.Black)
            }
        }
        if (isSensorListVisible) {
            SensorGridView(modifier = modifier)
        }
    }
}
