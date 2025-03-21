package fr.uge.structsure.scanPage.presentation

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fr.uge.structsure.MainActivity.Companion.db
import fr.uge.structsure.R
import fr.uge.structsure.bluetooth.cs108.Cs108Connector
import fr.uge.structsure.components.Button
import fr.uge.structsure.components.InputTextArea
import fr.uge.structsure.components.Page
import fr.uge.structsure.components.PlanForSensor
import fr.uge.structsure.components.PopUp
import fr.uge.structsure.components.SensorDetails
import fr.uge.structsure.components.Title
import fr.uge.structsure.navigateNoReturn
import fr.uge.structsure.scanPage.domain.ScanState
import fr.uge.structsure.scanPage.domain.ScanViewModel
import fr.uge.structsure.scanPage.presentation.components.ScanWeather
import fr.uge.structsure.scanPage.presentation.components.SensorState.Companion.getStateDisplayName
import fr.uge.structsure.scanPage.presentation.components.SensorsList
import fr.uge.structsure.structuresPage.data.SensorDB
import fr.uge.structsure.ui.theme.Black
import fr.uge.structsure.ui.theme.LightGray
import fr.uge.structsure.ui.theme.Red
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
fun ScanPage(context: Context,
             scanViewModel: ScanViewModel,
             structureId: Long,
             connexionCS108: Cs108Connector,
             navController: NavController) {

    val currentState = scanViewModel.currentScanState.observeAsState(initial = ScanState.NOT_STARTED)
    scanViewModel.setStructure(context, structureId)

    var sensorPopup by remember { mutableStateOf<SensorDB?>(null) } // Control the popup visibility and hold popup data
    var showScanNotePopup by remember { mutableStateOf(false) } // Control the scan note popup visibility

    val showToast: (String) -> Unit = { message ->
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    Page(
        Modifier.padding(bottom = 100.dp),
        navController = navController,
        bottomBar = {
            ToolBar(
                currentState = currentState.value,
                onPlayClick = {
                    scanViewModel.startNewScan(structureId)
                },
                onPauseClick = {
                    scanViewModel.pauseScan()
                },
                onStopClick = {
                    scanViewModel.stopScan()
                    navController.navigateNoReturn("HomePage")
                },
                onContentClick = {
                    showScanNotePopup = true
                },
                connexionCS108 = connexionCS108,
                navController = navController
            )
        }
    ) { scrollState ->
        sensorPopup?.let { sensor ->
            SensorPopUp(
                sensor = sensor,
                scanViewModel = scanViewModel,
                onSubmit = { note ->
                    if (scanViewModel.updateSensorNote(sensorPopup!!, note)) sensorPopup = null
                },
                onCancel = { sensorPopup = null }
            )
        }

        if (showScanNotePopup) {
            ScanNotePopUp(
                scanViewModel = scanViewModel,
                onSubmit = { showScanNotePopup = false },
                onCancel = { showScanNotePopup = false }
            )
        }

        ScanWeather(scanViewModel = scanViewModel, scrollState)
        PlansView(scanViewModel) { sensorPopup = it }
        SensorsList(scanViewModel, context) { sensorPopup = it }

        scanViewModel.sensorMessages.observeAsState(null).value?.let {
            showToast(it)
            scanViewModel.sensorMessages.value = null
        }

        scanViewModel.alertMessages.observeAsState(null).value?.let {
            scanViewModel.alertMessages.value = null
            navController.navigate("Alerte?state=${it.state}&sensorId=${it.sensorId}")
        }
   }
}


@Composable
private fun SensorPopUp(
    sensor: SensorDB,
    scanViewModel: ScanViewModel,
    onSubmit: (String) -> Unit,
    onCancel: () -> Unit
) {
    var note by remember { mutableStateOf(sensor.note.orEmpty()) }
    var scanStarted = scanViewModel.isScanStarted()

    val currentStateDisplay = getStateDisplayName(
        scanViewModel.sensorsScanned.observeAsState(initial = emptyList()).value
            .find { it.id == sensor.sensorId }?.state ?: sensor.state
    )

    val lastStateDisplay = getStateDisplayName(scanViewModel.getPreviousState(sensor.sensorId))

    PopUp(onCancel, {
        Title(sensor.name, false) {
            Button(
                R.drawable.check,
                "valider",
                Black,
                LightGray
            ) { if (scanStarted) onSubmit(note) else onCancel() }
        }
    }) {
        PlanForSensor(scanViewModel.planViewModel, sensor, Black)

        SensorDetails(
            Black,
            "État courant:",
            currentStateDisplay,
            "Dernier état:",
            lastStateDisplay
        )

        InputTextArea(
            label = "Note",
            value = note,
            placeholder = "Aucune note pour le moment",
            enabled = scanStarted
        ) { s -> note = s.take(1000) }
    }
}


@Composable
private fun ScanNotePopUp(
    scanViewModel: ScanViewModel,
    onSubmit: () -> Unit,
    onCancel: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val errorMessage by scanViewModel.noteErrorMessage.observeAsState()
    var scanNote by remember { mutableStateOf("") }
    val scanState by scanViewModel.currentScanState.observeAsState()

    LaunchedEffect(Unit) {
        scanViewModel.activeScanId?.let { scanId ->
            scanNote = db.scanDao().getNote(scanId) ?: ""
        }
    }

    PopUp(onCancel, {
        Title("Note du scan", false) {
            if (scanState != ScanState.NOT_STARTED) {
                Button(R.drawable.check, "valider", Black, LightGray) {
                    coroutineScope.launch {
                        if (scanViewModel.updateScanNote(scanNote)) onSubmit()
                    }
                }
            } else {
                Button(R.drawable.x, "fermer", Black, LightGray) {
                    onCancel()
                }
            }
        }
    }) {
        Column(
            verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.Start,
        ) {
            InputTextArea(
                label = "Note",
                value = scanNote,
                placeholder = "Aucune note pour le moment",
                enabled = scanState != ScanState.NOT_STARTED
            ) { s -> if (s.length <= 1000) scanNote = s }
        }


        errorMessage?.let {
            Text(
                text = it,
                color = Red,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

    }
}