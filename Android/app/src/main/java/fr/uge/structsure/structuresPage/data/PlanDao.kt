package fr.uge.structsure.structuresPage.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface PlanDao {

    @Upsert
    fun upsertPlan(plan: PlanDB)

    @Query("DELETE FROM `plan` WHERE structureId = :structureId")
    fun deletePlansByStructureId(structureId: Long)

    @Query("SELECT * FROM `plan`  WHERE structureId = :structureId")
    suspend fun getPlansByStructureId(structureId: Long): List<PlanDB>
}