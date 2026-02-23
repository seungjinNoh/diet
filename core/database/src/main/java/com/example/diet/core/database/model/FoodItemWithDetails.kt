package com.example.diet.core.database.model

import androidx.room.Embedded
import androidx.room.Relation
import com.example.diet.core.database.entity.FoodEntity
import com.example.diet.core.database.entity.MealFoodItem

data class FoodItemWithDetails(
    @Embedded
    val item: MealFoodItem,
    @Relation(
        parentColumn = "foodId",
        entityColumn = "id"
    )
    val food: FoodEntity
)
