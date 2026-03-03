package com.example.diet.core.data.mapper

import com.example.diet.core.database.entity.UserProfileEntity
import com.example.diet.core.model.AppSettings
import com.example.diet.core.model.DarkMode
import com.example.diet.core.model.NotificationSettings
import com.example.diet.core.model.NutritionGoals
import com.example.diet.core.model.UserProfile

object UserProfileMapper {
    fun toDomain(entity: UserProfileEntity): UserProfile {
        return UserProfile(
            id = entity.id,
            name = entity.name,
            email = entity.email,
            avatarUrl = entity.avatarUrl,
            goals = NutritionGoals(
                dailyCalorie = entity.dailyCalorieGoal,
                carbohydrate = entity.carbohydrateGoal,
                protein = entity.proteinGoal,
                fat = entity.fatGoal,
                fiber = entity.fiberGoal
            ),
            notifications = NotificationSettings(
                mealNotificationEnabled = entity.mealNotificationEnabled,
                weeklyReportEnabled = entity.weeklyReportEnabled,
                goalExceededNotificationEnabled = entity.goalExceededNotificationEnabled,
                breakfastTime = entity.breakfastNotificationTime,
                lunchTime = entity.lunchNotificationTime,
                dinnerTime = entity.dinnerNotificationTime
            ),
            appSettings = AppSettings(
                darkMode = when (entity.darkMode) {
                    "LIGHT" -> DarkMode.LIGHT
                    "DARK" -> DarkMode.DARK
                    else -> DarkMode.AUTO
                }
            ),
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    fun toEntity(domain: UserProfile): UserProfileEntity {
        return UserProfileEntity(
            id = domain.id,
            name = domain.name,
            email = domain.email,
            avatarUrl = domain.avatarUrl,
            dailyCalorieGoal = domain.goals.dailyCalorie,
            carbohydrateGoal = domain.goals.carbohydrate,
            proteinGoal = domain.goals.protein,
            fatGoal = domain.goals.fat,
            fiberGoal = domain.goals.fiber,
            mealNotificationEnabled = domain.notifications.mealNotificationEnabled,
            weeklyReportEnabled = domain.notifications.weeklyReportEnabled,
            goalExceededNotificationEnabled = domain.notifications.goalExceededNotificationEnabled,
            breakfastNotificationTime = domain.notifications.breakfastTime,
            lunchNotificationTime = domain.notifications.lunchTime,
            dinnerNotificationTime = domain.notifications.dinnerTime,
            darkMode = domain.appSettings.darkMode.name,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt
        )
    }
}
