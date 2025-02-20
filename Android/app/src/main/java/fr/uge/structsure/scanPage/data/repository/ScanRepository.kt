package fr.uge.structsure.scanPage.data.repository

import android.content.Context
import fr.uge.structsure.MainActivity.Companion.db
import fr.uge.structsure.exception.NoConnectivityException
import fr.uge.structsure.retrofit.RetrofitInstance
import fr.uge.structsure.scanPage.data.ResultSensors
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
     * Retrieves all scan results from local database
     *
     * @return List<ResultSensors> List of all scan results stored in local DB
     */
    fun getAllScanResults(): List<ResultSensors> {
        return resultDao.getAllResults()
    }

    /**
     * Submits scan results to the server
     * Sends the completed scan data to backend for storage
     * Handles different response scenarios:
     * - Success (200): Returns successful Result
     * - Not Found (404): Returns failure with "Scan introuvable"
     * - Other errors: Returns failure with server error message
     *
     * @param scanRequest DTO containing all scan data to send to server
     * @return Result<Unit> Success or Failure with error message
     */
    suspend fun submitScanResults(scanRequest: ScanRequestDTO): Result<Unit> {
        return try {
            if (connectivityViewModel.isConnected.value != true) {
                throw NoConnectivityException()
            }

            val response = scanApi.submitScanResults(scanRequest)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Erreur serveur: ${response.code()}"))
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
     * @param scanId Unique identifier of the scan
     * @param launchDate When the scan started (timestamp)
     * @param note Any additional information about the scan
     * @param results List of local scan results to be converted
     * @return ScanRequestDTO Complete DTO ready to be sent to server
     */
    suspend fun convertToScanRequest(
        scanId: Long,
        launchDate: String,
        note: String,
        results: List<ResultSensors>
    ): ScanRequestDTO {

        val scanResults = results.map { result ->
            val sensor = sensorDao.getSensor(result.id)
            ScanResultDTO(
                sensorId = result.id,
                control_chip = result.controlChip,
                measure_chip = result.measureChip,
                name = sensor?.name ?: "",
                state = result.state,
                note = note,
                installation_date = launchDate
            )
        }

        return ScanRequestDTO(
            scanId = scanId,
            launchDate = launchDate,
            note = note,
            results = scanResults
        )
    }

    suspend fun resendUnsentResults(): Result<Unit> {
        return try {
            val results = resultDao.getAllResults()
            if (results.isEmpty()) {
                return Result.success(Unit)
            }

            // Regrouper les résultats par scanId
            val groupedResults = results.groupBy { it.scanId }

            // Traiter chaque groupe de résultats séparément
            groupedResults.forEach { (scanId, scanResults) ->
                val scan = scanDao.getScanById(scanId)
                    ?: return@forEach // Skip si le scan n'existe pas

                val scanRequest = ScanRequestDTO(
                    scanId = scanId,
                    launchDate = scan.start_timestamp,
                    note = scan.note,
                    results = scanResults.map { result ->
                        ScanResultDTO(
                            sensorId = result.id,
                            control_chip = result.controlChip,
                            measure_chip = result.measureChip,
                            name = result.id.toString(),
                            state = result.state,
                            note = "",
                            installation_date = result.timestamp
                        )
                    }
                )

                val response = submitScanResults(scanRequest)
                if (response.isSuccess) {
                    scanResults.forEach { result ->
                        resultDao.deleteResult(result.id)
                    }
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}


