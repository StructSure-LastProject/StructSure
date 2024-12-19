package fr.uge.structsure.start_scan.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity class for the Sensor table.
 * @param controlChip Control chip identifier.
 * @param measureChip Measure chip identifier.
 * @param name Name of the sensor.
 * @param note Note associated with the sensor.
 * @param state Initial state of the sensor.
 * @param installationDate Installation date.
 * @param x X coordinate.
 * @param y Y coordinate.
 */
@Entity(tableName = "sensors", primaryKeys = ["control_chip", "measure_chip"])
data class SensorEntity(
    @ColumnInfo(name = "control_chip") val controlChip: String,
    @ColumnInfo(name = "measure_chip") val measureChip: String,
    val name: String,
    val note: String,
    val state: String = "UNSCAN",
    @ColumnInfo(name = "installation_date") val installationDate: String,
    val x: Double,
    val y: Double
)
