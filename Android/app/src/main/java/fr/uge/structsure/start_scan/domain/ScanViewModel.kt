package fr.uge.structsure.start_scan.domain

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.uge.structsure.retrofit.RetrofitInstance
import fr.uge.structsure.retrofit.response.GetAllSensorsResponse
import fr.uge.structsure.start_scan.data.ScanEntity
import fr.uge.structsure.start_scan.data.SensorEntity
import fr.uge.structsure.start_scan.data.dao.ScanDao
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
 * @param context Application context.
 */
class ScanViewModel(private val scanDao: ScanDao, private val context: Context) : ViewModel() {

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
        RetrofitInstance.sensorApi.getAllSensors(structureId).enqueue(object :
            Callback<List<GetAllSensorsResponse>> {
            override fun onResponse(
                call: Call<List<GetAllSensorsResponse>>,
                response: Response<List<GetAllSensorsResponse>>
            ) {
                if (response.isSuccessful) {
                    val sensors = response.body()
                    sensors?.let { sensorList ->
                        insertSensorsAndStartScan(sensorList)
                    }
                }
            }

            override fun onFailure(call: Call<List<GetAllSensorsResponse>>, t: Throwable) {
                println(" failure  : ${t.localizedMessage}")
            }
        })
    }


    /**
     * Inserts the sensors into the database and starts the scan.
     * @param sensors List of sensors to insert.
     */
    private fun insertSensorsAndStartScan(sensors: List<GetAllSensorsResponse>) {
        viewModelScope.launch(Dispatchers.IO) {
            val newScan = ScanEntity(structureId = 1, date = System.currentTimeMillis())
            val scanId = scanDao.insertScan(newScan)

            // filter out duplicate sensors
            val uniqueSensors = sensors.distinctBy { it.controlChip to it.measureChip }

            val sensorEntities = uniqueSensors.map { sensor ->
                SensorEntity(
                    controlChip = sensor.controlChip,
                    measureChip = sensor.measureChip,
                    name = sensor.name,
                    note = sensor.note,
                    state = "UNSCAN",
                    installationDate = sensor.installationDate,
                    x = sensor.x,
                    y = sensor.y
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
    private fun startSensorInterrogation(sensors: List<SensorEntity>) {
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
     * Reprend le scan après une pause.
     */
    fun resumeScan(sensors: List<SensorEntity>) {
        currentScanState.value = ScanState.STARTED
        startSensorInterrogation(sensors)
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
