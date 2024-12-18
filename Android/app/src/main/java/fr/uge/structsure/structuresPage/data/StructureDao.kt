package fr.uge.structsure.structuresPage.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface StructureDao {

    @Upsert
    fun upsertStructure(structureData: StructureData)

    @Delete
    fun deleteStructure(structureData: StructureData)

    @Query("SELECT * FROM structure")
    fun getAllStructures(): List<StructureData>
}