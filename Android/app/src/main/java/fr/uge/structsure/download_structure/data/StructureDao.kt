package fr.uge.structsure.download_structure.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StructureDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(structureEntity: StructureEntity)

    @Query("SELECT * FROM structures")
    fun getAllStructures(): Flow<List<StructureEntity>>

    @Query("SELECT name FROM structures WHERE archived = 0")
    fun getActiveStructures(): List<String>
}
