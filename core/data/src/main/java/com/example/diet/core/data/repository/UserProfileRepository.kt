package com.example.diet.core.data.repository

import com.example.diet.core.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserProfileRepository {
    fun getProfile(): Flow<UserProfile?>
    suspend fun getProfileOnce(): UserProfile?
    suspend fun createProfile(profile: UserProfile)
    suspend fun updateProfile(profile: UserProfile)
    suspend fun updateCalorieGoal(goal: Int)
    suspend fun updateMacroGoals(carb: Float, protein: Float, fat: Float, fiber: Float)
}
