package fr.uge.structsure.start_scan.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entité représentant le Résultat d'un capteur pour un scan donné.
 */
@Entity(tableName = "result")
data class ResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sensorId: Int, // ID du capteur
    val state: String // État final après interrogation (OK, NOK, etc.)
)
