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

    @Query("""
        SELECT s.sensorId, s.controlChip, s.measureChip, s.name, s.note, s.installationDate, r.state, s.`plan`, s.x, s.y, s.structureId
        FROM sensors AS s
        LEFT JOIN resultSensor AS r ON s.controlChip = r.controlChip
        WHERE s.structureId = :id
    """)
    suspend fun getAllSensorsWithResults(id: Long): List<SensorDB>

    @Query("SELECT * FROM sensors WHERE sensorId = :id")
    suspend fun getSensor(id: String): SensorDB?

    @Query("SELECT * FROM sensors AS s WHERE s.`plan` = :plan")
    fun getAllSensorsByPlan(plan: Long): List<SensorDB>

    @Query("UPDATE sensors SET note = :note WHERE sensorId = :sensorId")
    fun updateNote(sensorId: String, note: String)

    @Query("SELECT _state FROM sensors WHERE sensorId = :sensorId")
    fun getSensorState(sensorId: String): String
}