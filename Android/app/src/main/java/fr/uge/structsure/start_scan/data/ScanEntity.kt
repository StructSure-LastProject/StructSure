package fr.uge.structsure.start_scan.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entité représentant un scan dans la base de données.
 */
@Entity(tableName = "scans")
data class ScanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0, // ID unique du scan
    val structureId: Int, // ID de la structure associée au scan
    val date: Long, // Date du scan (timestamp)
    val note: String? = null // Note associée au scan
)
