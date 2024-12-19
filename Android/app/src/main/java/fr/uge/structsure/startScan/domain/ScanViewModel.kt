package fr.uge.structsure.startScan.domain

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.uge.structsure.retrofit.RetrofitInstance
import fr.uge.structsure.retrofit.response.GetAllSensorsResponse
import fr.uge.structsure.startScan.data.ScanEntity
import fr.uge.structsure.startScan.data.dao.ScanDao
import fr.uge.structsure.structuresPage.data.SensorDB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * États possibles pour le déroulement du scan.
 */
enum class ScanState {
    NOT_STARTED, // Aucun scan n'est lancé
    STARTED,     // Scan en cours
    PAUSED,      // Scan en pause
    STOPPED      // Scan arrêté
}

/**
 * ViewModel for the ScanFragment.
 * @param scanDao Data access object for the ScanEntity and SensorEntity classes.
 */
class ScanViewModel(private val scanDao: ScanDao) : ViewModel() {

    // state for the current scan
    val currentScanState = mutableStateOf(ScanState.NOT_STARTED)

    // ID for the active scan
    var activeScanId: Long? = null
        private set

    // Messages for the sensors
    val sensorMessages = mutableStateListOf<String>()

    // control variable for the scan
    private var continueScanning = true

    // last processed sensor index
    private var lastProcessedSensorIndex = 0

    /**
     * Fetches the sensors for the given structure and starts the scan.
     * @param structureId ID of the structure.
     */
    fun fetchSensorsAndStartScan(structureId: Long) {
        viewModelScope.launch {
            scanDao.getAllSensors(structureId)
        }

    }


    /**
     * Inserts the sensors into the database and starts the scan.
     * @param sensors List of sensors to insert.
     */
    private fun insertSensorsAndStartScan(sensors: List<GetAllSensorsResponse>) {
        viewModelScope.launch(Dispatchers.IO) {
            val newScan = ScanEntity(structureId = 1, date = System.currentTimeMillis())

            // filter out duplicate sensors
            val uniqueSensors = sensors.distinctBy { it.controlChip to it.measureChip }

            val sensorEntities = uniqueSensors.map { sensor ->
              SensorDB(
                  sensorId = "${sensor.controlChip}-${sensor.measureChip}",
                  controlChip = sensor.controlChip,
                  measureChip = sensor.measureChip,
                  name = sensor.name,
                  note = sensor.note,
                  installationDate = sensor.installationDate,
                  state = "NOK",
                  x = sensor.x,
                  y = sensor.y,
                  structureId = 1
              )
            }

            scanDao.insertSensors(sensorEntities)
            currentScanState.value = ScanState.STARTED

            // LOG for the number of sensors inserted
            println("Capteurs insérés : ${sensorEntities.size}")

            // start the sensor interrogation
            startSensorInterrogation(sensorEntities)
        }
    }

    /**
     * Starts the interrogation of the sensors.
     * @param sensors List of sensors to interrogate.
     */
    private fun startSensorInterrogation(sensors: List<SensorDB>) {
        continueScanning = true // Controle variable pour le scan
        viewModelScope.launch(Dispatchers.IO) {
            for (i in lastProcessedSensorIndex until sensors.size) {
                if (!continueScanning) {
                    lastProcessedSensorIndex = i // Saving the last processed sensor index
                    return@launch
                }

                val sensor = sensors[i]
                delay(2000) // Simulate the time taken to interrogate a sensor

                // Randomly change the state of the sensor
                val newState = when ((1..3).random()) {
                    1 -> "OK"
                    2 -> "DEFECTIVE"
                    else -> "NOK"
                }

                // Update the state of the sensor in the database
                scanDao.updateSensorState(sensor.controlChip, sensor.measureChip, newState)

                // Add a message to the sensorMessages list
                if (newState == "OK") {
                    viewModelScope.launch(Dispatchers.Main) {
                        sensorMessages.add("Capteur ${sensor.name} is OK!")
                    }
                }
            }
        }
    }

    /**
     * Met le scan en pause.
     */
    fun pauseScan() {
        currentScanState.value = ScanState.PAUSED
        continueScanning = false
    }

    /**
     * Arrête définitivement le scan et réinitialise les données.
     */
    fun stopScan() {
        currentScanState.value = ScanState.STOPPED
        continueScanning = false
        sensorMessages.clear()
        activeScanId = null
        lastProcessedSensorIndex = 0
    }
    

}
