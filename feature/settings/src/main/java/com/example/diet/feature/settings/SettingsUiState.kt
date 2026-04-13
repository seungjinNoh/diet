package com.example.diet.feature.settings

import com.example.diet.core.model.AppSettings
import com.example.diet.core.model.NotificationSettings
import com.example.diet.core.model.NutritionGoals

data class SettingsUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,

    // Stats
    val consecutiveDays: Int = 0,
    val goalAchievementRate: Int = 0,
    val registeredFoodsCount: Int = 0,

    // Goals
    val nutritionGoals: NutritionGoals = NutritionGoals(),

    // Notifications
    val notificationSettings: NotificationSettings = NotificationSettings(),

    // App Settings
    val appSettings: AppSettings = AppSettings()
)
