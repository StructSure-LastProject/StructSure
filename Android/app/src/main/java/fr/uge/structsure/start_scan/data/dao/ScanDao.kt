package fr.uge.structsure.start_scan.data.dao

import androidx.room.*
import fr.uge.structsure.start_scan.data.ScanEntity
import fr.uge.structsure.start_scan.data.SensorEntity

/**
 * DAO pour gérer les opérations sur les scans et capteurs dans la base de données.
 */
@Dao
interface ScanDao {

    // Insertion d'un nouveau scan
    @Insert
    suspend fun insertScan(scan: ScanEntity): Long

    // Insertion d'une liste de capteurs
    @Insert
    suspend fun insertSensors(sensors: List<SensorEntity>)

    // Mise à jour de l'état d'un capteur
    @Query("UPDATE sensors SET state = :newState WHERE id = :sensorId")
    suspend fun updateSensorState(sensorId: Long, newState: String)

    // Récupération d'un scan par son ID
    @Query("SELECT * FROM scans WHERE id = :scanId")
    suspend fun getScanById(scanId: Long): ScanEntity?

    // Récupération des capteurs associés à un scan
    @Query("SELECT * FROM sensors WHERE scan_id = :scanId")
    suspend fun getSensorsByScanId(scanId: Long): List<SensorEntity>

    // Suppression d'un scan et des capteurs associés
    @Transaction
    suspend fun deleteScanWithSensors(scanId: Long) {
        deleteSensorsByScanId(scanId)
        deleteScan(scanId)
    }

    // Suppression d'un scan
    @Query("DELETE FROM scans WHERE id = :scanId")
    suspend fun deleteScan(scanId: Long)

    // Suppression des capteurs associés à un scan
    @Query("DELETE FROM sensors WHERE scan_id = :scanId")
    suspend fun deleteSensorsByScanId(scanId: Long)
}
