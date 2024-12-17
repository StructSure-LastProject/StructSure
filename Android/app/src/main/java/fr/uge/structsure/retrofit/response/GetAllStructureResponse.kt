package fr.uge.structsure.retrofit.response

import com.google.gson.annotations.SerializedName

data class GetAllStructureResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("numberOfSensors") val numberOfSensors: Int,
    @SerializedName("numberOfPlans") val numberOfPlans: Int,
    @SerializedName("url") val url: String
)
