package fr.uge.structsure.startScan.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity class for the Structure table.
 * @param id Unique ID.
 * @param name Name of the structure.
 * @param note Descriptive note associated with the structure.
 */
@Entity(tableName = "structures")
data class StructureEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val note: String? = null
)
