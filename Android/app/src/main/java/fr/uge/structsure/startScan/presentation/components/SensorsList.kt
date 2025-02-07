package fr.uge.structsure.startScan.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.uge.structsure.R
import fr.uge.structsure.components.Button
import fr.uge.structsure.ui.theme.Typography
import fr.uge.structsure.ui.theme.White

// Temporary variables for the sensors list
const val SENSORS_NUMBER = 30
val SENSORS_STATES_LIST = List(SENSORS_NUMBER) { SensorState.entries.toTypedArray().random() }

@Preview(showBackground = true)
@Composable
fun SensorsList(
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.Top),
        horizontalAlignment = Alignment.Start,
    ) {
        /* Structure name and note button */
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.End),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "Capteurs",
                Modifier.weight(1f),
                style = Typography.titleLarge
            )

            Button(R.drawable.arrow_down_narrow_wide, "Sort", Color.Black, Color.White)
            Button(R.drawable.filter, "Filter", Color.Black, Color.White)
            Button(R.drawable.plus, "Add", Color.White, Color.Black)
        }

        List()
    }
}

@Composable
private fun List() {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    LazyColumn (
        modifier = Modifier
            .fillMaxWidth()
            .background(color = White, shape = RoundedCornerShape(size = 20.dp))
            .padding(start = 20.dp, top = 15.dp, end = 20.dp, bottom = 15.dp)
            .heightIn(min = 0.dp, max = screenHeight * 0.8f),
        verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.Top),
        horizontalAlignment = Alignment.Start,
    ) {
        items(SENSORS_NUMBER) { index ->
            // SensorItem -> display of a sensor
            SensorBean(Modifier.fillMaxWidth(), "Capteur $index", SENSORS_STATES_LIST[index]) {
                println("Sensor $index")
            }
        }
    }
}
