package fr.uge.structsure.start_scan.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entité représentant une Structure (ouvrage d'art).
 */
@Entity(tableName = "structure")
data class StructureEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String, // Nom de l'ouvrage
    val note: String // Note descriptive associée à l'ouvrage
)
