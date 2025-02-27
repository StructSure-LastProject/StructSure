package fr.uge.structsure.scanPage.data.repository

import android.content.Context
import fr.uge.structsure.MainActivity.Companion.db
import fr.uge.structsure.exception.NoConnectivityException
import fr.uge.structsure.retrofit.RetrofitInstance
import fr.uge.structsure.scanPage.data.EditType
import fr.uge.structsure.scanPage.data.ResultSensors
import fr.uge.structsure.scanPage.data.ScanEdits
import fr.uge.structsure.scanPage.data.ScanEntity
import fr.uge.structsure.scanPage.data.network.dto.ScanRequestDTO
import fr.uge.structsure.scanPage.data.network.dto.ScanResultDTO
import fr.uge.structsure.scanPage.data.network.dto.SensorEditDTO
import fr.uge.structsure.structuresPage.domain.ConnectivityViewModel

/**
 * Repository class handling all scan-related data operations
 * Manages both local database operations and server communication
 */
class ScanRepository(context: Context) {
    private val scanDao = db.scanDao()
    private val resultDao = db.resultDao()
    private val sensorDao = db.sensorDao()
    private val accountDao = db.accountDao()
    private val scanEditsDao = db.scanEditsDao()
    private val connectivityViewModel = ConnectivityViewModel(context)

    /**
     * Updates the end timestamp of a scan in local database
     * Called when a scan is completed to record its end time
     *
     * @param scanId ID of the scan to update - identifies which scan to modify
     * @param endTime End timestamp to set - the completion time in string format
     */
    suspend fun updateScanEndTime(scanId: Long, endTime: String) {
        scanDao.updateEndTimestamp(scanId, endTime)
    }

    /**
     * Retrieves all scan results for the given scan
     * @return List of all result for the given scan
     */
    fun getResultsByScan(scanId: Long): List<ResultSensors> = resultDao.getResultsByScan(scanId)

    /**
     * Retrieves all scan that are done but have not been sent to the
     * server yet.
     * @return List of all unsent scan from the DB
     */
    fun getUnsentScans(): List<ScanEntity> = scanDao.getUnsentScan()

    /**
     * Submits scan results to the server
     * Sends the completed scan data to backend for storage
     * Handles different response scenarios:
     * - Success (200): Returns successful Result
     * - Not Found (404): Returns failure with "Scan introuvable"
     * - Other errors: Returns failure with server error message
     *
     * @param scanRequest DTO containing all scan data to send to server
     * @return Success or Failure with error message
     * @throws NoConnectivityException if no internet connection is available
     */
    suspend fun submitScanResults(scanRequest: ScanRequestDTO): Result<Unit> {
        return try {
            if (connectivityViewModel.isConnected.value != true) {
                throw NoConnectivityException()
            }

            if (scanRequest.results.isEmpty() && scanRequest.sensorEdits.isEmpty()) {
                return Result.success(Unit)
            }

            val response = RetrofitInstance.scanApi.submitScanResults(scanRequest)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    /**
     * Converts the scan results to a DTO to send to the server
     * @param scanId ID of the scan to convert
     * @param results List of results to convert
     * @return DTO containing all scan data
     */
    suspend fun convertToScanRequest(
        scanId: Long,
        results: List<ResultSensors>
    ): ScanRequestDTO {
        val scan = scanDao.getScanById(scanId)
        val login = accountDao.get()?.login.orEmpty()
        val edits = scanEditsDao.getAllByScanId(scanId)

        val scanResults = results.map { ScanResultDTO.from(it) }
        val sensorEdits = getSensorEdits(edits)

        return ScanRequestDTO(scan.structureId, scanId, scan.startTimestamp, scan.note, login,
            scanResults, sensorEdits)
    }

    /**
     * Retrieves all the edits that corresponds to a sensor and build
     * a list of ScanSensorEditDTOs that regroups all edits by sensors.
     * @param edits the list of all edits of the scan
     * @return the edits grouped by sensor
     */
    private suspend fun getSensorEdits(edits: List<ScanEdits>): List<SensorEditDTO> {
        val sensorEdits = mutableMapOf<String, SensorEditDTO>()

        edits.forEach {
            when (it.type) {
                EditType.SENSOR_NOTE -> {
                    /* Get the new note of the sensor */
                    val sensor = sensorEdits.getOrPut(it.value) { SensorEditDTO(it.value) }
                    sensorEdits[it.value] = sensor.copy(note = sensorDao.getSensor(it.value)?.note)
                }
                EditType.SENSOR_PLACE -> {
                    /* Get the new position of the sensor */
                    val sensor = sensorEdits.getOrPut(it.value) { SensorEditDTO(it.value) }
                    sensorEdits[it.value] = sensor.copy(
                        plan = sensorDao.getSensor(it.value)?.plan,
                        x = sensorDao.getSensor(it.value)?.x?.toInt(),
                        y = sensorDao.getSensor(it.value)?.y?.toInt()
                    )
                }
                EditType.SENSOR_CREATION -> {
                    /* Get the sensor created in the database and include all necessary fields */
                    val sensorCreated = sensorDao.getSensor(it.value)
                    if (sensorCreated != null) {
                        sensorEdits[it.value] = SensorEditDTO(
                            sensorId = sensorCreated.sensorId,
                            controlChip = sensorCreated.controlChip,
                            measureChip = sensorCreated.measureChip,
                            name = sensorCreated.name,
                            note = sensorCreated.note,
                            plan = sensorDao.getSensor(it.value)?.plan,
                            x = sensorDao.getSensor(it.value)?.x?.toInt(),
                            y = sensorDao.getSensor(it.value)?.y?.toInt()
                        )
                    }
                }
            }
        }

        return sensorEdits.values.toList()
    }
}


