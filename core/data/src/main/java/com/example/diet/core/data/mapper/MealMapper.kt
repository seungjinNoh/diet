package com.example.diet.core.data.mapper

import com.example.diet.core.database.entity.MealEntity
import com.example.diet.core.database.model.FoodItemWithDetails
import com.example.diet.core.model.Meal

/**
 * MealEntity와 음식 리스트를 Meal 도메인 모델로 변환
 */
fun MealEntity.toDomain(foodItems: List<FoodItemWithDetails>): Meal {
    val foods = foodItems.map { it.toDomain() }

    return Meal(
        id = id,
        foods = foods,
        totalCalories = foods.sumOf { it.calories },
        totalNutrition = foods
            .map { it.nutrition }
            .reduce { acc, nutrition ->
                acc.copy(
                    carbs = acc.carbs + nutrition.carbs,
                    protein = acc.protein + nutrition.protein,
                    fat = acc.fat + nutrition.fat,
                    fiber = acc.fiber + nutrition.fiber
                )
            }
    )
}
