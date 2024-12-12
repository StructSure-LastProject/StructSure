package fr.uge.structsure.dbTest.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * User Data Class
 * Data Class represent a record on the database
 */
@Entity(tableName = "user")
data class UserData(
    @PrimaryKey val uid: Int,
    @ColumnInfo(name = "first_name") val firstName: String?,
    @ColumnInfo(name = "last_name") val lastName: String?,
)
