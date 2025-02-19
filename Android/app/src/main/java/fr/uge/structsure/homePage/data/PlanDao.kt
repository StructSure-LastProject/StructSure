package fr.uge.structsure.homePage.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface PlanDao {

    @Upsert
    fun upsertPlan(plan: PlanDB)

    @Query("DELETE FROM sensors WHERE structureId = :structureId")
    fun deletePlansByStructureId(structureId: Long)
}