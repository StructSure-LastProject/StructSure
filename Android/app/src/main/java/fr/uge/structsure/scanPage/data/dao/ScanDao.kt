package fr.uge.structsure.scanPage.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
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

    @Query("SELECT * FROM scan WHERE id = :scanId")
    fun getScanById(scanId: Long): ScanEntity

    @Query("SELECT * FROM scan WHERE structureId = :structureId LIMIT 1")
    fun getScanByStructure(structureId: Long): ScanEntity?

    @Query("SELECT * FROM scan WHERE end_timestamp != ''")
    fun getUnsentScan(): List<ScanEntity>

    @Query("SELECT * FROM scan WHERE end_timestamp == '' LIMIT 1")
    fun getUnfinishedScan(): ScanEntity?

    @Query("DELETE FROM scan WHERE structureId = :id")
    fun deleteScanByStructure(id: Long)

    @Query("UPDATE scan SET note = :note WHERE id = :scanId")
    suspend fun updateScanNote(scanId: Long, note: String)
}

