package fr.uge.structsure.structuresPage.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface SensorDao {

    @Upsert
    fun upsertSensor(sensor: SensorDB)

    @Query("DELETE FROM sensor WHERE structureId = :structureId")
    fun deleteSensorsByStructureId(structureId: Long)
}