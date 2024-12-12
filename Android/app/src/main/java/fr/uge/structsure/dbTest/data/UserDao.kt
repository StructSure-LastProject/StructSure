package fr.uge.structsure.dbTest.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

/**
 * UserDAO (Data Access Object)
 * This interface is for performing CRUD operations with the Room DB.
 */
@Dao
interface UserDao {
    @Insert
    fun insert(userData: UserData);

    @Query("SELECT * FROM user")
    fun getAll(): List<UserData>
}