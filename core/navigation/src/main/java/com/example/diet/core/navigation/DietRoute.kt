package com.example.diet.core.navigation

import kotlinx.serialization.Serializable

// 바텀 내비게이션 최상위 Route
@Serializable data object HomeRoute
@Serializable data object DiaryRoute
@Serializable data object AnalysisRoute
@Serializable data object SettingsRoute

// 설정 하위 Route
@Serializable data object NotificationSettingsRoute
@Serializable data object AccountSettingsRoute
@Serializable data class EditProfileRoute(val userId: Long)

// 식단 하위 Route
@Serializable data class FoodRegisterRoute(val mealType: String, val date: String)
