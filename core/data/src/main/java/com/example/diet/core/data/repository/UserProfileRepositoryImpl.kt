package com.example.diet.core.data.repository

import com.example.diet.core.data.mapper.UserProfileMapper
import com.example.diet.core.database.dao.UserProfileDao
import com.example.diet.core.data.repository.UserProfileRepository
import com.example.diet.core.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserProfileRepositoryImpl @Inject constructor(
    private val userProfileDao: UserProfileDao
) : UserProfileRepository {

    override fun getProfile(): Flow<UserProfile?> {
        return userProfileDao.getProfile()
            .map { entity -> entity?.let { UserProfileMapper.toDomain(it) } }
    }

    override suspend fun getProfileOnce(): UserProfile? {
        return userProfileDao.getProfileOnce()?.let { UserProfileMapper.toDomain(it) }
    }

    override suspend fun createProfile(profile: UserProfile) {
        val entity = UserProfileMapper.toEntity(profile)
        userProfileDao.insertProfile(entity)
    }

    override suspend fun updateProfile(profile: UserProfile) {
        val entity = UserProfileMapper.toEntity(profile)
        userProfileDao.updateProfile(entity)
    }

    override suspend fun updateCalorieGoal(goal: Int) {
        userProfileDao.updateCalorieGoal(goal)
    }

    override suspend fun updateMacroGoals(carb: Float, protein: Float, fat: Float, fiber: Float) {
        userProfileDao.updateMacroGoals(carb, protein, fat, fiber)
    }
}
