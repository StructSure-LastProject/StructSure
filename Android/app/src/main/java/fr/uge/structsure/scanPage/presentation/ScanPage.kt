package fr.uge.structsure.scanPage.presentation

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fr.uge.structsure.MainActivity.Companion.db
import fr.uge.structsure.R
import fr.uge.structsure.bluetooth.cs108.Cs108Connector
import fr.uge.structsure.components.Button
import fr.uge.structsure.components.InputTextArea
import fr.uge.structsure.components.Page
import fr.uge.structsure.components.PopUp
import fr.uge.structsure.components.SensorDetails
import fr.uge.structsure.components.Title
import fr.uge.structsure.navigateNoReturn
import fr.uge.structsure.scanPage.data.findPlanById
import fr.uge.structsure.scanPage.data.getPlanSectionName
import fr.uge.structsure.scanPage.data.ScanEntity
import fr.uge.structsure.scanPage.domain.ScanState
import fr.uge.structsure.scanPage.domain.ScanViewModel
import fr.uge.structsure.scanPage.presentation.components.ScanWeather
import fr.uge.structsure.scanPage.presentation.components.SensorState.Companion.getStateDisplayName
import fr.uge.structsure.scanPage.presentation.components.SensorsList
import fr.uge.structsure.structuresPage.data.SensorDB
import fr.uge.structsure.ui.theme.Black
import fr.uge.structsure.ui.theme.LightGray
import fr.uge.structsure.ui.theme.Typography
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
                    if (scanViewModel.currentScanState.value != ScanState.NOT_STARTED) {
                        showScanNotePopup = true
                    } else {
                        showToast("Veuillez lancer un scan avant d'ajouter une note")
                    }
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
                onSubmit = { sensorPopup = null },
                onCancel = { sensorPopup = null }
            )
        }

        if (showScanNotePopup && scanViewModel.currentScanState.value != ScanState.NOT_STARTED) {
            ScanNotePopUp(
                scanViewModel = scanViewModel,
                onSubmit = { showScanNotePopup = false },
                onCancel = { showScanNotePopup = false }
            )
        }

        ScanWeather(viewModel = scanViewModel, scrollState)
        PlansView(scanViewModel)
        SensorsList(scanViewModel, onClick = { sensorPopup = it }, context)

        scanViewModel.sensorMessages.observeAsState(null).value?.let {
            showToast(it)
            scanViewModel.sensorMessages.value = null
        }

        scanViewModel.alertMessages.observeAsState(null).value?.let {
            scanViewModel.alertMessages.value = null
            navController.navigate("Alerte?state=${it.state}&name=${it.sensorName}&lastState=${it.lastStateSensor}")
        }
   }
}


@Composable
private fun SensorPopUp(
    sensor: SensorDB,
    scanViewModel: ScanViewModel,
    onSubmit: () -> Unit,
    onCancel: () -> Unit
) {
    val current = LocalContext.current

    LaunchedEffect(sensor.plan) {
        scanViewModel.planViewModel.loadPlanForPopup(current, sensor.plan)
    }

    var note by remember { mutableStateOf(sensor.note.orEmpty()) }
    val planImage by scanViewModel.planViewModel.popupImage.observeAsState()

    val currentStateDisplay = getStateDisplayName(
        scanViewModel.sensorsScanned.observeAsState(initial = emptyList()).value
            .find { it.id == sensor.sensorId }?.state ?: sensor.state
    )

    val lastStateDisplay = getStateDisplayName(scanViewModel.getPreviousState(sensor.sensorId))

    PopUp(onCancel) {
        Title(sensor.name, false) {
            Button(
                R.drawable.check,
                "valider",
                MaterialTheme.colorScheme.onSurface,
                MaterialTheme.colorScheme.surface,
                onSubmit
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.Start,
        ) {
            val planNode = sensor.plan?.let { scanViewModel.planViewModel.plans.value?.findPlanById(it) }
            val sectionName = planNode?.let { getPlanSectionName(it) } ?: "Section inconnue"

            Text(
                text = sectionName,
                style = MaterialTheme.typography.headlineMedium
            )

            planImage?.let { bitmap ->
                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(156.dp)
                        .clip(shape = RoundedCornerShape(size = 15.dp))
                        .border(width = 3.dp, color = LightGray, shape = RoundedCornerShape(size = 15.dp)),
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Plan",
                )
            } ?: Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(156.dp)
                    .clip(shape = RoundedCornerShape(size = 15.dp))
                    .background(LightGray),
                contentAlignment = Alignment.Center
            ) {
                Text("Loading...", style = Typography.titleMedium)
            }
        }

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
            placeholder = "Aucune note pour le moment"
        ) { s -> if (s.length <= 1000) note = s }
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

    LaunchedEffect(Unit) {
        scanViewModel.activeScanId?.let { scanId ->
            scanNote = db.scanDao().getNote(scanId) ?: ""
        }
    }

    PopUp(onCancel) {
        Title("Note du scan", false) {
            Button(
                R.drawable.check,
                "valider",
                MaterialTheme.colorScheme.onSurface,
                MaterialTheme.colorScheme.surface,
                onClick =
                {
                    coroutineScope.launch {
                        if (scanViewModel.updateScanNote(scanNote)) {
                            onSubmit()
                        }
                    }
                }
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.Start,
        ) {
            InputTextArea(
                label = "Note",
                value = scanNote,
                placeholder = "Aucune note pour le moment",
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



@Composable
private fun ScanNotePopUp(
    scan: ScanEntity,
    scanViewModel: ScanViewModel,
    onSubmit: () -> Unit,
    onCancel: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val errorMessage by scanViewModel.noteErrorMessage.observeAsState()
    var scanNote by remember { mutableStateOf(scan.note ?: "") }

    PopUp(onCancel) {
        Title("Note du scan", false) {
            Button(
                R.drawable.check,
                "valider",
                MaterialTheme.colorScheme.onSurface,
                MaterialTheme.colorScheme.surface,
                onClick =
                {
                    coroutineScope.launch {
                        if (scanViewModel.updateScanNote(scanNote)) {
                            onSubmit()
                        }
                    }
                }
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.Start,
        ) {
            InputTextArea(
                label = "Note",
                value = scanNote,
                placeholder = "Aucune note pour le moment",
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