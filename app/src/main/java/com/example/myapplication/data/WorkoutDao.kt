package com.example.myapplication.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(workout: Workout)

    @Query("SELECT * FROM workouts WHERE userId = :userId ORDER BY dateTime DESC")
    fun getAllWorkouts(userId: Int): Flow<List<Workout>>

    @Query("SELECT * FROM workouts WHERE id = :id")
    fun getWorkoutById(id: Int): Flow<Workout>

    @Query("DELETE FROM workouts WHERE id = :id")
    suspend fun deleteById(id: Int)
}
