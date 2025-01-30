package fr.uge.structsure.startScan.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fr.uge.structsure.bluetooth.cs108.Cs108Scanner
import fr.uge.structsure.components.Page
import fr.uge.structsure.startScan.domain.ScanState
import fr.uge.structsure.startScan.domain.ScanViewModel
import fr.uge.structsure.startScan.presentation.components.*
import fr.uge.structsure.startScan.presentation.sensors.list.SensorsListView

/**
 * Home screen of the application when the user starts a scan.
 * It displays the header, the summary of the scanned structure, the plans and the list of sensors.
 * It also displays a toast for each sensor with an "OK" state.
 * @param scanViewModel ViewModel containing the data of the scan.
 * @see HeaderView
 * @see StructureSummaryView
 * @see PlansView
 * @see SensorsListView
 * @see CustomToast
 * @see ToolBar
 */
@Composable
fun MainScreenStartSensor(scanViewModel: ScanViewModel, structureId: Long, navController: NavController) {

    val cs108Scanner = remember { mutableStateOf(Cs108Scanner { chip ->
        scanViewModel.addSensorMessage("Capteur : ${chip.id}")
    }) }

    SideEffect {
        scanViewModel.fetchSensorsAndStartScan(structureId)
    }

    Page(
        bottomBar = {
            ToolBar(
                currentState = scanViewModel.currentScanState.value,
                onPlayClick = {
                    println("before scan state " + scanViewModel.currentScanState.value)
                    cs108Scanner.value.start()
                    scanViewModel.currentScanState.value = ScanState.STARTED
                    println("after scan state " + scanViewModel.currentScanState.value)
                },
                onPauseClick = {
                    cs108Scanner.value.stop()
                    scanViewModel.currentScanState.value = ScanState.PAUSED
                },
                onStopClick = {
                    scanViewModel.stopScan()
                    scanViewModel.currentScanState.value = ScanState.STOPPED
                },
                onSyncClick = { /* À implémenter */ },
                onContentClick = { /* À implémenter */ },
                navController = navController
            )
        }
    ) { scrollState ->
        StructureSummaryView(viewModel = scanViewModel, scrollState)
        PlansView(modifier = Modifier.fillMaxWidth())
        SensorsListView(modifier = Modifier.fillMaxWidth())
        // TODO Display alert
        // navController.navigate("Alerte?state=true&name=Sensor&lastState=Ok") // true for NOK, false for Failing

        println("toast " + scanViewModel.currentScanState.value)
        if (scanViewModel.currentScanState.value == ScanState.STARTED) {
            scanViewModel.sensorMessages.lastOrNull()?.let { message ->
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