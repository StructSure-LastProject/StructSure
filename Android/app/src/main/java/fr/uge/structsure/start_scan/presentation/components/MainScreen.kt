package fr.uge.structsure.start_scan.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.uge.structsure.start_scan.domain.ScanState
import fr.uge.structsure.start_scan.domain.ScanViewModel
import fr.uge.structsure.start_scan.presentation.components.*
import fr.uge.structsure.start_scan.presentation.components.sensors.list.SensorsListView

/**
 * Écran principal de l'application StructSure.
 * Gère l'affichage de la structure, des capteurs et des plans associés.
 */
@Composable
fun MainScreen(scanViewModel: ScanViewModel) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            ToolBar(
                currentState = scanViewModel.currentScanState.value,
                onPlayClick = {
                    scanViewModel.createNewScan(
                        structureId = 1,
                        sensorDetails = (1..10).map {
                            "$it" to (it * 100 to it * 200)
                        }
                    )
                },
                onPauseClick = { scanViewModel.pauseScan() },
                onStopClick = { scanViewModel.stopScan() },
                onSyncClick = { /* TODO: Ajouter la logique de synchronisation */ },
                onContentClick = { /* TODO: Ajouter la gestion des notes */ }
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
            PlansView(
                modifier = Modifier.fillMaxWidth()
            )
            SensorsListView(
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Affiche un Toast personnalisé si l'état du scan est STARTED
        if (scanViewModel.currentScanState.value == ScanState.STARTED) {
            scanViewModel.sensorMessages.lastOrNull()?.let { message ->
                CustomToast(
                    message = message,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 150.dp) // Positionné au-dessus de la ToolBar
                )
            }
        }
    }
}
