package fr.uge.structsure.scanPage.domain

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.uge.structsure.MainActivity.Companion.db
import fr.uge.structsure.bluetooth.cs108.Cs108Connector
import fr.uge.structsure.bluetooth.cs108.Cs108Scanner
import fr.uge.structsure.bluetooth.cs108.RfidChip
import fr.uge.structsure.scanPage.data.EditType
import fr.uge.structsure.scanPage.data.ResultSensors
import fr.uge.structsure.scanPage.data.ScanEdits
import fr.uge.structsure.scanPage.data.ScanEntity
import fr.uge.structsure.scanPage.data.cache.SensorCache
import fr.uge.structsure.scanPage.data.repository.ScanRepository
import fr.uge.structsure.scanPage.presentation.components.SensorState
import fr.uge.structsure.settingsPage.presentation.PreferencesManager.getScannerSensitivity
import fr.uge.structsure.structuresPage.data.SensorDB
import fr.uge.structsure.structuresPage.data.StructureData
import fr.uge.structsure.structuresPage.domain.StructureViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.sql.Timestamp

/**
 * ViewModel responsible for managing the scanning process.
 * It interacts with the database, sensor cache, and scanner hardware to handle sensor states and user actions.
 */
class ScanViewModel(context: Context, private val structureViewModel: StructureViewModel) : ViewModel() {

    /** The structure being scanned */
    var structure: StructureData? = null
        private set

    /** Repository to interact with the scan database */
    private val scanRepository: ScanRepository = ScanRepository(context)

    /** Scanner hardware for reading RFID chips */
    private val cs108Scanner =
        Cs108Scanner { chip ->
            if (directChipRead) ChipFinder.add(chip)
            else onTagScanned(context, chip)
        }

    /** Whether the chip are send to the ChipFinder or process for a scan */
    private var directChipRead = false

    /** Current state of the scan process: NOT_STARTED, STARTED, PAUSED, STOPPED */
    val currentScanState = MutableLiveData(ScanState.NOT_STARTED)

    /** ID of the currently active scan */
    var activeScanId: Long? = null
        private set

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

    /** LiveData for displaying the sensors that have not been scanned yet */
    val sensorsNotScanned = MutableLiveData<List<SensorDB>>()

    /** LiveData for displaying the scanned sensors */
    val sensorsScanned = MutableLiveData<List<ResultSensors>>()

    /** Counts how many results are in a given state for the scan weather */
    val sensorStateCounts = MutableLiveData<Map<SensorState, Int>>()

    /** LiveData for displaying errors when adding a sensor. */
    val addSensorError = MutableLiveData<String?>()

    /** Sub-ViewModel that handle all plan selection/display logic */
    val planViewModel = PlanViewModel(context, this)

    /** Displaying error messages when updating notes */
    val noteErrorMessage = MutableLiveData<String?>()

    /** Remember if the filters are collapsed or not */
    val sensorsFilterVisible = mutableStateOf(true)

    /** Whether a scan has been started or not yet */
    fun isScanStarted(): Boolean = currentScanState.value != ScanState.NOT_STARTED

    /**
     * Update the state of the sensors dynamically in the header of the scan page.
     */
    private fun updateSensorStateCounts() {
        viewModelScope.launch(Dispatchers.IO) {
            val scannedSensors = activeScanId?.let { scanId ->
                db.resultDao().getResultsByScan(scanId)
            } ?: emptyList()

            val stateCounts = SensorState.entries.associateWith { state ->
                if (state == SensorState.UNKNOWN) sensorCache.size() - scannedSensors.size
                else scannedSensors.count { it.state == state.name }
            }
            sensorStateCounts.postValue(stateCounts)
        }
    }

    /**
     * Updates the note of the ongoing scan.
     * The note will be saved in the database and sent to the server
     * when the scan is uploaded.
     *
     * @param note The new note to set for the scan
     * @return true if the note was updated, false otherwise
     */
    suspend fun updateScanNote(note: String): Boolean {
        return viewModelScope.async(Dispatchers.IO) {
            activeScanId?.let { scanId ->
                db.scanDao().updateScanNote(scanId, note)
                noteErrorMessage.postValue(null)
                true
            } ?: run {
                noteErrorMessage.postValue("Veuillez lancer un scan avant d'ajouter une note")
                false
            }
        }.await()
    }

    /**
     * Updates the note of the structure currently being scanned.
     * The note will be saved in the database and sent to the server
     * when the scan is uploaded.
     *
     * @param note The new note to set for the structure
     * @return true if the note was updated, false otherwise
     */
    suspend fun updateStructureNote(note: String): Boolean {
        return viewModelScope.async(Dispatchers.IO) {
            structure?.let {
                db.structureDao().updateStructureNote(it.id, note)
                noteErrorMessage.postValue(null)
                true
            } ?: run {
                noteErrorMessage.postValue("Aucune structure sélectionnée")
                false
            }
        }.await()
    }

    /**
     * Updates the note of a sensor in the modal dialog of the sensor when the scan is in progress.
     * @param sensor the sensor to update
     * @param note the new note to set
     * @return true if the note was updated, false otherwise
     */
    fun updateSensorNote(sensor: SensorDB, note: String): Boolean {
        if (activeScanId == null) {
            noteErrorMessage.postValue("Aucun scan en cours")
            return false
        }

        if (note.length > 1000) return false
        activeScanId?.let { scanId ->
            viewModelScope.launch(Dispatchers.IO) {
                db.sensorDao().updateNote(sensor.sensorId, note)
                db.scanEditsDao().upsert(ScanEdits(scanId, EditType.SENSOR_NOTE, sensor.sensorId))
                refreshSensorStates()
            }
        }
        return true
    }

    /**
     * Adds a sensor to the database and updates the list of sensors.
     * @param controlChip the id of the control chip
     * @param measureChip the id of the measure chip
     * @param name the name of the sensor
     * @param note the note of the sensor (can be empty)
     */
    fun addSensor(controlChip: String, measureChip: String, name: String, note: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val control = controlChip.replace(" ", "")
            val measure = measureChip.replace(" ", "")
            val sensorId = "${control}-${measure}"
            val sensorDB = SensorDB(sensorId, control, measure, name,
                note, Timestamp(System.currentTimeMillis()).toString(),
                SensorState.UNKNOWN.name, structureId = structure!!.id)

            db.sensorDao().upsertSensor(sensorDB)

            activeScanId?.let { scanId ->
                db.scanEditsDao().upsert(ScanEdits(scanId, EditType.SENSOR_CREATION, sensorId))
            }

            refreshSensorStates()
            addSensorError.postValue(null)
        }
    }

    /**
     * Changes the structureId of the scanViewModel. This will reload
     * the sensors if the given id is not the same as the saved one.
     * @param context used to load the image of the plan if changed
     * @param structureId the id of the structure in use
     * @param scanId the id of the uncompleted scan to continue
     */
    fun setStructure(context: Context, structureId: Long, scanId: Long? = null) {
        if (this.structure?.id == structureId) return
        viewModelScope.launch(Dispatchers.IO) {
            structure = db.structureDao().getStructureById(structureId)
            activeScanId = null
            if (structureId == -1L) {
                planViewModel.reset()
                currentScanState.postValue(ScanState.NOT_STARTED)
                return@launch
            }
            sensorCache.clearCache()
            val sensors = db.sensorDao().getAllSensorsWithResults(structureId)
            sensorsNotScanned.postValue(sensors)
            val stateCounts = SensorState.entries.associateWith { state ->
                if (state == SensorState.UNKNOWN) sensors.size else 0
            }
            sensorStateCounts.postValue(stateCounts)
            sensorCache.insertSensors(sensors)
            planViewModel.loadPlans(context, structureId)
            if (scanId != null) continueExistingScan(scanId)
            else db.scanDao().getScanByStructure(structureId)?.let { continueExistingScan(it.id) }
        }
    }

    /**
     * Refreshes the states of the sensors after starting a scan.
     */
    fun refreshSensorStates() {
        viewModelScope.launch(Dispatchers.IO) {
            val sensors = db.sensorDao().getAllSensorsWithResults(structure?.id?: return@launch)
            val scannedResults = activeScanId?.let { scanId ->
                db.resultDao().getResultsByScan(scanId)
            } ?: emptyList()

            val updatedSensors = sensors.map { sensor ->
                val result = scannedResults.find { it.id == sensor.sensorId }
                sensor.copy(_state = result?.state ?: sensor.state)
            }

            sensorsNotScanned.postValue(updatedSensors)
        }
    }

    /**
     * Gets the previous state for a sensor
     */
    fun getPreviousState(sensorId: String): String = db.sensorDao().getSensorState(sensorId)

    /**
     * Adds a scanned RFID chip ID to the buffer for processing.
     * @param chip The details of the scanned chip
     */
    private fun onTagScanned(context: Context, chip: RfidChip) {
        val sensitivity = getScannerSensitivity(context)
        if (chip.id == "") return
        if (chip.attenuation > sensitivity[0] || chip.attenuation < sensitivity[1]) return
        rfidBuffer.add(chip.id)
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
     * Returns the internal state name (NOT the display name).
     *
     * @param sensor The sensor being processed.
     * @param scannedChip The chip that was scanned.
     * @param otherChipInBuffer Whether the other chip is present in the buffer.
     * @return The new state of the sensor as an internal name.
     */
    private fun computeSensorState(sensor: SensorDB, scannedChip: String, otherChipInBuffer: Boolean): String {
        return if (scannedChip == sensor.controlChip) {
            if (otherChipInBuffer) SensorState.NOK.name else SensorState.OK.name
        } else {
            if (otherChipInBuffer) SensorState.NOK.name else SensorState.DEFECTIVE.name
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
            val result = ResultSensors(
                id = sensor.sensorId,
                timestamp = Timestamp(System.currentTimeMillis()).toString(),
                scanId = scanId,
                controlChip = sensor.controlChip,
                measureChip = sensor.measureChip,
                state = stateChanged
            )
            db.resultDao().insertResult(result)

            val currentList = sensorsScanned.value?.toMutableList() ?: mutableListOf()
            currentList.removeAll { it.id == sensor.sensorId }
            currentList.add(result)
            sensorsScanned.postValue(currentList)
        }

        when (SensorState.from(stateChanged)) {
            SensorState.OK -> {
                sensorMessages.postValue("${sensor.name} est OK")
            }
            SensorState.NOK -> {
                pauseScan()
                alertMessages.postValue( AlertInfo(true, sensor.sensorId) )
            }
            SensorState.DEFECTIVE -> {
                pauseScan()
                alertMessages.postValue( AlertInfo(false, sensor.sensorId) )
            }
            else -> Unit
        }
    }

    /**
     * Creates a new scan for the given structure.
     *
     * @param structureId ID of the structure to scan.
     */
    fun startNewScan(structureId: Long) {
        if (!Cs108Connector.isReady) {
            sensorMessages.postValue("Interrogateur non connecté")
            return
        }

        cs108Scanner.start()
        currentScanState.postValue(ScanState.STARTED)
        alertMessages.postValue(null)
        sensorsScanned.postValue(emptyList())

        if (activeScanId != null) return // already created

        val now = Timestamp(System.currentTimeMillis()).toString()

        val newScan = ScanEntity(
            structureId = structureId,
            startTimestamp = now,
            endTimestamp = "",
            technician = db.accountDao().getLogin(),
            note = ""
        )

        activeScanId = db.scanDao().insertScan(newScan)
        structure = db.structureDao().getStructureById(structureId)

        refreshSensorStates()
        updateSensorStateCounts()
    }

    /**
     * Same function as [startNewScan] but for the special case of
     * continuing an existing scan (for example after the app crashed)
     * @param scanId the id of the scan to continue
     */
    private fun continueExistingScan(scanId: Long) {
        currentScanState.postValue(ScanState.PAUSED)
        alertMessages.postValue(null)

        if (activeScanId != null) return // already created
        activeScanId = scanId
        structure = db.structureDao().getStructureById(db.scanDao().getScanById(scanId).structureId)

        refreshSensorStates()
        updateSensorStateCounts()
        db.resultDao().getResultsByScan(scanId).forEach {
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
                    val results = db.resultDao().getAllResults()
                    if (results.isNotEmpty() || db.scanEditsDao().getAllByScanId(scanId).isNotEmpty()) {
                        val now = Timestamp(System.currentTimeMillis()).toString()
                        scanRepository.updateScanEndTime(scanId, now)
                        structure?.id?.let { structureViewModel.tryUploadScan(it, scanId) }
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

    /**
     * Enable or disable the DirectChipRead. This features disable the
     * chip reading for the current scan and redirect results to the
     * ChipFinder. This enables to read chips ID directly from the
     * interrogator without scanning them.
     * @param toggle Whether to enable or disable the DirectChipRead
     * @return true for success, false otherwise
     */
    fun toggleDirectChipRead(toggle: Boolean): Boolean {
        if (toggle == directChipRead) return true
        ChipFinder.reset()
        if (toggle) {
            /* Enable DirectChipRead */
            if (!Cs108Connector.isReady) {
                sensorMessages.postValue("Interrogateur non connecté")
                return false
            }
            pauseScan()
            directChipRead = true
            cs108Scanner.start()
        } else {
            /* Disable DirectChipRead */
            cs108Scanner.stop()
            directChipRead = false
        }
        return true
    }
}
