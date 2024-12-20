package fr.uge.structsure.startScan.domain

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.uge.structsure.bluetooth.cs108.RfidChip
import fr.uge.structsure.retrofit.RetrofitInstance
import fr.uge.structsure.retrofit.response.GetAllSensorsResponse
import fr.uge.structsure.startScan.data.ResultEntity
import fr.uge.structsure.startScan.data.ResultSensors
import fr.uge.structsure.startScan.data.ScanEntity
import fr.uge.structsure.startScan.data.dao.ResultDao
import fr.uge.structsure.startScan.data.dao.ScanDao
import fr.uge.structsure.structuresPage.data.SensorDB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.sql.Timestamp

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
class ScanViewModel(private val scanDao: ScanDao, private val resultDao: ResultDao, private val structureId: Long) : ViewModel() {

    // state for the current scan
    val currentScanState = mutableStateOf(ScanState.NOT_STARTED)
    // ID for the active scan
    var activeScanId: Long? = null
        private set

    // control variable for the scan
    private var continueScanning = true

    // last processed sensor index
    private var lastProcessedSensorIndex = 0

    private val _sensorMessages = MutableLiveData<String>()
    val sensorMessages: LiveData<String> = _sensorMessages

    fun addSensorMessage(message: String) {
        _sensorMessages.postValue(message)
    }


    /**
     * Fetches the sensors for the given structure and starts the scan.
     * @param structureId ID of the structure.
     */
    fun fetchSensorsAndStartScan(structureId: Long){
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
           //startSensorInterrogation(sensorEntities, RfidChip("test"))
        }
    }

    fun startScan(){
        viewModelScope.launch {
            val startTimestamp = Timestamp(System.currentTimeMillis()).toString()
            var resultSensors: List<ResultSensors> = mutableListOf()

            val newScan = ScanEntity(
                start_timestamp = startTimestamp,
                end_timestamp = startTimestamp,
                technician_id = 1,
                structureId = structureId
            )
            scanDao.insertScan(newScan)
        }

    }

    /**
     * Starts the interrogation of the sensors.
     * @param sensors List of sensors to interrogate.
     */

    suspend fun startSensorInterrogation(rfidChip: RfidChip) {
        val sensors = scanDao.getAllSensors(structureId)

        continueScanning = true // Controle variable pour le scan
        println("sensors -----------" + sensors.size)
        viewModelScope.launch(Dispatchers.IO) {
            for (i in lastProcessedSensorIndex until sensors.size) {
                if (!continueScanning) {
                    lastProcessedSensorIndex = i // Saving the last processed sensor index
                    return@launch
                }

                val sensor = sensors[i]

                // Randomly change the state of the sensor
                val newState = when ((1..3).random()) {
                    1 -> "OK"
                    2 -> "DEFECTIVE"
                    else -> "NOK"
                }
                // Insert ResultEntity
                val resultSensor = ResultSensors(
                    timestamp = Timestamp(System.currentTimeMillis()).toString(),
                    state = newState,
                    scanId = scanDao.getScanByStructureId(sensor.structureId),
                    controlChip = sensor.controlChip,
                    measureChip = sensor.measureChip
                )

                //resultSensors = resultSensors + resultSensor
                resultDao.insertResult(resultSensor)

                // LOG for the number of sensors inserted
                println("result Dao : ${resultSensor.toString()}")

                // Update the state of the sensor in the database
                // scanDao.updateSensorState(sensor.controlChip, sensor.measureChip, newState)
            }
        }
    }


    /*

    fun startSensorInterrogation(sensors: List<SensorDB>, RFID: RfidChip) {
        if (sensors.isEmpty()) {
            println("No sensors to interrogate.")
            return
        }

        continueScanning = true // Control variable for the scan
        val startTimestamp = Timestamp(System.currentTimeMillis()).toString()

        viewModelScope.launch(Dispatchers.IO) {
            // Insert ScanEntity once before the loop
            val newScan = ScanEntity(
                start_timestamp = startTimestamp,
                end_timestamp = startTimestamp,
                technician_id = 1,
                structureId = sensors[0].structureId
            )
            val scanId = scanDao.insertScan(newScan)

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

                // Insert ResultEntity
                val resultSensor = ResultSensors(
                    timestamp = Timestamp(System.currentTimeMillis()).toString(),
                    state = newState,
                    scanId = scanId,
                    controlChip = sensor.controlChip,
                    measureChip = sensor.measureChip
                )

                resultDao.insertResult(resultSensor)

                // LOG for the number of sensors inserted
                println("result Dao : ${resultSensor.toString()}")

                // Update the state of the sensor in the database
                scanDao.updateSensorState(sensor.controlChip, sensor.measureChip, newState)
            }
        }
    }

     */

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
        activeScanId = null
        lastProcessedSensorIndex = 0
    }


}
