package fr.uge.structsure.retrofit

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/***
 * RetrofitInstance is a singleton class that provides an instance of the SensorApi interface.
 * The SensorApi interface is used to make API calls to the server.
 * The BASE_URL is the base URL of the server.
 * The loggingInterceptor is used to log the network requests and responses.
 * The okHttpClient is used to build the Retrofit instance with the loggingInterceptor." +
 * The sensorApi is a lazy property that provides an instance of the SensorApi interface.
 * The Retrofit instance is built with the BASE_URL, okHttpClient, and GsonConverterFactory.
 */
object RetrofitInstance {

    private const val BASE_URL = "http://172.20.10.4:8080"
    private const val TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhY2hhcmYiLCJpYXQiOjE3MzQ2OTMzMjYsImV4cCI6MTczNDY5NDIyNn0.kxemVT99A-4pFqfnmW9Bu4JNPjik28RnSugi7ZuUbG0"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val tokenInjector = { chain: Interceptor.Chain ->
        chain.proceed(chain.request().newBuilder().apply {
            header("Authorization", "Bearer  $TOKEN")
        }.build())
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(tokenInjector)
        .build()

    val sensorApi: SensorApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SensorApi::class.java)
    }

    val structureApi: StructureApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(StructureApi::class.java)
    }

    val serverStatus: ServerStatusApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ServerStatusApi::class.java)
    }


}