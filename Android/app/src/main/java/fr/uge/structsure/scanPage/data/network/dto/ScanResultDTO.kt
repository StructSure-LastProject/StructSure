package fr.uge.structsure.scanPage.data.network.dto

/**
 * Data Transfer Object representing a single sensor's scan result
 *
 * @property sensorId Unique identifier of the sensor
 * @property control_chip ID of the control RFID chip
 * @property measure_chip ID of the measure RFID chip
 * @property name Name of the sensor
 * @property state Current state of the sensor (OK, NOK, DEFECTIVE)
 * @property note Optional note for this specific result
 * @property installation_date Timestamp when the sensor was installed
 */
data class ScanResultDTO(
    val sensorId: String,
    val control_chip: String,
    val measure_chip: String,
    val name: String,
    val state: String,
    val note: String,
    val installation_date: String
)