package fr.uge.structsure.start_scan.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity class for the Result table.
 * @param id Unique ID.
 * @param sensorId ID of the sensor.
 * @param state Final state after interrogation (OK, NOK, etc.).
 */
@Entity(tableName = "result")
data class ResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sensorId: Int, // ID du capteur
    val state: String // État final après interrogation (OK, NOK, etc.)
)
