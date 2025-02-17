package fr.uge.structsure.scanPage.data.network.api

import fr.uge.structsure.scanPage.data.network.dto.ScanRequestDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ScanApi {
    @POST("/api/scans")
    suspend fun submitScanResults(@Body scanRequest: ScanRequestDTO): Response<Unit>
}