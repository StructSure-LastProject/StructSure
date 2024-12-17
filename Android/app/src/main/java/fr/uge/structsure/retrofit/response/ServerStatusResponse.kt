package fr.uge.structsure.retrofit.response

import com.google.gson.annotations.SerializedName


data class ServerStatusResponse(@SerializedName("state") val state: Boolean)
