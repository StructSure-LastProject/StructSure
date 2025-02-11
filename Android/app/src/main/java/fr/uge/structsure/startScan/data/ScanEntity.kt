package fr.uge.structsure.startScan.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Timestamp

/**
 * Entity class for the Scan table.
 * @param id Unique scan ID.
 * @param structureId ID of the associated structure.
 * @param date Scan date (timestamp).
 * @param note Note associated with the scan.
 */
@Entity(tableName = "scan")
data class ScanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0, // ID unique du scan
    val structureId: Long,
    val start_timestamp: String,
    val end_timestamp: String,
    val technician: String,
    val note: String
)
