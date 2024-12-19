package fr.uge.structsure.retrofit

import fr.uge.structsure.retrofit.response.GetAllSensorsResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.Call

/**
 * SensorApi is an interface that provides methods for making API calls to the server.
 */
interface SensorApi {
    @GET("api/structure/{id}/sensors")
    fun getAllSensors(
        @Path("id") id: Long,
        @Query("tri") tri: String = "nom",
        @Query("ordre") ordre: String = "asc",
        @Query("filtreEtat") filtreEtat: String? = null,
        @Query("dateInstallationMin") dateInstallationMin: String? = null,
        @Query("dateInstallationMax") dateInstallationMax: String? = null
    ): Call<List<GetAllSensorsResponse>>
}