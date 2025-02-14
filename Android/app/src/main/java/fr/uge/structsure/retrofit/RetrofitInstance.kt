package fr.uge.structsure.retrofit

import fr.uge.structsure.MainActivity
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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

    private const val BASE_URL = "https://dev.structsure.miumo.xyz"

    private var tokenProvider: () -> String = {
        val account = MainActivity.db.accountDao().get()
        if (account == null) "" else account.token?: ""
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        // level = HttpLoggingInterceptor.Level.BODY
    }

    private val tokenInjector = { chain: Interceptor.Chain ->
        var request = chain.request()

        request = request.newBuilder()
            .header("Authorization", "Bearer " + tokenProvider())
            .build()
        val response = chain.proceed(request)
        if (response.code == 401) {
            MainActivity.navigateToLogin.postValue(true)
        }
        response
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

    val loginApi: LoginApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LoginApi::class.java)
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