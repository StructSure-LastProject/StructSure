package fr.uge.structsure.start_scan.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entité représentant un capteur dans la base de données.
 */
@Entity(tableName = "sensors")
data class SensorEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0, // ID unique du capteur
    @ColumnInfo(name = "scan_id") val scanId: Long, // ID du scan auquel le capteur est associé
    val controlChip: String, // Identifiant de la puce de contrôle
    val measureChip: String, // Identifiant de la puce de mesure
    val name: String, // Nom du capteur
    val state: String = "UNSCAN", // État du capteur (UNSCAN, OK, NOK, DEFECTIVE)
    val x: Int, // Coordonnée X du capteur
    val y: Int // Coordonnée Y du capteur
)
