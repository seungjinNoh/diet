package com.example.diet.core.model

data class NutritionInfo(
    val carbs: Float = 0f,      // 탄수화물 (g)
    val protein: Float = 0f,    // 단백질 (g)
    val fat: Float = 0f,        // 지방 (g)
    val fiber: Float = 0f       // 식이섬유 (g)
)
