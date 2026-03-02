package com.example.diet.core.model

data class Meal(
    val id: Long = 0,
    val mealType: MealType,
    val time: String? = null,  // HH:mm 형식 (예: "08:20")
    val foods: List<Food> = emptyList(),
    val totalCalories: Int = 0,
    val totalNutrition: NutritionInfo = NutritionInfo()
)
