package fr.uge.structsure.retrofit

import fr.uge.structsure.retrofit.response.Datamodel
import fr.uge.structsure.retrofit.response.UserAuthResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginApi {
    @POST("/api/login")
    fun userAuth(
        @Body
        datamodel: Datamodel
    ): Call<UserAuthResponse>
}