package com.example.diet.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "meal_food_items",
    foreignKeys = [
        ForeignKey(
            entity = MealEntity::class,
            parentColumns = ["id"],
            childColumns = ["mealId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = FoodEntity::class,
            parentColumns = ["id"],
            childColumns = ["foodId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["mealId"]),
        Index(value = ["foodId"])
    ]
)
data class MealFoodItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val mealId: Long,
    val foodId: Long,
    val amount: Float  // 실제 섭취량 (g)
)
