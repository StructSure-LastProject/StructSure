package fr.uge.structsure.scanPage.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "resultSensor")
data class ResultSensors(
    @PrimaryKey
    val id: String,
    val timestamp: String,
    val controlChip: String,
    val measureChip: String,
    val state: String,
    val scanId: Long
)

