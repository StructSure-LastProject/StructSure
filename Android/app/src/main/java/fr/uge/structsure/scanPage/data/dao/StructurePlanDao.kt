package fr.uge.structsure.scanPage.data.dao

import androidx.room.*
import fr.uge.structsure.scanPage.data.StructureEntity
import fr.uge.structsure.structuresPage.data.PlanDB

/**
 * DAO for the StructureEntity class.
 * This class provides methods for accessing the StructureEntity table in the database.
 * @see StructureEntity
 * @see PlanEntity
 */
@Dao
interface StructurePlanDao {

    @Insert
    suspend fun insertStructure(structure: StructureEntity): Long

    @Query("SELECT * FROM structures")
    suspend fun getAllStructures(): List<StructureEntity>

    @Query("SELECT * FROM `plan` WHERE structureId = :structureId")
    suspend fun getPlansByStructureId(structureId: Long): List<PlanDB>
}

