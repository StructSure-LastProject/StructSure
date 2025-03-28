package fr.uge.structsure.retrofit

import fr.uge.structsure.retrofit.response.GetAllStructureResponse
import fr.uge.structsure.retrofit.response.GetStructureDetailsResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface StructureApi {
    @GET("api/structures?searchByName=&orderByColumnName=NAME&orderType=ASC&archived=false")
    fun getAllStructures(): Call<List<GetAllStructureResponse>>

    @GET("/api/structures/android/{id}")
    fun getStructureDetails(@Path("id") id: Long): Call<GetStructureDetailsResponse>

    @GET("api/structures/plans/{planId}/image")
    suspend fun downloadPlanImage(@Path("planId") planId: Long): Response<ResponseBody>
}