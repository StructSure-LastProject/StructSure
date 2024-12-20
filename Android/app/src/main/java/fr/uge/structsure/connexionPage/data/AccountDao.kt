package fr.uge.structsure.connexionPage.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface AccountDao {

    @Insert
    fun insertAccount(account: AccountEntity)

    @Update
    fun updateAccount(account: AccountEntity)

    suspend fun upsertAccount(account: AccountEntity): Boolean {
        val last = getByLogin(account.login)
        if (last == null) {
            clear()
            insertAccount(account)
            return false
        } else {
            updateAccount(account)
            return true
        }
    }

    @Query("SELECT * FROM account WHERE login=:login ")
    fun getByLogin(login: String): AccountEntity?

    @Query("SELECT * FROM account LIMIT 1")
    fun get(): AccountEntity?

    @Query("UPDATE account SET token = NULL WHERE login = :login")
    fun disconnect(login: String)

    @Query("DELETE FROM account")
    fun clear()
}