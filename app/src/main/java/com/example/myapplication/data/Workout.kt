package com.example.myapplication.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workouts")
data class Workout(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val name: String,
    val category: String,
    val duration: Int?,
    val dateTime: Long,
    val enjoymentRating: String,
    val comments: String?
)
