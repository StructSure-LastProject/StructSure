package fr.uge.structsure.retrofit.response

import com.google.gson.annotations.SerializedName

/**
 * Réponse du backend pour récupérer tous les capteurs.
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
