package fr.uge.structsure.start_scan.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import fr.uge.structsure.start_scan.domain.ScanState
import fr.uge.structsure.start_scan.domain.ScanViewModel
import fr.uge.structsure.start_scan.presentation.components.*
import fr.uge.structsure.start_scan.presentation.components.sensors.list.SensorsListView
import kotlinx.coroutines.launch

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
 *
 */
@Composable
fun MainScreenStartSensor(scanViewModel: ScanViewModel) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            ToolBar(
                currentState = scanViewModel.currentScanState.value,
                onPlayClick = { scanViewModel.fetchSensorsAndStartScan(structureId = 1) },
                onPauseClick = { scanViewModel.pauseScan() },
                onStopClick = { scanViewModel.stopScan() },
                onSyncClick = { /* À implémenter */ },
                onContentClick = { /* À implémenter */ }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            HeaderView()
            StructureSummaryView(viewModel = scanViewModel)
            PlansView(modifier = Modifier.fillMaxWidth())
            SensorsListView(modifier = Modifier.fillMaxWidth())
        }

        // Print a toast for each sensor with an "OK" state
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
