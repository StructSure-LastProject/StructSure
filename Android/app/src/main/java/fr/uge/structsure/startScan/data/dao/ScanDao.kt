package fr.uge.structsure.startScan.data.dao

import androidx.room.*
import fr.uge.structsure.startScan.data.ScanEntity
import fr.uge.structsure.structuresPage.data.Sensor
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

    @Query("UPDATE sensor SET state = :newState WHERE controlChip = :controlChip AND measureChip = :measureChip")
    suspend fun updateSensorState(controlChip: String, measureChip: String, newState: String)

    @Query("SELECT * from sensor as s WHERE s.structureId = :id")
    suspend fun getAllSensors(id: Long): List<SensorDB>
}
