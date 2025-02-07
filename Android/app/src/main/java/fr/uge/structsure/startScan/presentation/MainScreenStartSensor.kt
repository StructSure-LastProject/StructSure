package fr.uge.structsure.startScan.presentation

import android.content.Context
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
import fr.uge.structsure.bluetooth.cs108.RfidChip
import fr.uge.structsure.components.Page
import fr.uge.structsure.retrofit.response.GetAllSensorsResponse
import fr.uge.structsure.startScan.domain.ScanState
import fr.uge.structsure.startScan.domain.ScanViewModel
import fr.uge.structsure.startScan.presentation.components.CustomToast
import fr.uge.structsure.startScan.presentation.components.SensorsList
import fr.uge.structsure.startScan.presentation.components.StructureWeather
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
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
@OptIn(DelicateCoroutinesApi::class)
@Composable
fun MainScreenStartSensor(context: Context, scanViewModel: ScanViewModel, structureId: Long, connexionCS108: Cs108Connector, navController: NavController) {

    val sensors = listOf<GetAllSensorsResponse>()

    val cs108Scanner = remember {
        Cs108Scanner { chip: RfidChip ->
        println("RFID détecté = ${chip.id}, atten=${chip.attenuation}")
            GlobalScope.launch(Dispatchers.Main) {
                Toast.makeText(context, "Capteur ${chip.id} is oK ! ", Toast.LENGTH_SHORT).show()
            }
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
                    cs108Scanner.stop()
                    scanViewModel.pauseScan()
                },
                onStopClick = {
                    cs108Scanner.stop()
                    scanViewModel.stopScan()
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