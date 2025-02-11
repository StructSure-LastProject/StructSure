package fr.uge.structsure.startScan.domain

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.uge.structsure.bluetooth.cs108.Cs108Scanner
import fr.uge.structsure.connexionPage.data.AccountDao
import fr.uge.structsure.startScan.data.ResultSensors
import fr.uge.structsure.startScan.data.ScanEntity
import fr.uge.structsure.startScan.data.dao.ResultDao
import fr.uge.structsure.startScan.data.dao.ScanDao
import fr.uge.structsure.structuresPage.data.SensorCache
import fr.uge.structsure.structuresPage.data.SensorDB
import fr.uge.structsure.structuresPage.data.SensorDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.sql.Timestamp

/**
 * ViewModel responsible for managing the scanning process.
 * It interacts with the database, sensor cache, and scanner hardware to handle sensor states and user actions.
 *
 * @property scanDao DAO to interact with the scan database.
 * @property resultDao DAO to store scan results.
 * @property sensorDao DAO to interact with sensor data.
 * @property accountDao DAO to fetch user account information.
 * @property structureId ID of the structure being scanned.
 */
class ScanViewModel(
    private val scanDao: ScanDao,
    private val resultDao: ResultDao,
    private val sensorDao: SensorDao,
    private val accountDao: AccountDao,
    private val structureId: Long
) : ViewModel() {

    companion object {
        private const val LOG_TAG = "ScanViewModel"
    }

    val cs108Scanner =
        Cs108Scanner { chip ->
            onTagScanned(chip.id)
        }

    // Current state of the scan process: NOT_STARTED, STARTED, PAUSED, STOPPED
    val currentScanState = mutableStateOf(ScanState.NOT_STARTED)

    // ID of the currently active scan
    var activeScanId: Long? = null
        private set

    // Sensor cache for managing sensor states in memory
    private val sensorCache = SensorCache()

    // LiveData for displaying sensor messages (e.g., OK state)
    val sensorMessages = MutableLiveData<String>()

    // LiveData for displaying sensor messages (e.g., NOK / DEFECTIVE state)
    val alertMessages = MutableLiveData<AlertInfo?>()

    // Buffer to manage RFID chip scanning with timeout handling
    private val rfidBuffer = TimedBuffer<String> { _, chipId ->
        handleTimedOutChip(chipId)
    }

    /**
     * Adds a scanned RFID chip ID to the buffer for processing.
     *
     * @param chipId ID of the scanned RFID chip.
     */
    fun onTagScanned(chipId: String) {
        rfidBuffer.add(chipId)
    }

    /**
     * Handles a chip timeout event from the TimedBuffer.
     * Determines the state of the corresponding sensor and updates its state.
     *
     * @param chipId ID of the timed-out RFID chip.
     */
    private fun handleTimedOutChip(chipId: String) {
        val sensor = sensorCache.findSensor(chipId)
        if (sensor == null) {
            Log.d(LOG_TAG, "No sensor found for chipId=$chipId")
            return
        }
        val otherChipId = if (chipId == sensor.controlChip) sensor.measureChip else sensor.controlChip
        val otherPresent = rfidBuffer.contains(otherChipId)
        val newState = computeSensorState(sensor, chipId, otherPresent)
        Log.d(LOG_TAG, "Chip $chipId timed out. Other chip ($otherChipId) present: $otherPresent => newState=$newState")
        updateSensorState(sensor, newState)
    }

    /**
     * Computes the new state of a sensor based on the scanned chip and its context.
     *
     * @param sensor The sensor being evaluated.
     * @param scannedChip The scanned RFID chip ID.
     * @param otherChipInBuffer Whether the other chip of the sensor is also present.
     * @return The computed state of the sensor: OK, NOK, or DEFECTIVE.
     */
    private fun computeSensorState(sensor: SensorDB, scannedChip: String, otherChipInBuffer: Boolean): String {
        return if (scannedChip == sensor.controlChip) {
            if (otherChipInBuffer) "NOK" else "OK"
        } else {
            if (otherChipInBuffer) "NOK" else "DEFECTIVE"
        }
    }

    /**
     * Updates the state of a sensor in the cache and the database.
     * Triggers alerts or messages if necessary.
     *
     * @param sensor The sensor being updated.
     * @param newState The new state of the sensor.
     */
    private fun updateSensorState(sensor: SensorDB, newState: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val previousState = sensorCache.updateSensorState(sensor, newState)
            if (previousState == newState) {
                Log.d(LOG_TAG, "Sensor ${sensor.sensorId} already in state $newState; skipping update")
                return@launch
            }

            sensorDao.updateSensorDBState(sensor.controlChip, sensor.measureChip, newState)
            Log.d(LOG_TAG, "Sensor ${sensor.sensorId} updated to state=$newState in DB.")

            activeScanId?.let { sid ->
                val resultSensor = ResultSensors(
                    timestamp = Timestamp(System.currentTimeMillis()).toString(),
                    scanId = sid,
                    controlChip = sensor.controlChip,
                    measureChip = sensor.measureChip,
                    state = newState
                )
                resultDao.insertResult(resultSensor)
            }

            when (newState) {
                "OK" -> {
                    sensorMessages.postValue("Sensor ${sensor.sensorId} => OK")
                }
                "NOK", "DEFECTIVE" -> {
                    stopScan()
                    alertMessages.postValue(
                        AlertInfo(
                            newState = newState,
                            sensorName = "Capteur ${sensor.name}",
                            lastStateSensor = sensor.state
                        )
                    )
                }

            }
        }
    }

    /**
     * Loads sensors from the database and stores them in the cache.
     *
     * @param structureId ID of the structure to fetch sensors for.
     */
    fun fetchSensors(structureId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            if (sensorCache.getAllSensors().isNotEmpty()) {
                Log.d(LOG_TAG, "Sensors already loaded, skipping fetch.")
                return@launch
            }

            val sensors = scanDao.getAllSensors(structureId)
            sensorCache.insertSensors(sensors)
            Log.d(LOG_TAG, "Fetched and cached ${sensors.size} sensors")
        }
    }


    /**
     * Starts a new scan process and resets the processed sensor index.
     *
     * @param structureId ID of the structure to scan.
     */
    fun createNewScan(structureId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            currentScanState.value = ScanState.STARTED
            alertMessages.postValue(null)
            cs108Scanner.start()
            val now = Timestamp(System.currentTimeMillis()).toString()
            val newScan = ScanEntity(
                structureId = structureId,
                start_timestamp = now,
                end_timestamp = "",
                technician = accountDao.getLogin(),
                note = ""
            )
            activeScanId = scanDao.insertScan(newScan)
        }
    }


    /**
     * Pauses the scan process, stopping the scanner and clearing the buffer.
     */
    fun pauseScan() {
        cs108Scanner.stop()
        currentScanState.value = ScanState.PAUSED
        rfidBuffer.stop()
    }

    /**
     * Stops the scan process completely and clears the sensor cache.
     */
    fun stopScan() {
        viewModelScope.launch(Dispatchers.IO) {
            cs108Scanner.stop()
            currentScanState.value = ScanState.STOPPED
            val now = Timestamp(System.currentTimeMillis()).toString()
            activeScanId?.let { scanId ->
                scanDao.updateEndTimestamp(scanId, now)
            }
            activeScanId = null
            rfidBuffer.stop()
        }
    }
}
