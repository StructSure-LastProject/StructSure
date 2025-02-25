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

    @Query("SELECT * FROM sensors AS s WHERE s.structureId = :id")
    suspend fun getAllSensors(id: Long): List<SensorDB>

    @Query("SELECT * FROM sensors WHERE sensorId = :id")
    suspend fun getSensor(id: String): SensorDB?
    
    @Query("SELECT * FROM sensors AS s WHERE s.`plan` = :plan")
    fun getAllSensorsByPlan(plan: Long): List<SensorDB>

    @Query("UPDATE sensors SET note = :note WHERE sensorId = :sensorId")
    fun updateNote(sensorId: String, note: String)
    
    @Query("SELECT * FROM sensors WHERE structureId = :structureId AND `plan` IS NULL")
    fun getSensorsUnplacedByStructure(structureId: Long): List<SensorDB>

    @Query("UPDATE sensors SET `plan` = :plan, x = :x, y = :y WHERE sensorId = :id")
    fun placeSensor(id: String, plan: Long?, x: Double, y: Double)
}