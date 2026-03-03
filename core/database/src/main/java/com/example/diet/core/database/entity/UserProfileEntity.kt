package com.example.diet.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey
    val id: Long = 1,
    val name: String,
    val email: String,
    val avatarUrl: String? = null,

    // 목표 설정
    val dailyCalorieGoal: Int = 2000,
    val carbohydrateGoal: Float = 250f,
    val proteinGoal: Float = 100f,
    val fatGoal: Float = 67f,
    val fiberGoal: Float = 25f,

    // 알림 설정
    val mealNotificationEnabled: Boolean = true,
    val weeklyReportEnabled: Boolean = true,
    val goalExceededNotificationEnabled: Boolean = false,

    // 식사 알림 시간
    val breakfastNotificationTime: String? = "08:00",
    val lunchNotificationTime: String? = "12:00",
    val dinnerNotificationTime: String? = "18:00",

    // 앱 설정
    val darkMode: String = "AUTO",  // AUTO, LIGHT, DARK

    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
