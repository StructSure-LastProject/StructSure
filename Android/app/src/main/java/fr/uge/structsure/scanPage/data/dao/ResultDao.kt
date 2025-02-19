package fr.uge.structsure.scanPage.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import fr.uge.structsure.scanPage.data.ResultSensors

@Dao
interface ResultDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertResult(resultSensor: ResultSensors)

    @Query("SELECT * FROM resultSensor")
    fun getAllResults(): List<ResultSensors>

    @Query("SELECT * FROM resultSensor WHERE scanId = :scanId")
    fun getResultsByScanId(scanId: Long): List<ResultSensors>

    @Query("DELETE FROM resultSensor WHERE 1")
    fun deleteResults()

    @Query("""
        SELECT EXISTS(
            SELECT 1 FROM resultSensor 
            WHERE id = :sensorId 
            AND state = :state 
            AND scanId != :currentScanId
        )
    """)
    fun hasExistingResult(sensorId: String, state: String, currentScanId: Long): Boolean
}