package fr.uge.structsure.retrofit.response

import com.google.gson.annotations.SerializedName
import fr.uge.structsure.structuresPage.data.Plan
import fr.uge.structsure.structuresPage.data.Sensor

data class GetStructureDetailsResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("note") val note: String,
    @SerializedName("plans") val plans: List<Plan>,
    @SerializedName("sensors") val sensors: List<Sensor>
)