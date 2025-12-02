package com.example.myapplication.di

import android.content.Context
import com.example.myapplication.AppDatabase
import com.example.myapplication.AccountsRepository
import com.example.myapplication.data.WorkoutsRepository

interface AppContainer {
    val accountsRepository: AccountsRepository
    val workoutRepository: WorkoutsRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val accountsRepository: AccountsRepository by lazy {
        AccountsRepository(AppDatabase.getDatabase(context).accountDao())
    }

    override val workoutRepository: WorkoutsRepository by lazy {
        WorkoutsRepository(AppDatabase.getDatabase(context).workoutDao())
    }
}