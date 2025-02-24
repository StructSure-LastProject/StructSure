package fr.uge.structsure.connexionPage.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Data Access Object for SensorScanModification entity
 */
@Dao
interface SensorScanModificationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertModification(modification: SensorScanModification)

    @Query("SELECT * FROM sensor_scan_modifications WHERE scanId = :scanId")
    fun getModificationsByScanId(scanId: Long): List<SensorScanModification>

    @Query("DELETE FROM sensor_scan_modifications WHERE structureId = :structureId")
    fun deleteModificationsByStructureId(structureId: Long)
}