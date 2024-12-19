package fr.uge.structsure.structuresPage.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "structure")
data class StructureData(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val name: String,
    val numberOfSensors: Int,
    val numberOfPlan: Int,
    val url: String,
    var state: Boolean,
    var fileLocation: String

)
