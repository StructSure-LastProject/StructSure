package fr.uge.structsure.startScan.domain

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.uge.structsure.bluetooth.cs108.Cs108Scanner
import fr.uge.structsure.connexionPage.data.AccountDao
import fr.uge.structsure.startScan.data.ResultSensors
import fr.uge.structsure.startScan.data.ScanEntity
import fr.uge.structsure.startScan.data.dao.ResultDao
import fr.uge.structsure.startScan.data.dao.ScanDao
import fr.uge.structsure.startScan.data.cache.SensorCache
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

    // Scanner hardware for reading RFID chips
    val cs108Scanner =
        Cs108Scanner { chip ->
            onTagScanned(chip.id)
        }

    // Current state of the scan process: NOT_STARTED, STARTED, PAUSED, STOPPED
    val currentScanState = MutableLiveData(ScanState.NOT_STARTED)

    // ID of the currently active scan
    var activeScanId: Long? = null
        private set

    // Sensor cache for managing sensor states in memory
    private val sensorCache = SensorCache()

    // LiveData for displaying sensor messages which have a "OK" state
    val sensorMessages = MutableLiveData<String>()

    // LiveData for displaying sensor messages which have a "NOK" / "DEFECTIVE" state
    val alertMessages = MutableLiveData<AlertInfo?>()

    // Buffer to manage RFID chip scanning with timeout handling
    private val rfidBuffer = TimedBuffer { _, chipId ->
        processChip(chipId)
    }

    /**
     * Adds a scanned RFID chip ID to the buffer for processing.
     *
     * @param chipId ID of the scanned RFID chip.
     */
    private fun onTagScanned(chipId: String) {
        if(chipId == "") return
        rfidBuffer.add(chipId)
    }

    /**
     * Processes a timed-out RFID chip, updating the state of the corresponding sensor.
     *
     * @param chipId ID of the timed-out RFID chip.
     */
    private fun processChip(chipId: String) {
        val sensor = sensorCache.findSensor(chipId) ?: return
        val otherChipId = if (chipId == sensor.controlChip) sensor.measureChip else sensor.controlChip
        val otherPresent = rfidBuffer.contains(otherChipId)
        val newState = computeSensorState(sensor, chipId, otherPresent)
        updateSensorState(sensor, newState)
    }

    /**
     * Computes the new state of a sensor based on the scanned chip and the presence of the other chip.
     *
     * @param sensor The sensor being processed.
     * @param scannedChip The chip that was scanned.
     * @param otherChipInBuffer Whether the other chip is present in the buffer.
     * @return The new state of the sensor.
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
     *
     * @param sensor The sensor being updated.
     * @param newState The new state of the sensor.
     */
    private fun updateSensorState(sensor: SensorDB, newState: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val stateChanged = sensorCache.updateSensorState(sensor, newState) ?: return@launch

            activeScanId?.let { scanId ->
                resultDao.insertResult(
                    ResultSensors(
                        id = sensor.sensorId,
                        timestamp = Timestamp(System.currentTimeMillis()).toString(),
                        scanId = scanId,
                        controlChip = sensor.controlChip,
                        measureChip = sensor.measureChip,
                        state = stateChanged
                    )
                )
            }

            when (stateChanged) {
                "OK" -> {
                    sensorMessages.postValue("Sensor ${sensor.name} is OK")
                }
                "NOK" -> {
                    pauseScan()
                    alertMessages.postValue(
                        AlertInfo(
                            state = true,
                            sensorName = "Capteur ${sensor.name}",
                            lastStateSensor = sensor.state
                        )
                    )
                }
                "DEFECTIVE" -> {
                    pauseScan()
                    alertMessages.postValue(
                        AlertInfo(
                            state = false,
                            sensorName = "Capteur ${sensor.name}",
                            lastStateSensor = sensor.state
                        )
                    )
                }
            }
        }
    }

    /**
     * Initializes the sensor cache with the sensors of the structure being scanned.
     *
     */
    fun init() {
        viewModelScope.launch(Dispatchers.IO) {
            val sensors = scanDao.getAllSensors(structureId)
            sensorCache.insertSensors(sensors)
        }
    }

    /**
     * Creates a new scan for the given structure.
     *
     * @param structureId ID of the structure to scan.
     */
    fun createNewScan(structureId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            cs108Scanner.start()
            currentScanState.postValue(ScanState.STARTED)
            alertMessages.postValue(null)
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
     * Pauses the scan process, stopping the scanner and the RFID buffer.
     */
    fun pauseScan() {
        cs108Scanner.stop()
        currentScanState.postValue(ScanState.PAUSED)
        rfidBuffer.stop()
    }

    /**
     * Stops the scan process, stopping the scanner and the RFID buffer.
     */
    fun stopScan() {
        viewModelScope.launch(Dispatchers.IO) {
            cs108Scanner.stop()
            currentScanState.postValue(ScanState.STOPPED)
            val now = Timestamp(System.currentTimeMillis()).toString()
            activeScanId?.let { scanId ->
                scanDao.updateEndTimestamp(scanId, now)
            }
            rfidBuffer.stop()
            sensorCache.clearCache()
        }
    }
}
