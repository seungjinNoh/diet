package com.example.diet.core.database.model

import androidx.room.Embedded
import androidx.room.Relation
import com.example.diet.core.database.entity.MealEntity
import com.example.diet.core.database.entity.MealFoodItem

data class MealWithFoodItems(
    @Embedded
    val meal: MealEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "mealId"
    )
    val foodItems: List<MealFoodItem>
)
