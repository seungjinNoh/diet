package com.example.diet.core.database.dao

import androidx.room.*
import com.example.diet.core.database.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getProfile(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id = 1")
    suspend fun getProfileOnce(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: UserProfileEntity)

    @Update
    suspend fun updateProfile(profile: UserProfileEntity)

    @Query("UPDATE user_profile SET dailyCalorieGoal = :goal WHERE id = 1")
    suspend fun updateCalorieGoal(goal: Int)

    @Query("""
        UPDATE user_profile
        SET carbohydrateGoal = :carb, proteinGoal = :protein, fatGoal = :fat, fiberGoal = :fiber
        WHERE id = 1
    """)
    suspend fun updateMacroGoals(carb: Float, protein: Float, fat: Float, fiber: Float)
}
