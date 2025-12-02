package com.example.myapplication

import kotlinx.coroutines.flow.Flow

class AccountsRepository(private val accountDao: AccountDao) {

    fun getAllAccountsStream(): Flow<List<Account>> = accountDao.getAllAccounts()

    fun getAccountStream(username: String): Flow<Account?> = accountDao.getAccount(username)

    suspend fun getAccountByUsername(username: String): Account? = accountDao.getAccountByUsername(username)

    fun getAccountByCredentials(username: String, password: String): Flow<Account?> = accountDao.getAccountByCredentials(username, password)

    suspend fun insertAccount(account: Account) {
        accountDao.insert(account)
    }
}