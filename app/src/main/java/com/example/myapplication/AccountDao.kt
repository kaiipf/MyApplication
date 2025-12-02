package com.example.myapplication

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(account: Account)

    @Query("SELECT * FROM accounts WHERE username = :username")
    fun getAccount(username: String): Flow<Account?>

    @Query("SELECT * FROM accounts WHERE username = :username")
    suspend fun getAccountByUsername(username: String): Account?

    @Query("SELECT * FROM accounts WHERE username = :username AND password = :password")
    fun getAccountByCredentials(username: String, password: String): Flow<Account?>

    @Query("SELECT * FROM accounts")
    fun getAllAccounts(): Flow<List<Account>>
}