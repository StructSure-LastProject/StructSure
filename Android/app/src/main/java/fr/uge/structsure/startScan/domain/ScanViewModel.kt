package fr.uge.structsure.startScan.domain

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.uge.structsure.cache.SensorCache
import fr.uge.structsure.startScan.data.ResultSensors
import fr.uge.structsure.startScan.data.ScanEntity
import fr.uge.structsure.startScan.data.dao.ResultDao
import fr.uge.structsure.startScan.data.dao.ScanDao
import fr.uge.structsure.structuresPage.data.SensorDB
import fr.uge.structsure.structuresPage.data.SensorDao
import kotlinx.coroutines.*
import java.sql.Timestamp

/**
 * ViewModel that manages the scan process (start, pause, resume, stop).
 * It stores sensors in a local cache and inserts ResultSensors into the database.
 */
class ScanViewModel(
    private val scanDao: ScanDao,
    private val resultDao: ResultDao,
    private val sensorDao: SensorDao,
    private val structureId: Long
) : ViewModel() {

    /**
     * Represents the current state of the scan:
     * NOT_STARTED, STARTED, PAUSED, or STOPPED.
     */
    val currentScanState = mutableStateOf(ScanState.NOT_STARTED)

    /**
     * The ID of the current ScanEntity (created when the user presses "Play").
     * This is used to link ResultSensors to the correct scan.
     */
    var activeScanId: Long? = null
        private set

    /**
     * Local cache of sensors to avoid multiple database fetches.
     */
    private val sensorCache = SensorCache()

    /**
     * Keeps track of the last sensor index already processed.
     * This allows the scan to resume from the same position if needed.
     */
    private var lastProcessedSensorIndex = 0

    /**
     * The Job associated with the insertion coroutine.
     * If canceled, the loop stops immediately.
     */
    private var scanJob: Job? = null

    /**
     * A LiveData for any messages (e.g., toast messages) that can be observed by the UI.
     */
    private val _sensorMessages = MutableLiveData<String>()
    val sensorMessages: LiveData<String> = _sensorMessages

    /**
     * Buffer that stores RFID chip IDs for a certain amount of time.
     * When a chip is added, it waits for the other chip to be read.
     * If the other chip is not read within the timeout, the buffer is flushed
     * and the state of the sensor is determined.
     */
    private val rfidBuffer = TimedBuffer<String> { buffer, chipId ->
        handleTimedOutChip(chipId)
    }

    /**
     * Function called each time an RFID chip is read.
     * We add it to the buffer to “wait” for the other chip
     * from the same sensor can be read.
     */
    fun onTagScanned(chipId: String)  {
        Log.d("TestScan", "Adding chip $chipId to buffer")
        rfidBuffer.add(chipId)
    }

    /**
     * Callback executed when chipId has remained in the buffer
     * longer than timeout (in TimedBuffer).
     * Here, we decide how to determine the state of a sensor.
     */
    private fun handleTimedOutChip(chipId: String) {
        try {
            val sensor = findSensorByChip(chipId)
            if (sensor == null) {
                Log.d("TestScan", "No sensor found for chipId $chipId")
                return
            }
            val otherChipId = if (sensor.controlChip == chipId) sensor.measureChip else sensor.controlChip
            val otherPresent = rfidBuffer.contains(otherChipId)
            val newState = computeSensorState(sensor, chipId, otherPresent)
            Log.d("TestScan", "Chip $chipId timed out. Other chip presence: $otherPresent. New state computed: $newState")
            updateSensorState(sensor, newState)
        } catch (e: Exception) {
            Log.e("TestScan", "Error in handleTimedOutChip: ${e.message}")
        }
    }

    /**
     * Retrieves a cached SensorDB from a chipId (controlChip or measureChip).
     */
    private fun findSensorByChip(chipId: String): SensorDB? {
        return sensorCache.getAllSensors().find { s ->
            s.controlChip == chipId || s.measureChip == chipId
        }
    }

    /**
     * Rules :
     *  - OK: if we only have the control chip => controlChip
     *  - NOK : if we have the control chip AND the scrambled chip
     *  - DEFECTIVE: if only the chip is scrambled
     */
    private fun computeSensorState(sensor: SensorDB, scannedChip: String, otherChipInBuffer: Boolean): String {
        val isControl = (scannedChip == sensor.controlChip)
        val isMeasure = (scannedChip == sensor.measureChip)

        return when {
            (isControl && otherChipInBuffer) -> "NOK"

            (isMeasure && otherChipInBuffer) -> "NOK"

            isControl && !otherChipInBuffer -> "OK"

            isMeasure && !otherChipInBuffer -> "DEFECTIVE"

            else -> "UNKNOWN"
        }
    }

    /**
     * Save sensor status by updating SensorDB.state and inserting a ResultSensors entry.
     */
    private fun updateSensorState(sensor: SensorDB, newState: String) {
        viewModelScope.launch(Dispatchers.IO) {
            // 1) Update the 'state' field in the sensors table
            sensorDao.updateSensorDBState(sensor.controlChip, sensor.measureChip, newState)
            Log.d("TestScan", "Sensor ${sensor.sensorId} updated to state: $newState")

            // 2) Insert a new record into ResultSensors
            activeScanId?.let { scanId ->
                val resultSensor = ResultSensors(
                    timestamp = Timestamp(System.currentTimeMillis()).toString(),
                    scanId = scanId,
                    controlChip = sensor.controlChip,
                    measureChip = sensor.measureChip,
                    state = newState
                )
                resultDao.insertResult(resultSensor)
            }
            if (newState == "OK") {
                _sensorMessages.postValue("Message posted: Sensor ${sensor.sensorId} => $newState")
            }
        }
    }



    /**
     * Fetches sensors for the given structure from the database and stores them in sensorCache.
     */
    fun fetchSensors(structureId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val sensors = scanDao.getAllSensors(structureId)
            if (sensors.isNotEmpty()) {
                sensorCache.insertSensors(sensors)
                println("Fetched and cached ${sensors.size} sensors for structureId=$structureId")
            } else {
                println("No sensors found for structureId=$structureId")
                sensorCache.clearCache()
            }
        }
    }

    /**
     * Creates a new ScanEntity in the database (triggered by clicking "Play"),
     * retrieves its ID, and starts the loop that inserts ResultSensors.
     */
    fun createNewScan(structureId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            // 1) Insert a new ScanEntity in the database
            val now = Timestamp(System.currentTimeMillis()).toString()
            val newScan = ScanEntity(
                structureId = structureId,
                start_timestamp = now,
                end_timestamp = "",
                technician_id = 123
            )
            val newScanId = scanDao.insertScan(newScan)
            activeScanId = newScanId

            // 2) Reset the last processed index to 0
            lastProcessedSensorIndex = 0

            // 3) Change the current scan state to STARTED
            currentScanState.value = ScanState.STARTED

            // 4) Start the interrogation loop
            startSensorInterrogation()
        }
    }

    /**
     * The loop that inserts ResultSensors into the database for each sensor in the cache.
     * It runs until all sensors are processed or until it's canceled (pause or stop).
     */
    private fun startSensorInterrogation() {
        // If there's already a job running, don't start a new one
        if (scanJob != null) return

        scanJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                val sensors = sensorCache.getAllSensors()
                for (i in lastProcessedSensorIndex until sensors.size) {
                    // Check if the coroutine was canceled
                    ensureActive()

                    val sensor = sensors[i]
                    println("Inserting sensor #$i => ${sensor.controlChip} - ${sensor.measureChip}")

                    // Only insert if we have a valid scan ID
                    activeScanId?.let { sid ->
                        val resultSensor = ResultSensors(
                            timestamp = Timestamp(System.currentTimeMillis()).toString(),
                            scanId = sid,
                            controlChip = sensor.controlChip,
                            measureChip = sensor.measureChip,
                            state = sensor.state ?: "UNKNOWN"
                        )
                        resultDao.insertResult(resultSensor)
                    }

                    lastProcessedSensorIndex = i + 1
                    // Simulate some processing delay
                    delay(100)
                }
            } catch (e: CancellationException) {
                // Caught when scanJob?.cancel() is called
                println("startSensorInterrogation() => coroutine canceled.")
            } finally {
                // This block is executed even if the coroutine was canceled
                println("All sensors processed or job was canceled.")
                scanJob = null
            }
        }
    }

    /**
     * Pauses the scan: cancels the current job so the insertion loop stops immediately.
     */
    fun pauseScan() {
        if (currentScanState.value == ScanState.STARTED) {
            currentScanState.value = ScanState.PAUSED
            // Cancel the current job
            scanJob?.cancel()
            scanJob = null
            rfidBuffer.stop()
        }
    }

    /**
     * Resumes the scan from a paused state,
     * continuing the insertion loop from where it stopped.
     */
    fun resumeScan() {
        if (currentScanState.value == ScanState.PAUSED) {
            currentScanState.value = ScanState.STARTED
            startSensorInterrogation()
        }
    }

    /**
     * Completely stops the scan: cancels the job, updates the end_timestamp
     * in the database, and resets the state to STOPPED.
     */
    fun stopScan() {
        if (currentScanState.value == ScanState.STARTED || currentScanState.value == ScanState.PAUSED) {
            viewModelScope.launch(Dispatchers.IO) {
                // Immediately cancel the loop
                scanJob?.cancel()
                scanJob = null

                // Update end_timestamp in the database
                val now = Timestamp(System.currentTimeMillis()).toString()
                activeScanId?.let { scanId ->
                    scanDao.updateEndTimestamp(scanId, now)
                }
                activeScanId = null

                // Reset the state and index
                currentScanState.value = ScanState.STOPPED
                lastProcessedSensorIndex = 0

                // Clear the sensor cache
                sensorCache.clearCache()

                // Stop the timerBuffer
                rfidBuffer.stop()
            }
        }
    }
}
