package fr.uge.structsure.retrofit

import fr.uge.structsure.retrofit.response.GetAllStructureResponse
import fr.uge.structsure.retrofit.response.GetStructureDetailsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface StructureApi {
    @GET("api/structures/android")
    fun getAllStructures(): Call<List<GetAllStructureResponse>>

    @GET("/api/structures/android/{id}")
    fun getStructureDetails(@Path("id") id: Long): Call<GetStructureDetailsResponse>
}