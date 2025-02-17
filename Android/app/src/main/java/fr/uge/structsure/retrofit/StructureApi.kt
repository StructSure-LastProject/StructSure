package fr.uge.structsure.retrofit

import fr.uge.structsure.retrofit.response.GetAllStructureResponse
import fr.uge.structsure.retrofit.response.GetStructureDetailsResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface StructureApi {
    @GET("api/structures")
    fun getAllStructures(): Call<List<GetAllStructureResponse>>

    @GET("/api/structures/android/{id}")
    fun getStructureDetails(@Path("id") id: Long): Call<GetStructureDetailsResponse>

    @GET("/api/structures/{id}/plans/image")
    suspend fun downloadPlanImage(@Path("id") id: Long): Response<ResponseBody>
}