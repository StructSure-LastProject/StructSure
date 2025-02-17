package fr.uge.structsure.scanPage.data.repository

import fr.uge.structsure.MainActivity.Companion.db
import fr.uge.structsure.retrofit.RetrofitInstance
import fr.uge.structsure.scanPage.data.ResultSensors
import fr.uge.structsure.scanPage.data.dao.ResultDao
import fr.uge.structsure.scanPage.data.dao.ScanDao
import fr.uge.structsure.scanPage.data.network.api.ScanApi
import fr.uge.structsure.scanPage.data.network.dto.ScanRequestDTO
import fr.uge.structsure.scanPage.data.network.dto.ScanResultDTO
import fr.uge.structsure.structuresPage.data.SensorDao

class ScanRepository {
    private val scanDao = db.scanDao()
    private val resultDao = db.resultDao()
    private val sensorDao = db.sensorDao()
    private val scanApi = RetrofitInstance.scanApi

    /**
     * Met à jour le timestamp de fin d'un scan
     */
    suspend fun updateScanEndTime(scanId: Long, endTime: String) {
        scanDao.updateEndTimestamp(scanId, endTime)
    }

    /**
     * Récupère tous les résultats de scan
     */
    suspend fun getAllScanResults(): List<ResultSensors> {
        return resultDao.getAllResults()
    }

    /**
     * Envoie les résultats du scan au serveur
     */
    suspend fun submitScanResults(request: ScanRequestDTO): Result<Unit> {
        return try {
            val response = scanApi.submitScanResults(request)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Server error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Convertit les résultats locaux en DTO pour l'API
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
}