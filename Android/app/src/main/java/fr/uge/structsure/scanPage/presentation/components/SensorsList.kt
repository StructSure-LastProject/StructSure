package fr.uge.structsure.scanPage.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import fr.uge.structsure.R
import fr.uge.structsure.components.Button
import fr.uge.structsure.scanPage.data.ResultSensors
import fr.uge.structsure.scanPage.domain.ScanViewModel
import fr.uge.structsure.structuresPage.data.SensorDB
import fr.uge.structsure.ui.theme.Typography
import fr.uge.structsure.ui.theme.White

// Temporary variables for the sensors list
const val SENSORS_NUMBER = 30



@Composable
fun SensorsList(viewModel: ScanViewModel) {
    val sensorsScanned by viewModel.sensorsScanned.observeAsState(emptyList())
    val sensorsNotScanned by viewModel.sensorsNotScanned.observeAsState(emptyList())

    Column(
        verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.Top),
        horizontalAlignment = Alignment.Start,
    ) {
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

        Box(modifier = Modifier.heightIn(max = 400.dp)) {
            List(sensorsScanned, sensorsNotScanned)
        }
    }
}
@Composable
private fun List(sensorsScanned: List<ResultSensors>, sensorsNotScanned: List<SensorDB>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = White, shape = RoundedCornerShape(size = 20.dp))
            .padding(start = 20.dp, top = 15.dp, end = 20.dp, bottom = 15.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.Top),
        horizontalAlignment = Alignment.Start,
    ) {
        items(sensorsNotScanned) { sensor ->
            SensorBean(Modifier.fillMaxWidth(), sensor.name, SensorState.UNKNOWN) {
                println("Sensor ${sensor.name}")
            }
        }
        items(sensorsScanned) { sensor ->
            val state = when (sensor.state) {
                "OK" -> SensorState.OK
                "NOK" -> SensorState.NOK
                "DEFECTIVE" -> SensorState.DEFECTIVE
                else -> SensorState.UNKNOWN
            }

            SensorBean(Modifier.fillMaxWidth(), sensor.id, state) {
                println("Sensor ${sensor.id}")
            }
        }
    }
}

