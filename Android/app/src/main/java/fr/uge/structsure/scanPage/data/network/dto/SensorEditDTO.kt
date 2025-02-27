package fr.uge.structsure.scanPage.data.network.dto

/**
 * Object sent to the server that signals modifications of a sensor.
 * Any field other than ID that is null has not been edited.
 * @property sensorId the id of the edited sensor
 * @property note the new note value (null if unchanged)
 * @property controlChip the control chip value (set when a sensor is created)
 * @property measureChip the measure chip value (set when a sensor is created)
 * @property name the name of the sensor (set when a sensor is created)
 */
data class SensorEditDTO(
    val sensorId: String,
    val controlChip: String? = null,
    val measureChip: String? = null,
    val name: String? = null,
    val note: String? = null,
    val plan: Long?,
    val x: Int?,
    val y: Int?
) {
    constructor(sensorId: String) :
        this(sensorId, null, null, null, null,null, null, null)
}