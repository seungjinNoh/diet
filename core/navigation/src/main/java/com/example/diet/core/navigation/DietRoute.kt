package com.example.diet.core.navigation

import kotlinx.serialization.Serializable

// Bottom Navigation 탑레벨 Route
@Serializable data object HomeRoute
@Serializable data object DiaryRoute
@Serializable data object AnalysisRoute
@Serializable data object SettingsRoute

// Settings 하위 Route
@Serializable data object NotificationSettingsRoute
@Serializable data object AccountSettingsRoute
@Serializable data class EditProfileRoute(val userId: Long)
