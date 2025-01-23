package fr.uge.structsure.startScan.presentation

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import fr.uge.structsure.bluetooth.cs108.Cs108Connector
import fr.uge.structsure.bluetooth.cs108.Cs108Scanner
import fr.uge.structsure.components.Page
import fr.uge.structsure.retrofit.response.GetAllSensorsResponse
import fr.uge.structsure.startScan.domain.ScanState
import fr.uge.structsure.startScan.domain.ScanViewModel
import fr.uge.structsure.startScan.presentation.components.CustomToast
import fr.uge.structsure.startScan.presentation.components.SensorsList
import fr.uge.structsure.startScan.presentation.components.StructureWeather
import kotlinx.coroutines.launch

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
fun MainScreenStartSensor(context: Context, scanViewModel: ScanViewModel, structureId: Long, connexionCS108: Cs108Connector, navController: NavController) {

    val sensors = listOf<GetAllSensorsResponse>()

    val cs108Scanner = remember { mutableStateOf(Cs108Scanner { chip ->
        println(chip)
        scanViewModel.viewModelScope.launch {
            scanViewModel.insertSensorsAndStartScan(chip, sensors)
            Toast.makeText(context, "Capteur : ${chip.id}" + " is ok ! ", Toast.LENGTH_SHORT).show()
        }
    }) }

    SideEffect {
        scanViewModel.fetchSensorsAndStartScan(structureId)
    }

    Page(
        Modifier.padding(bottom = 100.dp),
        bottomBar = {
            ToolBar(
                currentState = scanViewModel.currentScanState.value,
                onPlayClick = {
                    cs108Scanner.value.start()
                    scanViewModel.currentScanState.value = ScanState.STARTED
                },
                onPauseClick = {
                    cs108Scanner.value.stop()
                    scanViewModel.currentScanState.value = ScanState.PAUSED
                },
                onStopClick = {
                    scanViewModel.stopScan()
                    scanViewModel.currentScanState.value = ScanState.STOPPED
                },
                onContentClick = { /* À implémenter */ },
                connexionCS108 = connexionCS108,
                navController = navController
            )
        }
    ) { scrollState ->
        StructureWeather(viewModel = scanViewModel, scrollState)
        PlansView(modifier = Modifier.fillMaxWidth())
        SensorsList(modifier = Modifier.fillMaxWidth())
        // TODO Display alert
        // navController.navigate("Alerte?state=true&name=Sensor&lastState=Ok") // true for NOK, false for Failing

        println("toast " + scanViewModel.currentScanState.value)
        if (scanViewModel.currentScanState.value == ScanState.STARTED) {
           scanViewModel.sensorMessages.observeAsState().value?.let { message ->
                CustomToast(
                    message = message,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 150.dp)
                )
            }
        }
    }
}