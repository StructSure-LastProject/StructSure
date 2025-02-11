package fr.uge.structsure.startScan.domain

/**
 * Data class representing the information of an alert.
 * @param newState The new state of the sensor.
 * @param sensorName The name of the sensor.
 * @param lastStateSensor The last state of the sensor.
 */
data class AlertInfo(
    val newState: String,
    val sensorName: String,
    val lastStateSensor: String
)