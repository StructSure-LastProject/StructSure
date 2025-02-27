package fr.uge.structsure.structuresPage.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import fr.uge.structsure.scanPage.presentation.components.SensorState

data class Sensor(
    val controlChip: String,
    val measureChip: String,
    val name: String,
    val note: String,
    val installationDate: String,
    val state: String,
    val plan: Long?,
    val x: Int?,
    val y: Int?
)

@Entity(tableName = "sensors")
data class SensorDB(
    @PrimaryKey val sensorId: String,
    val controlChip: String,
    val measureChip: String,
    val name: String,
    val note: String?,
    val installationDate: String?,
    private val _state: String?,
    val plan: Long? = null,
    val x: Int? = null,
    val y: Int? = null,
    val structureId: Long
) {
    val state: String
        get() = _state ?: SensorState.UNKNOWN.name
    companion object
}
