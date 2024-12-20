package fr.uge.structsure.connexionPage.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "account")
data class AccountEntity (
    @PrimaryKey(autoGenerate = false)
    val login: String,

    val token: String?,
    val type: String,
    val firstName: String,
    val lastName: String,
    val role: String
)