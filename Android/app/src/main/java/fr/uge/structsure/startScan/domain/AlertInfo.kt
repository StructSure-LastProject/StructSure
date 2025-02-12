package fr.uge.structsure.startScan.domain

/**
 * Data class representing the information of an alert.
 * @param state true if sensor is NOK, false if DEFECTIVE
 * @param sensorName The name of the sensor.
 * @param lastStateSensor The last state of the sensor.
 */
data class AlertInfo(
    val state: Boolean,
    val sensorName: String,
    val lastStateSensor: String
)