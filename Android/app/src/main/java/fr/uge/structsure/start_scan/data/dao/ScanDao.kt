package fr.uge.structsure.start_scan.data.dao

import androidx.room.*
import fr.uge.structsure.start_scan.data.ScanEntity
import fr.uge.structsure.start_scan.data.SensorEntity

/**
 * DAO pour gérer les opérations sur les scans et capteurs dans la base de données.
 */
@Dao
interface ScanDao {

    // Insertion d'une liste de capteurs avec gestion des doublons
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSensors(sensors: List<SensorEntity>)

    @Insert
    suspend fun insertScan(scan: ScanEntity): Long

    // Mise à jour de l'état d'un capteur par clé composite
    @Query("UPDATE sensors SET state = :newState WHERE control_chip = :controlChip AND measure_chip = :measureChip")
    suspend fun updateSensorState(controlChip: String, measureChip: String, newState: String)

    // Récupération des capteurs associés
    @Query("SELECT * FROM sensors WHERE state = :state")
    suspend fun getSensorsByState(state: String): List<SensorEntity>
}
