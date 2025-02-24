package fr.uge.structsure.connexionPage.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Data class representing a modification made to a sensor's note during a scan
 *
 * @property id Unique identifier of the modification
 * @property structureId Unique identifier of the structure scanned
 * @property scanId Unique identifier of the scan
 * @property sensorId Unique identifier of the sensor
 * @property originalNote Original note of the sensor
 * @property modifiedNote Modified note of the sensor
 * @property timestamp Timestamp when the modification was made
 */
@Entity(tableName = "sensor_scan_modifications")
data class SensorScanModification(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val structureId: Long,
    val scanId: Long,
    val sensorId: String,
    val originalNote: String?,
    val modifiedNote: String,
    val timestamp: String
)