package com.example.diet.core.model

data class Food(
    val id: Long = 0,
    val name: String,
    val amount: Float,       // 섭취량 (g)
    val calories: Int,
    val nutrition: NutritionInfo = NutritionInfo()
)
