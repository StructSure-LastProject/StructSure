package fr.uge.structsure.structuresPage.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import fr.uge.structsure.startScan.presentation.components.SensorState

@Entity(tableName = "structure")
data class StructureData(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val name: String,
    val numberOfSensors: Long,
    val numberOfPlan: Long,
    var state: SensorState,
    val archived: Boolean,
    var downloaded: Boolean
)
