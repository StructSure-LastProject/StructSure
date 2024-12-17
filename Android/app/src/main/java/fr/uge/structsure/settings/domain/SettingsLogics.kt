package fr.uge.structsure.settings.domain

import android.util.Log
import fr.uge.structsure.retrofit.RetrofitInstance
import fr.uge.structsure.retrofit.ServerStatusApi
import fr.uge.structsure.retrofit.response.ServerStatusResponse
import okhttp3.Callback
import retrofit2.Call
import retrofit2.Response


private fun getApiInterface(): ServerStatusApi {
    return RetrofitInstance.serverStatus
}

fun checkServerStatus(){
    val apiInterface = getApiInterface()
    val call = apiInterface.serverStatus()
    call.enqueue(object : retrofit2.Callback<ServerStatusResponse>{
        override fun onResponse(
            p0: Call<ServerStatusResponse>,
            p1: Response<ServerStatusResponse>
        ) {
            if (p1.isSuccessful){
                val status = p1.body()
                if (status != null) {
                    Log.d("RESPONSE", "status: ${status.state}")
                }
            }
        }

        override fun onFailure(p0: Call<ServerStatusResponse>, p1: Throwable) {
            Log.d("ERROR API", p1.message.toString())
        }
    })
}