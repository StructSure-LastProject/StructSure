package fr.uge.structsure.structuresPage.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface StructureDao {

    @Upsert
    fun upsertStructure(structureData: StructureData)

    @Query("DELETE FROM structure WHERE id = :id")
    fun deleteStructure(id: Long)

    @Query("SELECT * FROM structure")
    fun getAllStructures(): List<StructureData>
}