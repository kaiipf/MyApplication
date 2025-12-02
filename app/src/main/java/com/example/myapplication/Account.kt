package com.example.myapplication

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accounts")
data class Account(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val username: String,
    val password: String,
    val email: String,
    val gender: String,
    val mobileNumber: String,
    val receiveUpdates: Boolean,
    val yearOfBirth: Int
)