package fr.uge.structsure.scanPage.data.network.dto

data class ScanResultDTO(
    val sensorId: String,
    val control_chip: String,
    val measure_chip: String,
    val name: String,
    val state: String,
    val note: String,
    val installation_date: String
)