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

    @Query("""
        SELECT s.sensorId, s.controlChip, s.measureChip, s.name, s.note, s.installationDate, r.state, s.`plan`, s.x, s.y, s.structureId
        FROM sensors AS s
        LEFT JOIN resultSensor AS r ON s.controlChip = r.controlChip
        WHERE s.structureId = :id
    """)
    suspend fun getAllSensorsWithResults(id: Long): List<SensorDB>

    @Query("SELECT * FROM sensors WHERE sensorId = :id")
    fun getSensor(id: String): SensorDB?
    
    @Query("SELECT * FROM sensors AS s WHERE s.`plan` = :plan")
    fun getAllSensorsByPlan(plan: Long): List<SensorDB>

    @Query("UPDATE sensors SET note = :note WHERE sensorId = :sensorId")
    fun updateNote(sensorId: String, note: String)
    
    @Query("SELECT * FROM sensors WHERE structureId = :structureId AND `plan` IS NULL")
    fun getSensorsUnplacedByStructure(structureId: Long): List<SensorDB>

    @Query("UPDATE sensors SET `plan` = :plan, x = :x, y = :y WHERE sensorId = :id")
    fun placeSensor(id: String, plan: Long?, x: Int, y: Int)

    @Query("SELECT _state FROM sensors WHERE sensorId = :sensorId")
    fun getSensorState(sensorId: String): String

    @Query("SELECT * FROM sensors as s WHERE s.measureChip = :id OR s.controlChip = :id LIMIT 1")
    fun findSensor(id: String): SensorDB?
}