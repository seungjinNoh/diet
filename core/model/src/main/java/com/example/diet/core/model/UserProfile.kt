package com.example.diet.core.model

data class UserProfile(
    val id: Long = 1,
    val name: String,
    val email: String,
    val avatarUrl: String? = null,
    val goals: NutritionGoals,
    val notifications: NotificationSettings,
    val appSettings: AppSettings,
    val createdAt: Long,
    val updatedAt: Long
)

data class NutritionGoals(
    val dailyCalorie: Int = 2000,
    val carbohydrate: Float = 250f,
    val protein: Float = 100f,
    val fat: Float = 67f,
    val fiber: Float = 25f
)

data class NotificationSettings(
    val mealNotificationEnabled: Boolean = true,
    val weeklyReportEnabled: Boolean = true,
    val goalExceededNotificationEnabled: Boolean = false,
    val breakfastTime: String? = "08:00",
    val lunchTime: String? = "12:00",
    val dinnerTime: String? = "18:00"
)

data class AppSettings(
    val darkMode: DarkMode = DarkMode.AUTO
)

enum class DarkMode {
    AUTO, LIGHT, DARK
}
