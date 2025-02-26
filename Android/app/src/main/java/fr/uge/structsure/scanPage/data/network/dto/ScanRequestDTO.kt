package fr.uge.structsure.scanPage.data.network.dto

/**
 * Data Transfer Object representing a complete scan with all its results
 *
 * @property structureId Unique identifier of the structure scanned
 * @property scanId Unique identifier of the scan
 * @property launchDate Timestamp when the scan was initiated
 * @property note Optional note associated with the scan
 * @property login User login who initiated the scan
 * @property results List of results for each sensor scanned
 */
data class ScanRequestDTO(
    val structureId: Long,
    val scanId: Long,
    val launchDate: String,
    val note: String,
    val login: String,
    val results: List<ScanResultDTO>,
    val sensorEdits: List<SensorEditDTO>
)