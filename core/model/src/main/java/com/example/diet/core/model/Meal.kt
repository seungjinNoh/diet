package com.example.diet.core.model

data class Meal(
    val id: Long = 0,
    val foods: List<Food> = emptyList(),
    val totalCalories: Int = 0,
    val totalNutrition: NutritionInfo = NutritionInfo()
)
