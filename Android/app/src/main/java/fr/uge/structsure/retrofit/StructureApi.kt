package fr.uge.structsure.retrofit

import fr.uge.structsure.retrofit.response.GetAllStructureResponse
import retrofit2.Call
import retrofit2.http.GET

interface StructureApi {
    @GET("api/structure/android")
    fun getAllStructures(): Call<List<GetAllStructureResponse>>
}