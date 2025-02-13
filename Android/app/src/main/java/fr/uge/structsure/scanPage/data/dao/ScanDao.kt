package fr.uge.structsure.scanPage.data.dao

import androidx.room.*
import fr.uge.structsure.scanPage.data.ScanEntity
import fr.uge.structsure.structuresPage.data.SensorDB

/**
 * DAO for the ScanEntity class.
 * This class provides methods for accessing the ScanEntity table in the database.
 * @see ScanEntity
 */
@Dao
interface ScanDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSensors(sensors: List<SensorDB>)

    @Insert
    fun insertScan(scan: ScanEntity): Long


    @Query("UPDATE scan SET end_timestamp = :endTime WHERE id = :scanId")
    suspend fun updateEndTimestamp(scanId: Long, endTime: String)

}

