package fr.uge.structsure.scanPage.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import fr.uge.structsure.scanPage.data.ResultSensors

/**
 * DAO for the sensors scanned results.
 * This class provides methods for accessing the ResultSensors table in the database.
 * @see ResultSensors
 */
@Dao
interface ResultDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertResult(resultSensor: ResultSensors)

    @Query("SELECT * FROM resultSensor")
    fun getAllResults(): List<ResultSensors>

    @Query("SELECT * FROM resultSensor WHERE scanId = :scanId")
    fun getResultsByScan(scanId: Long): List<ResultSensors>

    @Query("DELETE FROM resultSensor WHERE 1")
    fun deleteResults()

    @Query("DELETE FROM resultSensor WHERE id = :resultId")
    fun deleteResult(resultId: String)
}