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
    private val scanApi = RetrofitInstance.scanApi
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

            if (scanRequest.results.isEmpty()) {
                return Result.success(Unit)
            }

            val response = scanApi.submitScanResults(scanRequest)
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
     * Converts local scan results into a DTO format for API submission
     * Takes local database entities and maps them to transfer objects
     * Process:
     * 1. Maps each ResultSensors to ScanResultDTO
     * 2. Fetches sensor details for each result
     * 3. Combines all data into final ScanRequestDTO
     *
     * @param scanId the id of the scan that contains the results
     * @param results List of local scan results to be converted
     * @return ScanRequestDTO Complete DTO ready to be sent to server
     */
    suspend fun convertToScanRequest(
        scanId: Long,
        results: List<ResultSensors>
    ): ScanRequestDTO {

        val scanResults = results.mapNotNull{ result ->
            val sensor = sensorDao.getSensor(result.id) ?: return@mapNotNull null
            ScanResultDTO(
                sensorId = result.id,
                control_chip = result.controlChip,
                measure_chip = result.measureChip,
                name = sensor.name,
                state = result.state,
                note = "", // TODO
                installation_date = "0" // TODO
            )
        }

        val scan = scanDao.getScanById(scanId)
        return ScanRequestDTO(
            scanId = scanId,
            launchDate = scan.start_timestamp,
            note = scan.note,
            results = scanResults
        )
    }
}


