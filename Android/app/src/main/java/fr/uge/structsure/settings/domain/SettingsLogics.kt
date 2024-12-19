package fr.uge.structsure.settings.domain

import android.util.Log
import fr.uge.structsure.retrofit.RetrofitInstance
import fr.uge.structsure.retrofit.ServerStatusApi
import fr.uge.structsure.retrofit.response.ServerStatusResponse
import retrofit2.Call
import retrofit2.Response


private fun getApiInterface(): ServerStatusApi {
    return RetrofitInstance.serverStatus
}

fun checkServerStatus(callback: (States) -> Unit) {
    val apiInterface = getApiInterface()
    val call = apiInterface.serverStatus()

    call.enqueue(object : retrofit2.Callback<ServerStatusResponse> {
        override fun onResponse(
            p0: Call<ServerStatusResponse>,
            p1: Response<ServerStatusResponse>
        ) {
            if (p1.isSuccessful) {
                val status = p1.body()
                if (status != null && status.state) {
                    callback(States.UP)
                } else {
                    callback(States.DOWN) // Handle case where status is null or false
                }
            } else {
                callback(States.DOWN) // Handle unsuccessful response
            }
        }

        override fun onFailure(p0: Call<ServerStatusResponse>, p1: Throwable) {
            Log.d("ERROR API", p1.message.toString())
            callback(States.DOWN)
        }
    })
}
