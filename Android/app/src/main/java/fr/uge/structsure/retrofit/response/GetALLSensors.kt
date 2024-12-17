package fr.uge.structsure.retrofit.response

import com.google.gson.annotations.SerializedName

data class GetAllSensorsResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("control_chip") val controlChip: String,
    @SerializedName("measure_chip") val measureChip: String,
    @SerializedName("name") val name: String,
    @SerializedName("note") val note: String,
    @SerializedName("state") val state: String,
    @SerializedName("installationDate") val installationDate: String,
    @SerializedName("x") val x: Double,
    @SerializedName("y") val y: Double
)
