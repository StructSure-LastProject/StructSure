package fr.uge.structsure.retrofit.response

import com.google.gson.annotations.SerializedName

data class UserAuthResponse (
    @SerializedName("token") val token: String,
    @SerializedName("type") val type: String
)