package fr.uge.structsure.startScan.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity class for the Result table.
 * @param id Unique ID.
 * @param sensorId ID of the sensor.
 * @param state Final state after interrogation (OK, NOK, etc.).
 */
//@Entity(tableName = "result")
data class ResultEntity(
    val structureId: Long,
    val scanId: Long,
    // val scanEntity: ScanEntity,
    val results: ResultSensors
)
