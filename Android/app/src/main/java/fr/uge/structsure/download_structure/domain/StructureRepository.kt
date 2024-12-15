package fr.uge.structsure.download_structure.domain

import fr.uge.structsure.download_structure.data.StructureDao
import fr.uge.structsure.download_structure.data.StructureDownloadApi
import fr.uge.structsure.download_structure.data.toDomain
import fr.uge.structsure.download_structure.data.toEntity
import kotlinx.coroutines.flow.map

class StructureRepository(
    private val api: StructureDownloadApi,
    private val dao: StructureDao
) {
    suspend fun downloadStructure(name: String) {
        val structureDto = api.downloadStructure(name)      // Appel à l'API
        val structureEntity = structureDto.toEntity()       // Conversion DTO -> Entity
        dao.insert(structureEntity)                         // Enregistrement local
    }

    fun getStructures() = dao.getAllStructures().map { entities ->
        entities.map { it.toDomain() }
    }
}