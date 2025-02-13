package fr.uge.structsure.retrofit.response

import com.google.gson.annotations.SerializedName
import fr.uge.structsure.scanPage.presentation.components.SensorState

data class GetAllStructureResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("numberOfSensors") val numberOfSensors: Long,
    @SerializedName("numberOfPlans") val numberOfPlans: Long,
    @SerializedName("archived") val archived: Boolean,
    @SerializedName("state") val state: SensorState,
)
