package fr.uge.structsure.start_scan.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity class for the Scan table.
 * @param id Unique scan ID.
 * @param structureId ID of the associated structure.
 * @param date Scan date (timestamp).
 * @param note Note associated with the scan.
 */
@Entity(tableName = "scans")
data class ScanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0, // ID unique du scan
    val structureId: Int, // ID de la structure associée au scan
    val date: Long, // Date du scan (timestamp)
    val note: String? = null // Note associée au scan
)
