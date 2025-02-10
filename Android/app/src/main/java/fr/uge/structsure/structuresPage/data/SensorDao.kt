package fr.uge.structsure.structuresPage.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import fr.uge.structsure.bluetooth.cs108.RfidChip

@Dao
interface SensorDao {

    @Upsert
    fun upsertSensor(sensor: SensorDB)

    @Query("DELETE FROM sensors WHERE structureId = :structureId")
    fun deleteSensorsByStructureId(structureId: Long)

    /**
     * Updates the 'state' field of a sensor identified by its
     * controlChip and measureChip.
     */
    @Query("UPDATE sensors SET state = :newState WHERE controlChip = :controlChip AND measureChip = :measureChip")
    suspend fun updateSensorDBState(
        controlChip: String,
        measureChip: String,
        newState: String
    )}