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

    @Query("SELECT id FROM `plan` WHERE structureId = :structureId ORDER BY id DESC LIMIT 1") // for the moment get last id plan added
    suspend fun getPlanByStructureId(structureId: Long): Long?
}