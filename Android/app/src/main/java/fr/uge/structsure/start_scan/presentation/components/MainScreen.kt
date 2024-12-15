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
 * Écran principal de l'application pour gérer le scan d'une structure.
 * - Affiche les plans, les capteurs et l'état du scan.
 * - Contrôle les actions du scan via la ToolBar.
 *
 * @param scanViewModel ViewModel responsable de la logique métier du scan.
 */
@Composable
fun MainScreen(scanViewModel: ScanViewModel) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            ToolBar(
                currentState = scanViewModel.currentScanState.value, // État actuel du scan
                onPlayClick = {
                    // Si le scan est en pause, reprenez l'interrogation des capteurs
                    if (scanViewModel.currentScanState.value == ScanState.PAUSED) {
                        scanViewModel.viewModelScope.launch {
                            scanViewModel.resumeScan(scanViewModel.getSensors())
                        }
                    } else {
                        // Crée un nouveau scan avec des données simulées pour les tests
                        scanViewModel.createNewScan(
                            structureId = 1,
                            sensorDetails = (1..10000).map {
                                "$it" to (it * 100 to it * 200) // Capteurs avec des coordonnées fictives
                            }
                        )
                    }
                },
                onPauseClick = { scanViewModel.pauseScan() }, // Pause le scan
                onStopClick = { scanViewModel.stopScan() },   // Arrête le scan
                onSyncClick = { /* Ajouter une logique de synchronisation future */ },
                onContentClick = { /* Ajouter une logique pour afficher des notes */ }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()) // Rend le contenu défilable verticalement
        ) {
            HeaderView()
            StructureSummaryView(viewModel = scanViewModel) // Résumé de la structure scannée
            PlansView(modifier = Modifier.fillMaxWidth()) // Affiche les plans liés à la structure
            SensorsListView(modifier = Modifier.fillMaxWidth()) // Liste des capteurs scannés
        }

        // Affiche un toast pour chaque capteur avec un état "OK"
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
