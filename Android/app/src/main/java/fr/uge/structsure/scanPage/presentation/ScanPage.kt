package fr.uge.structsure.scanPage.presentation

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fr.uge.structsure.R
import fr.uge.structsure.bluetooth.cs108.Cs108Connector
import fr.uge.structsure.components.Button
import fr.uge.structsure.components.InputTextArea
import fr.uge.structsure.components.Page
import fr.uge.structsure.components.PopUp
import fr.uge.structsure.components.SensorDetails
import fr.uge.structsure.components.Title
import fr.uge.structsure.scanPage.domain.ScanState
import fr.uge.structsure.scanPage.domain.ScanViewModel
import fr.uge.structsure.scanPage.presentation.components.ScanWeather
import fr.uge.structsure.scanPage.presentation.components.SensorsList
import fr.uge.structsure.structuresPage.data.SensorDB
import fr.uge.structsure.ui.theme.Black
import fr.uge.structsure.ui.theme.LightGray

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

    scanViewModel.setStructure(structureId)

    var sensorPopup by remember { mutableStateOf<SensorDB?>(null) } // Control the popup visibility and hold popup data

    Page(
        Modifier.padding(bottom = 100.dp),
        navController = navController,
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
                    navController.popBackStack()
                },
                onContentClick = {

                },
                connexionCS108 = connexionCS108,
                navController = navController
            )
        }
    ) { scrollState ->
        if (sensorPopup != null) SensorPopUp({ sensorPopup = null }, { sensorPopup = null })
        ScanWeather(viewModel = scanViewModel, scrollState)
        PlansView(structureId)
        SensorsList(scanViewModel) { s -> sensorPopup = s }

        scanViewModel.sensorMessages.observeAsState(null).value?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            scanViewModel.sensorMessages.value = null
        }

        scanViewModel.alertMessages.observeAsState(null).value?.let {
            scanViewModel.alertMessages.value = null
            navController.navigate("Alerte?state=${it.state}&name=${it.sensorName}&lastState=${it.lastStateSensor}")
        }
   }
}

@Composable
private fun SensorPopUp(onSubmit: () -> Unit, onCancel: () -> Unit) {
    var note by remember { mutableStateOf("") }

    PopUp(onCancel) {
        Title("Capteur 04", false) {
            Button(R.drawable.check, "valider", MaterialTheme.colorScheme.onSurface, MaterialTheme.colorScheme.surface, onSubmit)
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.Start,
        ) {
            Text(
                text = "OA Zone 04",
                style = MaterialTheme.typography.headlineMedium
            )
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(156.dp)
                    .clip(shape = RoundedCornerShape(size = 15.dp))
                    .border(width = 3.dp, color = LightGray, shape = RoundedCornerShape(size = 15.dp)),
                painter = painterResource(id = R.drawable.plan_glaciere),
                contentDescription = "Plan",

            )
        }
        SensorDetails(Black, "Etat courant:", "Non scanné", "Dernier état:", "OK")

        InputTextArea(
            label = "Note",
            value = note,
            placeholder = "Aucune note pour le moment"
        ) { s -> if (s.length <= 1000) note = s }
    }
}