package fr.uge.structsure.start_scan.presentation.components.sensorsview

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalConfiguration

val SENSORS_NUMBER = 30

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
    LazyVerticalGrid(
        columns = GridCells.Fixed(if (isLandscape) 4 else 2), // 3 colonnes en mode paysage, 1 en portrait
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 0.dp, max = screenHeight * 0.8f)
    ) {
        items(SENSORS_NUMBER) { index ->
            // Affichage de chaque capteur
            SensorItem(sensorName = "Capteur $index", state = sensorStates[index])
        }
    }
}
