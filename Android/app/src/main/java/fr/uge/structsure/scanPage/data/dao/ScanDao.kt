package fr.uge.structsure.scanPage.data.dao

import androidx.room.*
import fr.uge.structsure.scanPage.data.ScanEntity
import fr.uge.structsure.structuresPage.data.SensorDB

/**
 * DAO for the ScanEntity class.
 * This class provides methods for accessing the ScanEntity table in the database.
 * @see ScanEntity
 * @see SensorEntity
 */
@Dao
interface ScanDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSensors(sensors: List<SensorDB>)

    @Insert
    suspend fun insertScan(scan: ScanEntity): Long

    @Query("SELECT * from sensors as s WHERE s.structureId = :id")
    suspend fun getAllSensors(id: Long): List<SensorDB>

    @Query("UPDATE scan SET end_timestamp = :endTime WHERE id = :scanId")
    suspend fun updateEndTimestamp(scanId: Long, endTime: String)

}

