package fr.uge.structsure.startScan.domain

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
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
import java.util.concurrent.CancellationException

/**
 * ViewModel that manages the scan process (start, pause, resume, stop).
 * It uses a SensorCache as a proxy for sensor state updates and saves results in the database.
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

    // Current scan state: NOT_STARTED, STARTED, PAUSED, or STOPPED.
    val currentScanState = mutableStateOf(ScanState.NOT_STARTED)

    // ID of the current ScanEntity, set when "Play" is pressed.
    var activeScanId: Long? = null
        private set

    // Local sensor cache (proxy) to store current sensor states.
    private val sensorCache = SensorCache()

    // Index of the last sensor processed in the interrogation loop.
    private var lastProcessedSensorIndex = 0

    // Job for the insertion coroutine.
    private var scanJob: Job? = null

    // LiveData for toast messages (e.g., for OK state).
    private val _sensorMessages = MutableLiveData<String>()
    val sensorMessages: LiveData<String> get() = _sensorMessages

    // LiveData for alert messages (for NOK/DEFECTIVE states).
    val alertMessages = MutableLiveData<AlertInfo?>()

    // Set to track sensors for which an alert has already been triggered.
    private val alertedSensors = mutableSetOf<String>()

    // TimedBuffer storing RFID chip IDs for a fixed timeout.
    private val rfidBuffer = TimedBuffer { _, chipId ->
        handleTimedOutChip(chipId)
    }

    /**
     * Called when an RFID chip is scanned.
     * Adds the chip ID to the buffer only if the scan is active.
     */
    fun onTagScanned(chipId: String) {
        if (currentScanState.value != ScanState.STARTED) {
            Log.d(LOG_TAG, "Scan not active; ignoring chip: $chipId")
            return
        }
        rfidBuffer.add(chipId)
    }

    /**
     * Callback from TimedBuffer when a chip has stayed in the buffer beyond the timeout.
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
     * - If the scanned chip is the controlChip: returns "OK" if the other chip is absent, "NOK" if present.
     * - If the scanned chip is the measureChip: returns "DEFECTIVE" if the other chip is absent, "NOK" if present.
     */
    private fun computeSensorState(sensor: SensorDB, scannedChip: String, otherChipInBuffer: Boolean): String {
        return if (scannedChip == sensor.controlChip) {
            if (otherChipInBuffer) "NOK" else "OK"
        } else if (scannedChip == sensor.measureChip) {
            if (otherChipInBuffer) "NOK" else "DEFECTIVE"
        } else {
            "UNKNOWN"
        }
    }

    /**
     * Updates the sensor state in the cache and database, and inserts a ResultSensors record.
     * If the new state is critical (NOK or DEFECTIVE), the scan is completely stopped and an alert is posted.
     */
    private fun updateSensorState(sensor: SensorDB, newState: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val lastState = sensorCache.updateSensorState(sensor, newState)
            if (lastState == newState) {
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
                    _sensorMessages.postValue("Sensor ${sensor.sensorId} => OK")
                }
                "NOK", "DEFECTIVE" -> {

                    pauseScan()
                    alertMessages.postValue(
                        AlertInfo(
                            newState = newState,
                            sensorName = "Capteur ${sensor.name}",
                            lastStateSensor = lastState ?: "UNKNOWN"
                        )
                    )

                }
            }
        }
    }



    /**
     * Loads sensors for the given structure from the DB and stores them in the cache.
     */
    fun fetchSensors(structureId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val sensors = scanDao.getAllSensors(structureId)
            if (sensors.isNotEmpty()) {
                sensorCache.insertSensors(sensors)
                Log.d(LOG_TAG, "Fetched and cached ${sensors.size} sensors")
            } else {
                Log.d(LOG_TAG, "No sensors found for structureId=$structureId")
                sensorCache.clearCache()
            }
        }
    }

    /**
     * Creates a new ScanEntity (when "Play" is pressed), using the logged-in user's login as technician,
     * then starts the interrogation loop.
     */
    fun createNewScan(structureId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            alertedSensors.clear()
            currentScanState.value = ScanState.STARTED

            val now = Timestamp(System.currentTimeMillis()).toString()
            val newScan = ScanEntity(
                structureId = structureId,
                start_timestamp = now,
                end_timestamp = "",
                technician = accountDao.getLogin(),
                note = ""
            )

            val newScanId = scanDao.insertScan(newScan)
            activeScanId = newScanId

            lastProcessedSensorIndex = 0

            startSensorInterrogation()
        }
    }

    /**
     * Starts the interrogation loop: iterates over cached sensors and inserts a ResultSensors record for each.
     */
    private fun startSensorInterrogation() {
        if (scanJob != null) return
        scanJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                val sensors = sensorCache.getAllSensors()
                for (i in lastProcessedSensorIndex until sensors.size) {
                    ensureActive()
                    val sensor = sensors[i]
                    Log.d(LOG_TAG, "Inserting sensor #$i => ${sensor.controlChip} / ${sensor.measureChip}")
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
     * Pauses the scan: sets the state to PAUSED, cancels the interrogation loop,
     * and stops the TimedBuffer so no new chips are processed.
     */
    fun pauseScan() {
        currentScanState.value = ScanState.PAUSED
        scanJob?.cancel()
        scanJob = null
        rfidBuffer.stop()
    }

    /**
     * Stops the scan completely: cancels the loop, updates the scan end time in the DB,
     * clears caches, and stops the TimedBuffer.
     */
    fun stopScan() {
        viewModelScope.launch(Dispatchers.IO) {
            scanJob?.cancel()
            scanJob = null
            val now = Timestamp(System.currentTimeMillis()).toString()
            activeScanId?.let { scanId ->
                scanDao.updateEndTimestamp(scanId, now)
            }
            activeScanId = null
            currentScanState.value = ScanState.STOPPED

            rfidBuffer.stop()
            sensorCache.clearCache()
        }
    }

}
