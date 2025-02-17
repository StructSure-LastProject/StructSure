package fr.uge.structsure.scanPage.data.network.dto

data class ScanRequestDTO(
    val scanId: Long,
    val launchDate: String,
    val note: String,
    val results: List<ScanResultDTO>
)