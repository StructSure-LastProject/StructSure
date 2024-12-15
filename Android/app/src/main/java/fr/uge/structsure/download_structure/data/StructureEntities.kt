package fr.uge.structsure.download_structure.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import fr.uge.structsure.download_structure.domain.StructureData
import fr.uge.structsure.download_structure.domain.StructureDownloadState

@Entity(tableName = "structures")
data class StructureEntity(
    @PrimaryKey val name: String,
    val state: String
)

fun StructureEntity.toDomain() = StructureData(
    name = name,
    state = StructureDownloadState.fromString(state)
)

fun StructureData.toEntity() = StructureEntity(
    name = name,
    state = state.state
)