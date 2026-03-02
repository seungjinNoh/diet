package com.example.diet.core.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "foods")
data class FoodEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val emoji: String = "🍽️",       // 음식 이모지
    val servingSize: Float = 100f,  // 기준량 (g)
    val calories: Int,               // 기준량 당 칼로리
    @Embedded(prefix = "nutrition_")
    val nutrition: NutritionInfoEmbedded = NutritionInfoEmbedded()
)
