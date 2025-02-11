package fr.uge.structsure.startScan.domain

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.uge.structsure.connexionPage.data.AccountDao
import fr.uge.structsure.startScan.data.ResultSensors
import fr.uge.structsure.startScan.data.ScanEntity
import fr.uge.structsure.startScan.data.dao.ResultDao
import fr.uge.structsure.startScan.data.dao.ScanDao
import fr.uge.structsure.structuresPage.data.SensorCache
import fr.uge.structsure.structuresPage.data.SensorDB
import fr.uge.structsure.structuresPage.data.SensorDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import java.sql.Timestamp
import kotlin.coroutines.cancellation.CancellationException

/**
 * ViewModel that manages the scan process.
 */
class ScanViewModel(
    private val scanDao: ScanDao,
    private val resultDao: ResultDao,
    private val sensorDao: SensorDao,
    private val accountDao: AccountDao,
    private val structureId: Long,
    private val connexionCS108: fr.uge.structsure.bluetooth.cs108.Cs108Connector
) : ViewModel() {

    companion object {
        private const val LOG_TAG = "ScanViewModel"
    }

    // Current scan state: NOT_STARTED, STARTED, PAUSED, STOPPED
    val currentScanState = mutableStateOf(ScanState.NOT_STARTED)

    // ID of the current ScanEntity
    var activeScanId: Long? = null
        private set

    // Sensor cache (proxy)
    private val sensorCache = SensorCache()

    // Job for the sensor interrogation loop
    private var scanJob: Job? = null

    // LiveData for OK state messages (toasts)
    val sensorMessages = MutableLiveData<String>()

    // LiveData for alert events (for NOK/DEFECTIVE)
    val alertMessages = MutableLiveData<AlertInfo?>()

    // Index of the last processed sensor in the cache
    private var lastProcessedSensorIndex = 0

    // TimedBuffer for RFID chip IDs
    private val rfidBuffer = TimedBuffer<String> { _, chipId ->
        handleTimedOutChip(chipId)
    }

    /**
     * Called when an RFID chip is scanned.
     */
    fun onTagScanned(chipId: String) {
        rfidBuffer.add(chipId)
    }

    /**
     * Callback from TimedBuffer when a chip times out.
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
     * Determines the sensor state based on the scanned chip.
     */
    private fun computeSensorState(sensor: SensorDB, scannedChip: String, otherChipInBuffer: Boolean): String {
        return if (scannedChip == sensor.controlChip) {
            if (otherChipInBuffer) "OK" else "NOK"
        } else {
            if (otherChipInBuffer) "OK" else "DEFECTIVE"
        }
    }

    /**
     * Updates the sensor state in the cache and DB, and triggers an alert if needed.
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
     * Loads sensors from the DB and inserts them in the cache.
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
     * Creates a new scan.
     */
    fun createNewScan(structureId: Long) {
        viewModelScope.launch(Dispatchers.IO) {

            currentScanState.value = ScanState.STARTED
            val now = Timestamp(System.currentTimeMillis()).toString()
            val newScan = ScanEntity(
                structureId = structureId,
                start_timestamp = now,
                end_timestamp = "",
                technician = accountDao.getLogin(),
                note = ""
            )
            activeScanId = scanDao.insertScan(newScan)
            lastProcessedSensorIndex = 0
            startSensorInterrogation()
        }
    }

    /**
     * Starts the interrogation loop over the cached sensors.
     */
    private fun startSensorInterrogation() {
        if (scanJob != null) return
        scanJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                val sensors = sensorCache.getAllSensors()
                for (i in lastProcessedSensorIndex until sensors.size) {
                    ensureActive()
                    val sensor = sensors[i]
                    Log.d(LOG_TAG, "Processing sensor ${sensor.sensorId}")
                    activeScanId?.let { sid ->
                        val resultSensor = ResultSensors(
                            timestamp = Timestamp(System.currentTimeMillis()).toString(),
                            scanId = sid,
                            controlChip = sensor.controlChip,
                            measureChip = sensor.measureChip,
                            state = sensor.state
                        )
                        resultDao.insertResult(resultSensor)
                    }
                    lastProcessedSensorIndex = i + 1
                }
            } catch (e: CancellationException) {
                Log.e(LOG_TAG, "startSensorInterrogation() canceled.", e)
                throw e
            } finally {
                Log.d(LOG_TAG, "Interrogation loop finished or canceled.")
                scanJob = null
            }
        }
    }

    /**
     * Pauses the scan: stops the scanner and the TimedBuffer.
     */
    fun pauseScan() {
        connexionCS108.stop()
        currentScanState.value = ScanState.PAUSED
        scanJob?.cancel()
        scanJob = null
        rfidBuffer.stop()
    }

    /**
     * Stops the scan completely: cancels all tasks, updates end time, stops scanner, and clears cache.
     */
    fun stopScan() {
        viewModelScope.launch(Dispatchers.IO) {
            connexionCS108.stop()
            currentScanState.value = ScanState.STOPPED
            scanJob?.cancel()
            scanJob = null
            val now = Timestamp(System.currentTimeMillis()).toString()
            activeScanId?.let { scanId ->
                scanDao.updateEndTimestamp(scanId, now)
            }
            activeScanId = null
            rfidBuffer.stop()
            sensorCache.clearCache()
        }
    }
}
