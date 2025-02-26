package fr.uge.structsure.scanPage.data.network.dto

import fr.uge.structsure.scanPage.data.ResultSensors

/**
 * Data Transfer Object representing a single sensor's scan result
 *
 * @property sensorId Unique identifier of the sensor
 * @property state Current state of the sensor (OK, NOK, DEFECTIVE)
 */
data class ScanResultDTO(
    val sensorId: String,
    val state: String
) {
    companion object {
        /**
         * Creates a new DTO from a scan result
         * @param result the result to create a DTO from
         * @return the DTO with the given result's values
         */
        fun from(result: ResultSensors): ScanResultDTO = ScanResultDTO(result.id, result.state)
    }
}