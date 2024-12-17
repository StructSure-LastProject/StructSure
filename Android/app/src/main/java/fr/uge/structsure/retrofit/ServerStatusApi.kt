package fr.uge.structsure.retrofit

import fr.uge.structsure.retrofit.response.ServerStatusResponse
import retrofit2.Call
import retrofit2.http.GET


interface ServerStatusApi {

    @GET("api/serverStatus")
    fun serverStatus(): Call<ServerStatusResponse>
}