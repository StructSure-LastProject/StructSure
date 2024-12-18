package fr.uge.structsure.structuresPage.data

import androidx.room.Entity
import androidx.room.PrimaryKey

data class Plan(
    @PrimaryKey val id: Long,
    val name: String,
    val section: String,
    val imageUrl: String
)

@Entity(tableName = "plan")
data class PlanDB(
    @PrimaryKey val id: Long,
    val name: String,
    val section: String,
    val imageUrl: String,
    val structureId: Long
)
