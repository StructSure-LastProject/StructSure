package fr.uge.structsure.download_structure.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://example.com/api/" // TODO À remplacer par jsp encore quoi

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: StructureDownloadApi = retrofit.create(StructureDownloadApi::class.java)
}