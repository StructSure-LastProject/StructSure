package fr.uge.structsure.download_structure.data

import retrofit2.http.GET
import retrofit2.http.Path

interface StructureDownloadApi {
    @GET("/api/ouvrages/{id}") // Remplace par le chemin correct de l'API
    suspend fun downloadStructure(@Path("name") name: String): StructureDto
}