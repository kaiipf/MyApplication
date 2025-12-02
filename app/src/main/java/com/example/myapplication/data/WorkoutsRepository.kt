package com.example.myapplication.data

import kotlinx.coroutines.flow.Flow

class WorkoutsRepository(private val workoutDao: WorkoutDao) {
    fun getAllWorkouts(userId: Int): Flow<List<Workout>> = workoutDao.getAllWorkouts(userId)

    suspend fun insertWorkout(workout: Workout) {
        workoutDao.insert(workout)
    }

    fun getWorkoutById(id: Int): Flow<Workout> = workoutDao.getWorkoutById(id)

    suspend fun deleteWorkoutById(id: Int) {
        workoutDao.deleteById(id)
    }
}
