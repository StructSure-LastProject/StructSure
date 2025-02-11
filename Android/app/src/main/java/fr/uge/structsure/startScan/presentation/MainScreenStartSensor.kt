package fr.uge.structsure.startScan.presentation

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fr.uge.structsure.bluetooth.cs108.Cs108Connector
import fr.uge.structsure.bluetooth.cs108.Cs108Scanner
import fr.uge.structsure.components.Page
import fr.uge.structsure.startScan.domain.ScanViewModel
import fr.uge.structsure.startScan.presentation.components.SensorsList
import fr.uge.structsure.startScan.presentation.components.StructureWeather

/**
 * Home screen of the application when the user starts a scan.
 * It displays the header, the summary of the scanned structure, the plans and the list of sensors.
 * It also displays a toast for each sensor with an "OK" state.
 * @param scanViewModel ViewModel containing the data of the scan.
 * @param structureId id of the structure to display a scan page for
 * @param connexionCS108 connection to the CS108 scanner to monitor its
 *     state from the toolbar
 * @param navController to navigate to other screens
 */
@Composable
fun MainScreenStartSensor(context: Context,
                          scanViewModel: ScanViewModel,
                          structureId: Long,
                          connexionCS108: Cs108Connector,
                          navController: NavController) {

    val cs108Scanner = remember {
        Cs108Scanner { chip ->
            Log.d("MainScreenStartSensor", "Chip scanned: ${chip.id}")
            scanViewModel.onTagScanned(chip.id)
        }
    }

    SideEffect {
        scanViewModel.fetchSensors(structureId)
    }

    Page(
        Modifier.padding(bottom = 100.dp),
        bottomBar = {
            ToolBar(
                currentState = scanViewModel.currentScanState.value,
                onPlayClick = {
                    scanViewModel.createNewScan(structureId)
                    cs108Scanner.start()
                },
                onPauseClick = {
                    scanViewModel.pauseScan()
                },
                onStopClick = {
                    scanViewModel.stopScan()
                },
                onContentClick = { },
                connexionCS108 = connexionCS108,
                navController = navController
            )
        }
    ) { scrollState ->
        StructureWeather(viewModel = scanViewModel, scrollState)
        PlansView(modifier = Modifier.fillMaxWidth())
        SensorsList(modifier = Modifier.fillMaxWidth())

        val sensorMsg = scanViewModel.sensorMessages.observeAsState().value
        sensorMsg?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }

        scanViewModel.alertMessages.observeAsState(null).value.let {
            if (it != null) {
                scanViewModel.alertMessages.value = null
                navController.navigate("Alerte?state=true&name=${it.sensorName}&lastState=${it.lastStateSensor}")
            }
        }
   }
}