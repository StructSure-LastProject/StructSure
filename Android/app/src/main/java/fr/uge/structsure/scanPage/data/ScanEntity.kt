package fr.uge.structsure.scanPage.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity class for the Scan table.
 * @param id Unique scan ID.
 * @param structureId ID of the associated structure.
 * @param startTimestamp time at which the scan began
 * @param endTimestamp time at which the scan got stopped
 * @param note Note associated with the scan.
 */
@Entity(tableName = "scan")
data class ScanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0, // ID unique du scan
    val structureId: Long,
    val startTimestamp: String,
    val endTimestamp: String,
    val technician: String,
    val note: String
)
