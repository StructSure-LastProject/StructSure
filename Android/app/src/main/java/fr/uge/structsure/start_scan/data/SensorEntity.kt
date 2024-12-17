package fr.uge.structsure.start_scan.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Représentation d'un capteur dans la base de données locale Room.
 * Utilise une clé composite composée de "controlChip" et "measureChip".
 */
@Entity(tableName = "sensors", primaryKeys = ["control_chip", "measure_chip"])
data class SensorEntity(
    @ColumnInfo(name = "control_chip") val controlChip: String, // Clé composite (partie 1)
    @ColumnInfo(name = "measure_chip") val measureChip: String, // Clé composite (partie 2)
    val name: String,         // Nom du capteur
    val note: String,         // Note associée au capteur
    val state: String = "UNSCAN", // État initial du capteur
    @ColumnInfo(name = "installation_date") val installationDate: String, // Date d'installation
    val x: Double,            // Coordonnée X
    val y: Double             // Coordonnée Y
)
