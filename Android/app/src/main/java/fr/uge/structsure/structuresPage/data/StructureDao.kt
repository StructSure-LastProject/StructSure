package fr.uge.structsure.structuresPage.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

/**
 * Data access object for the structure table.
 */
@Dao
interface StructureDao {
    @Upsert
    fun upsertStructure(structureData: StructureData)

    @Query("DELETE FROM structure WHERE id = :id")
    fun deleteStructure(id: Long)

    @Query("SELECT * FROM structure")
    fun getAllStructures(): List<StructureData>

    @Query("SELECT * FROM structure WHERE id = :id")
    fun getStructureById(id: Long): StructureData?

    @Query("UPDATE structure SET note = :note WHERE id = :structureId")
    suspend fun updateStructureNote(structureId: Long, note: String)

    @Query("SELECT note FROM structure WHERE id = :structureId")
    suspend fun getStructureNote(structureId: Long): String?
}