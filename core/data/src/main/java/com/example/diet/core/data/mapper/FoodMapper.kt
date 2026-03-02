package com.example.diet.core.data.mapper

import com.example.diet.core.database.entity.FoodEntity
import com.example.diet.core.database.model.FoodItemWithDetails
import com.example.diet.core.model.Food
import com.example.diet.core.model.NutritionInfo

/**
 * FoodEntity를 Food 도메인 모델로 변환 (기준량 그대로)
 */
fun FoodEntity.toDomain(): Food {
    return Food(
        id = id,
        name = name,
        emoji = emoji,
        amount = servingSize,
        calories = calories,
        nutrition = nutrition.toDomain()
    )
}

/**
 * FoodItemWithDetails를 Food 도메인 모델로 변환
 * 실제 섭취량에 따라 칼로리와 영양소를 비율 계산
 */
fun FoodItemWithDetails.toDomain(): Food {
    val ratio = item.amount / food.servingSize

    return Food(
        id = food.id,
        name = food.name,
        emoji = food.emoji,
        amount = item.amount,
        calories = (food.calories * ratio).toInt(),
        nutrition = NutritionInfo(
            carbs = food.nutrition.carbs * ratio,
            protein = food.nutrition.protein * ratio,
            fat = food.nutrition.fat * ratio,
            fiber = food.nutrition.fiber * ratio
        )
    )
}

/**
 * Food 도메인 모델을 FoodEntity로 변환
 */
fun Food.toEntity(): FoodEntity {
    return FoodEntity(
        id = id,
        name = name,
        emoji = emoji,
        servingSize = amount,
        calories = calories,
        nutrition = nutrition.toEntity()
    )
}
