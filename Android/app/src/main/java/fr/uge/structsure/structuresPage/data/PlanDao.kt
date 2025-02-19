package fr.uge.structsure.structuresPage.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface PlanDao {

    @Upsert
    fun upsertPlan(plan: PlanDB)

    @Query("DELETE FROM `plan` WHERE structureId = :structureId")
    fun deletePlansByStructureId(structureId: Long)

    @Query("SELECT id FROM `plan` WHERE structureId = :structureId ORDER BY id DESC")
    suspend fun getPlanByStructureId(structureId: Long): List<Long>

    @Query("DELETE FROM `plan` WHERE id = :planId")
    fun deleteImagePlan(planId: Long)
}