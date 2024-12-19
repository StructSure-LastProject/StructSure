package fr.uge.structsure.start_scan.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Entity class for the Plan table.
 * @param id Unique ID.
 * @param structureId Reference to the structure.
 * @param name Name of the plan.
 * @param section Section of the plan.
 * @param imagePath Local path or URL of the image.
 */
@Entity(
    tableName = "plans",
    foreignKeys = [
        ForeignKey(
            entity = StructureEntity::class,
            parentColumns = ["id"],
            childColumns = ["structureId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PlanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0, // ID unique
    val structureId: Long, // Référence vers la structure
    val name: String, // Nom du plan (ex: Plan Nord, Plan Sud)
    val section: String, // Section du plan (ex: Syllan/Nord/P6/Hauban)
    val imagePath: String // Chemin local ou URL de l'image
)