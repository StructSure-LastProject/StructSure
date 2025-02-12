package fr.uge.structsure.startScan.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import fr.uge.structsure.structuresPage.data.Sensor
import fr.uge.structsure.structuresPage.data.SensorDB
import java.sql.Timestamp

@Entity(tableName = "resultSensor")
data class ResultSensors(
    @PrimaryKey
    val id: String,
    val timestamp: String,
    val controlChip: String,
    val measureChip: String,
    val state: String,
    val scanId: Long)

