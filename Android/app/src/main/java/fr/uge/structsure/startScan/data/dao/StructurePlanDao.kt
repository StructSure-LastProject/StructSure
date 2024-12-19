package fr.uge.structsure.startScan.data.dao

import androidx.room.*
import fr.uge.structsure.startScan.data.StructureEntity
import fr.uge.structsure.structuresPage.data.Plan
import fr.uge.structsure.structuresPage.data.PlanDB

/**
 * DAO for the StructureEntity class.
 * This class provides methods for accessing the StructureEntity table in the database.
 * @see StructureEntity
 * @see PlanEntity
 */
@Dao
interface StructurePlanDao {

    // Insérer un ouvrage
    @Insert
    suspend fun insertStructure(structure: StructureEntity): Long

    /*
    // Insérer plusieurs plans
    @Insert
    suspend fun insertPlans(plans: List<PlanEntity>)

     */

    // Récupérer toutes les structures
    @Query("SELECT * FROM structures")
    suspend fun getAllStructures(): List<StructureEntity>

    // Récupérer les plans pour une structure donnée
    @Query("SELECT * FROM `plan` WHERE structureId = :structureId")
    suspend fun getPlansByStructureId(structureId: Long): List<PlanDB>
}

