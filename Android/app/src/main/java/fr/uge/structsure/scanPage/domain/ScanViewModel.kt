package fr.uge.structsure.scanPage.domain

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.uge.structsure.MainActivity.Companion.db
import fr.uge.structsure.bluetooth.cs108.Cs108Connector
import fr.uge.structsure.bluetooth.cs108.Cs108Scanner
import fr.uge.structsure.scanPage.data.ResultSensors
import fr.uge.structsure.scanPage.data.ScanEntity
import fr.uge.structsure.scanPage.data.cache.SensorCache
import fr.uge.structsure.scanPage.data.repository.ScanRepository
import fr.uge.structsure.scanPage.presentation.components.SensorState
import fr.uge.structsure.structuresPage.data.SensorDB
import fr.uge.structsure.structuresPage.domain.StructureViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.sql.Timestamp

/**
 * ViewModel responsible for managing the scanning process.
 * It interacts with the database, sensor cache, and scanner hardware to handle sensor states and user actions.
 */
class ScanViewModel(context: Context, private val structureViewModel: StructureViewModel) : ViewModel() {

    /** DAO to interact with the scan database */
    private val scanDao = db.scanDao()

    /** DAO to store scan results */
    private val resultDao = db.resultDao()

    /** DAO to interact with sensor data */
    private val sensorDao = db.sensorDao()

    /** DAO to fetch user account information */
    private val accountDao = db.accountDao()

    /** ID of the structure being scanned */
    private var structureId: Long? = null

    /** Repository to interact with the scan database */
    private val scanRepository: ScanRepository = ScanRepository(context)

    /** Scanner hardware for reading RFID chips */
    private val cs108Scanner =
        Cs108Scanner { chip ->
            onTagScanned(chip.id)
        }

    /** Current state of the scan process: NOT_STARTED, STARTED, PAUSED, STOPPED */
    val currentScanState = MutableLiveData(ScanState.NOT_STARTED)

    /** ID of the currently active scan */
    private var activeScanId: Long? = null

    /** Sensor cache for managing sensor states in memory */
    private val sensorCache = SensorCache()

    /** LiveData for displaying sensor messages which have a "OK" state */
    val sensorMessages = MutableLiveData<String>()

    /** LiveData for displaying sensor messages which have a "NOK" / "DEFECTIVE" state */
    val alertMessages = MutableLiveData<AlertInfo?>()

    /** Buffer to manage RFID chip scanning with timeout handling */
    private val rfidBuffer = TimedBuffer { _, chipId ->
        processChip(chipId)
    }

    val sensorsNotScanned = MutableLiveData<List<SensorDB>>()

    /** Counts how many results are in a given state for the scan weather */
    val sensorStateCounts = MutableLiveData<Map<SensorState, Int>>()

    /** Sub-ViewModel that handle all plan selection/display logic */
    val planViewModel = PlanViewModel(context, this)


    /**
     * Update the state of the sensors dynamically in the header of the scan page.
     */
    private fun updateSensorStateCounts() {
        viewModelScope.launch(Dispatchers.IO) {
            val scannedSensors = resultDao.getAllResults()
            val stateCounts = SensorState.entries.associateWith { state ->
                if (state == SensorState.UNKNOWN) sensorCache.size() - scannedSensors.size
                else scannedSensors.count { it.state == state.name }
            }
            sensorStateCounts.postValue(stateCounts)
        }
    }

    /**
     * Changes the structureId of the scanViewModel. This will reload
     * the sensors if the given id is not the same as the saved one.
     * @param context used to load the image of the plan if changed
     * @param structureId the id of the structure in use
     * @param scanId the id of the uncompleted scan to continue
     */
    fun setStructure(structureId: Long, scanId: Long? = null) {
        if (this.structureId == structureId) return
        this.structureId = structureId
        this.activeScanId = null
        if (structureId == -1L) {
            planViewModel.reset()
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            sensorCache.clearCache()
            val sensors = sensorDao.getAllSensors(structureId)
            sensorsNotScanned.postValue(sensors)
            val stateCounts = SensorState.entries.associateWith { state ->
                if (state == SensorState.UNKNOWN) sensors.size else 0
            }
            sensorStateCounts.postValue(stateCounts)
            sensorCache.insertSensors(sensors)
            planViewModel.loadPlans(context, structureId)
            if (scanId != null) continueExistingScan(scanId)
            else scanDao.getScanByStructure(structureId)?.let { continueExistingScan(it.id) }
        }
    }

    /**
     * Refreshes the states of the sensors after starting a scan.
     */
    private fun refreshSensorStates() {
        viewModelScope.launch(Dispatchers.IO) {
            val sensors = sensorDao.getAllSensors(structureId?: return@launch)
            val scannedResults = resultDao.getAllResults()

            val updatedSensors = sensors.map { sensor ->
                val result = scannedResults.find { it.id == sensor.sensorId }
                sensor.copy(state = result?.state ?: "UNKNOWN")
            }

            sensorsNotScanned.postValue(updatedSensors)
        }
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
        refreshSensorStates()
        updateSensorStateCounts()
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
        val stateChanged = sensorCache.updateSensorState(sensor, newState) ?: return

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

    /**
     * Creates a new scan for the given structure.
     *
     * @param structureId ID of the structure to scan.
     */
    fun createNewScan(structureId: Long) {
        if (!Cs108Connector.isReady) {
            sensorMessages.postValue("Interrogateur non connecté")
            return
        }

        cs108Scanner.start()
        currentScanState.postValue(ScanState.STARTED)
        alertMessages.postValue(null)

        if (activeScanId != null) return // already created

        val now = Timestamp(System.currentTimeMillis()).toString()
        val newScan = ScanEntity(
            structureId = structureId,
            start_timestamp = now,
            end_timestamp = "",
            technician = accountDao.getLogin(),
            note = ""
        )

        activeScanId = scanDao.insertScan(newScan)
        this.structureId = structureId

        refreshSensorStates()
        updateSensorStateCounts()
    }

    /**
     * Same function as [createNewScan] but for the special case of
     * continuing an existing scan (for example after the app crashed)
     * @param scanId the id of the scan to continue
     */
    private fun continueExistingScan(scanId: Long) {
        currentScanState.postValue(ScanState.PAUSED)
        alertMessages.postValue(null)

        if (activeScanId != null) return // already created
        activeScanId = scanId
        this.structureId = scanDao.getScanById(scanId).structureId
        refreshSensorStates()
        updateSensorStateCounts()
        resultDao.getResultsByScan(scanId).forEach {
            sensorCache.setSensorState(it.controlChip, it.state)
        }

        if (!Cs108Connector.isReady) {
            sensorMessages.postValue("Interrogateur non connecté")
            return
        }

        cs108Scanner.start()
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
     * Updates the end time of the scan in the database and tries to upload the scan to the server.
     * If the upload fails, the scan will be uploaded later.
     */
    fun stopScan() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                cs108Scanner.stop()
                currentScanState.postValue(ScanState.STOPPED)

                activeScanId?.let { scanId ->
                    val results = resultDao.getAllResults()
                    if (results.isNotEmpty()) {
                        val now = Timestamp(System.currentTimeMillis()).toString()
                        scanRepository.updateScanEndTime(scanId, now)
                        structureId?.let { structureViewModel.tryUploadScan(it, scanId) }
                    }
                }
            } catch (e: Exception) {
                Log.e("ScanViewModel", "Error stopping scan", e)
            } finally {
                rfidBuffer.stop()
                sensorCache.clearCache()
            }
        }
    }
}
