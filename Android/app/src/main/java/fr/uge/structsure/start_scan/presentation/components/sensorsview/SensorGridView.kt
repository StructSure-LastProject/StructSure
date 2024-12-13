package fr.uge.structsure.start_scan.presentation.components.sensorsview

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview

val SENSORS_NUMBER = 30

@Preview(showBackground = true)
@Composable
fun SensorGridView(
    modifier: Modifier = Modifier
) {
    val sensorStates =
        remember { List(SENSORS_NUMBER) { (0..3).random() } }        // Temporary random sensor list
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val screenHeight = configuration.screenHeightDp.dp

    // LazyVerticalGrid -> s'afficher en grille en fonction de l'orientation
    LazyColumn (
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White, shape = RoundedCornerShape(16.dp))
            .heightIn(min = 0.dp, max = screenHeight * 0.8f)
            .padding(4.dp)
    ) {
        items(SENSORS_NUMBER) { index ->
            // Affichage de chaque capteur
            SensorItem(sensorName = "Capteur $index", state = sensorStates[index])
        }
    }
}
