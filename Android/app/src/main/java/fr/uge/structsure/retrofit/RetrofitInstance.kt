package fr.uge.structsure.retrofit

import android.content.Context
import fr.uge.structsure.MainActivity
import fr.uge.structsure.settingsPage.presentation.PreferencesManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * RetrofitInstance is a singleton object that provides Retrofit-based API service instances for the application.
 * It dynamically initializes Retrofit with a provided base URL and supports token-based authentication.
 *
 * - `structureApi`: Provides access to the `StructureApi` interface for API calls related to structure operations.
 * - `loginApi`: Provides access to the `LoginApi` interface for API calls related to login operations.
 *
 * The object handles:
 * - Setting up OkHttpClient with interceptors for logging and token injection.
 * - Providing lazy-loaded API service instances.
 * - Ensuring Retrofit is initialized before usage.
 */
object RetrofitInstance {
    private var retrofit: Retrofit? = null
    private var baseUrl: String? = null

    private var tokenProvider: () -> String = {
        val account = MainActivity.db.accountDao().get()
        account?.token.orEmpty()
    }

    /**
     * An interceptor for logging network requests and responses.
     * This is useful for debugging API interactions.
     */
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
       // level = HttpLoggingInterceptor.Level.BODY
    }

    /**
     * Retrieves the current base URL.
     *
     * @return The current base URL or null if not initialized.
     */
    fun getBaseUrl(): String? = baseUrl

    /**
     * An interceptor for injecting the authentication token into API requests.
     * If the server returns a 401 (Unauthorized), it triggers a navigation to the login screen.
     */
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

    /**
     * The OkHttpClient instance configured with interceptors for logging and token injection.
     */
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(tokenInjector)
        .build()

    /**
     * Initializes Retrofit with a dynamic base URL.
     *
     * @param baseUrl The base URL of the server.
     * @throws IllegalArgumentException if the provided base URL is invalid.
     */
    fun init(baseUrl: String) {
        this.baseUrl = baseUrl
        retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * Initializes Retrofit by fetching the server URL from SharedPreferences.
     *
     * @param context The application context used to access SharedPreferences.
     */
    fun initFromPreferences(context: Context) {
        val savedUrl = PreferencesManager.getServerUrl(context)
        if (!savedUrl.isNullOrEmpty()) {
            init(savedUrl)
        }
    }

    /**
     * Checks if Retrofit has been initialized.
     *
     * @return `true` if Retrofit is initialized, `false` otherwise.
     */
    fun isInitialized(): Boolean = retrofit != null

    /**
     * Retrieves the Retrofit instance.
     *
     * @return The Retrofit instance.
     * @throws IllegalStateException if Retrofit has not been initialized.
     */
    private fun getRetrofit(): Retrofit =
        retrofit ?: throw IllegalStateException("Retrofit n'a pas été initialisé.")

    /**
     * Provides an instance of the `StructureApi` interface for making structure-related API calls.
     */
    val structureApi: StructureApi
        get() = getRetrofit().create(StructureApi::class.java)

    /**
     * Provides an instance of the `LoginApi` interface for making login-related API calls.
     */
    val loginApi: LoginApi
        get() = getRetrofit().create(LoginApi::class.java)
}
