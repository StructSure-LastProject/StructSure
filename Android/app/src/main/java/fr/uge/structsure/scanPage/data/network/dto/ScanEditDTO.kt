package fr.uge.structsure.scanPage.data.network.dto

/**
 * Object send to the server that signal modifications of a sensor.
 * Any field other that ID that is null has not been edited.
 * @property sensorId the id of the edited sensor
 * @property note the new note value (null if unchanged)
 */
data class SensorEditDTO(
    val sensorId: String,
    val note: String?
) {
    constructor(sensorId: String) : this(sensorId, null)
}
