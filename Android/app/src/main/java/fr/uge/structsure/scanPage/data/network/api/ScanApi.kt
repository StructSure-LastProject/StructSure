package fr.uge.structsure.scanPage.data.network.api

import fr.uge.structsure.scanPage.data.network.dto.ScanRequestDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * API interface for scan-related operations
 * Used to communicate with the backend server for submitting scan results
 */
interface ScanApi {
    /**
     * Submits scan results to the server
     *
     * @param scanRequest The DTO containing all scan data and results
     * @return Response object indicating success or failure of the operation
     */
    @POST("/api/scans")
    suspend fun submitScanResults(@Body scanRequest: ScanRequestDTO): Response<Unit>
}