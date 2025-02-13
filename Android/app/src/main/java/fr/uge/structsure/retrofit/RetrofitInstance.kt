package fr.uge.structsure.retrofit

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import fr.uge.structsure.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/***
 * RetrofitInstance is a singleton class that provides an instance of the SensorApi interface.
 * The SensorApi interface is used to make API calls to the server.
 * The BASE_URL is the base URL of the server
 * The loggingInterceptor is used to log the network requests and responses.
 * The okHttpClient is used to build the Retrofit instance with the loggingInterceptor." +
 * The sensorApi is a lazy property that provides an instance of the SensorApi interface.
 * The Retrofit instance is built with the BASE_URL, okHttpClient, and GsonConverterFactory.
 */


object RetrofitInstance {
    private var retrofit: Retrofit? = null
    private var baseUrl: String? = null

    private var tokenProvider: () -> String = {
        val account = fr.uge.structsure.MainActivity.db.accountDao().get()
        account?.token ?: ""
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        // Vous pouvez activer le log si besoin :
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val tokenInjector = { chain: Interceptor.Chain ->
        var request = chain.request()
        request = request.newBuilder()
            .header("Authorization", "Bearer " + tokenProvider())
            .build()
        val response = chain.proceed(request)
        if (response.code == 401) {
            fr.uge.structsure.MainActivity.navigateToLogin.postValue(true)
        }
        response
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(tokenInjector)
        .build()

    /**
     * Initialise Retrofit avec un base URL dynamique.
     */
    fun init(baseUrl: String) {
        this.baseUrl = baseUrl
        retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun isInitialized(): Boolean = retrofit != null

    val structureApi: StructureApi
        get() = retrofit?.create(StructureApi::class.java)
            ?: throw IllegalStateException("Retrofit n'a pas été initialisé.")

    val loginApi: LoginApi
        get() = retrofit?.create(LoginApi::class.java)
            ?: throw IllegalStateException("Retrofit n'a pas été initialisé.")

    val serverStatus: ServerStatusApi
        get() = retrofit?.create(ServerStatusApi::class.java)
            ?: throw IllegalStateException("Retrofit n'a pas été initialisé.")
}
