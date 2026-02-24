package com.example.diet.core.data.mapper

import com.example.diet.core.database.entity.NutritionInfoEmbedded
import com.example.diet.core.model.NutritionInfo

fun NutritionInfoEmbedded.toDomain(): NutritionInfo {
    return NutritionInfo(
        carbs = carbs,
        protein = protein,
        fat = fat,
        fiber = fiber
    )
}

fun NutritionInfo.toEntity(): NutritionInfoEmbedded {
    return NutritionInfoEmbedded(
        carbs = carbs,
        protein = protein,
        fat = fat,
        fiber = fiber
    )
}
