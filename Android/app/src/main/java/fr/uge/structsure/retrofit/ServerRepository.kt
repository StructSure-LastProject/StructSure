package fr.uge.structsure.retrofit

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import fr.uge.structsure.retrofit.response.ServerStatusResponse

class ServerRepository {
    suspend fun checkServerStatus(baseUrl: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val retrofit = Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val api = retrofit.create(ServerStatusApi::class.java)

                val call: Call<ServerStatusResponse> = api.serverStatus()
                val response: Response<ServerStatusResponse> = call.execute()

                if (!response.isSuccessful) {
                    Log.e("ServerRepository", "Erreur du serveur : Code=${response.code()}, Message=${response.message()}, Body=${response.errorBody()?.string()}")
                }
                response.isSuccessful && response.body()?.state == true
            } catch (e: Exception) {
                Log.e("ServerRepository", "Erreur lors de la vérification du serveur : ${e.message}")
                false
            }
        }
    }
}
