package fr.uge.structsure.download_structure.data

import fr.uge.structsure.download_structure.domain.StructureRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import retrofit2.http.GET
import retrofit2.http.Path

@RestController
@RequestMapping("/api/structures")
class StructureDownloadApi(private val structureRepository: StructureRepository) {

    // Liste des ouvrages non archivés
    @GetMapping
    fun getActiveStructures(): List<StructureDto> {
        return structureRepository.getActiveStructures()
    }

    // Détails d'un ouvrage spécifique
    @GetMapping("/{name}")
    fun getStructureDetails(@PathVariable name: String): StructureDto {
        return structureRepository.getStructureDetails(name)
    }
}