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


}