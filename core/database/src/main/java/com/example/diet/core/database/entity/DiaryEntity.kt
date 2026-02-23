package com.example.diet.core.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "diaries")
data class DiaryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: LocalDate,
    val totalCalories: Int = 0,
    @Embedded(prefix = "total_")
    val totalNutrition: NutritionInfoEmbedded = NutritionInfoEmbedded()
)
