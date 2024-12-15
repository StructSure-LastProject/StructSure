package fr.uge.structsure.download_structure.domain

import fr.uge.structsure.download_structure.data.StructureDao
import fr.uge.structsure.download_structure.data.StructureDownloadApi
import fr.uge.structsure.download_structure.data.StructureDto
import fr.uge.structsure.download_structure.data.toDomain
import fr.uge.structsure.download_structure.data.toEntity
import kotlinx.coroutines.flow.map
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

/*
class StructureRepository(
    private val api: StructureDownloadApi,
    private val dao: StructureDao
) {


    fun getStructures() = dao.getAllStructures().map { entities ->
        entities.map { it.toDomain() }
    }
}
*/
class StructureRepository(private val structureDao: StructureDao) {

    @GetMapping("/{name}")
    fun getStructureDetails(@PathVariable name: String): StructureDto {
        return StructureDto(name) // Exemple
    }

    fun getActiveStructures(): List<StructureDto> {
        return structureDao.getActiveStructures().map { name ->
            StructureDto(name = name)
        }
    }
}