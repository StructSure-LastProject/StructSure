package fr.uge.structsure.scanPage.presentation

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fr.uge.structsure.bluetooth.cs108.Cs108Connector
import fr.uge.structsure.components.Page
import fr.uge.structsure.scanPage.domain.ScanState
import fr.uge.structsure.scanPage.domain.ScanViewModel
import fr.uge.structsure.scanPage.presentation.components.SensorsList
import fr.uge.structsure.scanPage.presentation.components.StructureWeather

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
fun ScanPage(context: Context,
             scanViewModel: ScanViewModel,
             structureId: Long,
             connexionCS108: Cs108Connector,
             navController: NavController) {

    val currentState = scanViewModel.currentScanState.observeAsState(initial = ScanState.NOT_STARTED)

    SideEffect {
        if (scanViewModel.currentScanState.value == ScanState.NOT_STARTED) {
            scanViewModel.init()
        }
    }

    Page(
        Modifier.padding(bottom = 100.dp),
        bottomBar = {
            ToolBar(
                currentState = currentState.value,
                onPlayClick = {

                    scanViewModel.createNewScan(structureId)
                },
                onPauseClick = {
                    scanViewModel.pauseScan()
                },
                onStopClick = {
                    scanViewModel.stopScan()
                },
                onContentClick = {

                },
                connexionCS108 = connexionCS108,
                navController = navController
            )
        }
    ) { scrollState ->
        StructureWeather(viewModel = scanViewModel, scrollState)
        PlansView(modifier = Modifier.fillMaxWidth())
        SensorsList(modifier = Modifier.fillMaxWidth())

        scanViewModel.sensorMessages.observeAsState(null).value.let {
            if (it != null) {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                scanViewModel.sensorMessages.value = null
            }
        }

        scanViewModel.alertMessages.observeAsState(null).value.let {
            if (it != null) {
                scanViewModel.alertMessages.value = null
                navController.navigate("Alerte?state=${it.state}&name=${it.sensorName}&lastState=${it.lastStateSensor}")
            }
        }
   }
}