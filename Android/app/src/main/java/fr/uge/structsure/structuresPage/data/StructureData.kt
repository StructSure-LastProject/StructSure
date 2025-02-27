package fr.uge.structsure.structuresPage.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import fr.uge.structsure.scanPage.presentation.components.SensorState

/**
 * Data class representing a structure.
 * @property id The id of the structure.
 * @property name The name of the structure.
 * @property numberOfSensors The number of sensors in the structure.
 * @property numberOfPlan The number of plans in the structure.
 * @property state The state of the structure.
 * @property archived The archived status of the structure.
 * @property downloaded The downloaded status of the structure.
 * @property note The note associated with the structure.
 */
@Entity(tableName = "structure")
data class StructureData(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val numberOfSensors: Long,
    val numberOfPlan: Long,
    var state: SensorState,
    val archived: Boolean,
    var downloaded: Boolean,
    var note: String = ""
)