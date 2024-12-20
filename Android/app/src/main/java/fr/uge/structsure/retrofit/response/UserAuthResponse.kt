package fr.uge.structsure.retrofit.response

import com.google.gson.annotations.SerializedName

data class UserAuthResponse (
    @SerializedName("token") val token: String,
    @SerializedName("type") val type: String,
    @SerializedName("login") val login: String,
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName") val lastName: String,
    @SerializedName("role") val role: String
)