package fr.uge.structsure.download_structure.data

import fr.uge.structsure.download_structure.domain.StructureData
import fr.uge.structsure.download_structure.domain.StructureDownloadState

data class StructureDto(
    val name: String,
    val state: String // Ajoutez cette propriété
) {
    fun toDomain(): StructureData = StructureData(
        name = name,
        state = StructureDownloadState.fromString(state) // Par défaut
    )
}

fun StructureDto.toEntity(): StructureEntity {
    return StructureEntity(
        name = this.name,
        state = this.state
    )
}