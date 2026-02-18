package com.example.diet.core.model

import java.time.LocalDate

data class Diary(
    val id: Long = 0,
    val date: LocalDate,
    val meals: List<Meal> = emptyList(),
    val totalCalories: Int = 0,
    val totalNutrition: NutritionInfo = NutritionInfo()
)
