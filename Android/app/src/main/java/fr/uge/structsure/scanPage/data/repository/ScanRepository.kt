package fr.uge.structsure.scanPage.data.repository

import android.content.Context
import fr.uge.structsure.MainActivity.Companion.db
import fr.uge.structsure.exception.NoConnectivityException
import fr.uge.structsure.retrofit.RetrofitInstance
import fr.uge.structsure.scanPage.data.ResultSensors
import fr.uge.structsure.scanPage.data.ScanEntity
import fr.uge.structsure.scanPage.data.network.dto.ScanRequestDTO
import fr.uge.structsure.scanPage.data.network.dto.ScanResultDTO
import fr.uge.structsure.structuresPage.domain.ConnectivityViewModel

/**
 * Repository class handling all scan-related data operations
 * Manages both local database operations and server communication
 */
class ScanRepository(private val context: Context) {
    private val scanDao = db.scanDao()
    private val resultDao = db.resultDao()
    private val sensorDao = db.sensorDao()
    private val accountDao = db.accountDao()
    private val connectivityViewModel = ConnectivityViewModel(context)
    private val sensorScanModificationDao = db.sensorScanModificationDao()

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

            if (scanRequest.results.isEmpty()) {
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
     * @param currentScanId ID of the scan to convert
     * @param scannedSensorResults List of results to convert
     * @return DTO containing all scan data
     */
    suspend fun convertToScanRequest(
        currentScanId: Long,
        scannedSensorResults: List<ResultSensors>
    ): ScanRequestDTO {
        val currentScan = scanDao.getScanById(currentScanId)
        val currentUser = accountDao.get()
        val sensorNoteModifications = sensorScanModificationDao.getModificationsByScanId(currentScanId)

        val scanResults = scannedSensorResults.map { sensorResult ->
            val originalSensor = sensorDao.getSensor(sensorResult.id)
            val sensorNoteModification = sensorNoteModifications.find { it.sensorId == sensorResult.id }

            ScanResultDTO(
                sensorId = sensorResult.id,
                control_chip = sensorResult.controlChip,
                measure_chip = sensorResult.measureChip,
                name = originalSensor?.name ?: "",
                state = sensorResult.state,
                note = sensorNoteModification?.modifiedNote ?: originalSensor?.note ?: "",
                installation_date = currentScan.start_timestamp
            )
        }

        return ScanRequestDTO(
            structureId = currentScan.structureId,
            scanId = currentScanId,
            launchDate = currentScan.start_timestamp,
            note = currentScan.note ?: "",
            login = currentUser?.login ?: "",
            results = scanResults
        )
    }
}


