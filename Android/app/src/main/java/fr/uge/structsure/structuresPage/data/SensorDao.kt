package fr.uge.structsure.structuresPage.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface SensorDao {

    @Upsert
    fun upsertSensor(sensor: SensorDB)

    @Query("DELETE FROM sensors WHERE structureId = :structureId")
    fun deleteSensorsByStructureId(structureId: Long)

    @Query("SELECT * from sensors as s WHERE s.structureId = :id")
    suspend fun getAllSensors(id: Long): List<SensorDB>
}