package fr.uge.structsure.scanPage.data

import androidx.room.Entity

/**
 * Data class representing a modification made to a sensors or
 * structure during a scan
 *
 * @property scanId Unique identifier of the scan
 * @property type Type of modification done during the scan
 * @property value id of the edited item (type of the item can vary
 *     depending on the type of edit done)
 */
@Entity(tableName = "scan_edits", primaryKeys = ["scanId", "type", "value"])
data class ScanEdits(
    val scanId: Long,
    val type: EditType,
    val value: String
)

/**
 * Enum of all available edition types
 */
enum class EditType {
    /** Edition of the note of a sensor (creation, edition or deletion) */
    SENSOR_NOTE, SENSOR_PLACE
}