package fr.uge.structsure.startScan.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import fr.uge.structsure.startScan.data.ResultEntity
import fr.uge.structsure.startScan.data.ResultSensors

@Dao
interface ResultDao {
    @Insert
    suspend fun insertResult(resultSensor : ResultSensors)
}