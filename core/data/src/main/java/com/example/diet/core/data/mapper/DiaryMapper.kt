package com.example.diet.core.data.mapper

import com.example.diet.core.database.entity.DiaryEntity
import com.example.diet.core.model.Diary
import com.example.diet.core.model.Meal

/**
 * DiaryEntity와 끼니 리스트를 Diary 도메인 모델로 변환
 */
fun DiaryEntity.toDomain(meals: List<Meal>): Diary {
    return Diary(
        id = id,
        date = date,
        meals = meals,
        totalCalories = meals.sumOf { it.totalCalories },
        totalNutrition = meals
            .map { it.totalNutrition }
            .reduceOrNull { acc, nutrition ->
                acc.copy(
                    carbs = acc.carbs + nutrition.carbs,
                    protein = acc.protein + nutrition.protein,
                    fat = acc.fat + nutrition.fat,
                    fiber = acc.fiber + nutrition.fiber
                )
            } ?: com.example.diet.core.model.NutritionInfo()
    )
}

/**
 * Diary 도메인 모델을 DiaryEntity로 변환
 */
fun Diary.toEntity(): DiaryEntity {
    return DiaryEntity(
        id = id,
        date = date,
        totalCalories = totalCalories,
        totalNutrition = totalNutrition.toEntity()
    )
}
