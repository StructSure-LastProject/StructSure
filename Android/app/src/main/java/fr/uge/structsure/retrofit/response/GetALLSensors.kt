package fr.uge.structsure.retrofit.response

import com.google.gson.annotations.SerializedName

/**
 * Response class for the GetAllSensors API.
 * @param controlChip The control chip of the sensor.
 * @param measureChip The measure chip of the sensor.
 * @param name The name of the sensor.
 * @param note The note of the sensor.
 * @param state The state of the sensor.
 * @param installationDate The installation date of the sensor.
 * @param x The x coordinate of the sensor.
 * @param y The y coordinate of the sensor.
 */
data class GetAllSensorsResponse(
    @SerializedName("controlChip") val controlChip: String,
    @SerializedName("measureChip") val measureChip: String,
    @SerializedName("name") val name: String,
    @SerializedName("note") val note: String,
    @SerializedName("state") val state: String,
    @SerializedName("installationDate") val installationDate: String,
    @SerializedName("x") val x: Double,
    @SerializedName("y") val y: Double
)
