package fr.uge.structsure.start_scan.data.dao

import androidx.room.*
import fr.uge.structsure.start_scan.data.PlanEntity
import fr.uge.structsure.start_scan.data.StructureEntity

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

    // Insérer plusieurs plans
    @Insert
    suspend fun insertPlans(plans: List<PlanEntity>)

    // Récupérer toutes les structures
    @Query("SELECT * FROM structures")
    suspend fun getAllStructures(): List<StructureEntity>

    // Récupérer les plans pour une structure donnée
    @Query("SELECT * FROM plans WHERE structureId = :structureId")
    suspend fun getPlansByStructureId(structureId: Long): List<PlanEntity>
}

