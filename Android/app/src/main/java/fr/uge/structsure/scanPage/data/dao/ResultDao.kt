package fr.uge.structsure.scanPage.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import fr.uge.structsure.scanPage.data.ResultSensors

@Dao
interface ResultDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertResult(resultSensor : ResultSensors)

    @Query("SELECT * FROM resultSensor")
    fun getAllSensorsScanned() : List<ResultSensors>
}