package fr.uge.structsure.scanPage.data.network.dto

/**
 * Data Transfer Object representing a complete scan with all its results
 *
 * @property scanId Unique identifier of the scan
 * @property launchDate Timestamp when the scan was initiated
 * @property note Optional note associated with the scan
 * @property results List of results for each sensor scanned
 */
data class ScanRequestDTO(
    val scanId: Long,
    val launchDate: String,
    val note: String,
    val results: List<ScanResultDTO>
)