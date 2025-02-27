package fr.uge.structsure.scanPage.domain

/**
 * Data class representing the information of an alert.
 * @param state true if sensor is NOK, false if DEFECTIVE
 * @param sensorId the ID of the sensor
 */
data class AlertInfo(
    val state: Boolean,
    val sensorId: String
)