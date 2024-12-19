package fr.uge.structsure.start_scan.presentation.components.sensors.list

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview


@Preview(showBackground = true)
@Composable
fun SensorGridView(
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val screenHeight = configuration.screenHeightDp.dp

    // LazyVerticalGrid -> orientation-dependent grid display
    LazyColumn (
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White, shape = RoundedCornerShape(16.dp))
            .heightIn(min = 0.dp, max = screenHeight * 0.8f)
            .padding(4.dp)
    ) {
        items(SENSORS_NUMBER) { index ->
            // SensorItem -> display of a sensor
            SensorItem(sensorName = "Capteur $index", state = SENSORS_STATES_LIST[index])
        }
    }
}
