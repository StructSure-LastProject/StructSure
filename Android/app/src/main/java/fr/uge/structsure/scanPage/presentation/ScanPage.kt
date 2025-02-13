package fr.uge.structsure.scanPage.presentation

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fr.uge.structsure.MainActivity
import fr.uge.structsure.bluetooth.cs108.Cs108Connector
import fr.uge.structsure.components.Page
import fr.uge.structsure.scanPage.data.dao.ResultDao
import fr.uge.structsure.scanPage.domain.ScanState
import fr.uge.structsure.scanPage.domain.ScanViewModel
import fr.uge.structsure.scanPage.presentation.components.SensorState
import fr.uge.structsure.scanPage.presentation.components.SensorsList
import fr.uge.structsure.scanPage.presentation.components.StructureWeather
import fr.uge.structsure.structuresPage.data.Sensor
import fr.uge.structsure.structuresPage.data.SensorDB
import fr.uge.structsure.structuresPage.data.SensorId

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


    val resultsDao = remember { MainActivity.db.resultDao() }
    val sensorDao = remember { MainActivity.db.sensorDao() }
    val sensors = remember { computeSensorStates(resultsDao, scanViewModel.activeScanId?:0, sensorDao.getAllSensors(structureId)) }

    val currentState = scanViewModel.currentScanState.observeAsState(initial = ScanState.NOT_STARTED)
    scanViewModel.setStructure(structureId)

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
        PlansView(sensors)
        SensorsList(sensors)

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

/**
 * Build a list of sensors with their states from the current scan
 * @param resultsDao DB access to the scan results
 * @param scanId ID of the current scan to get results for
 * @param sensors the sensors targeted in the active scan
 * @return the list of sensor with their state
 */
private fun computeSensorStates(resultsDao: ResultDao, scanId: Long, sensors: List<SensorDB>): MutableList<Sensor> {
    val results = resultsDao.getResults(scanId.toInt()).associateBy({ it.id }, { SensorState.from(it.state) })
    return sensors.map { s ->
        val scanState = results[s.sensorId]?:SensorState.UNKNOWN
        Sensor(SensorId(s.controlChip, s.measureChip), s.name, s.note?:"", s.installationDate, s.x, s.y, scanState)
    }.toMutableList()
}