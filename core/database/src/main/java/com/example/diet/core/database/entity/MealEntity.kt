package com.example.diet.core.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "meals",
    foreignKeys = [
        ForeignKey(
            entity = DiaryEntity::class,
            parentColumns = ["id"],
            childColumns = ["diaryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["diaryId"])]
)
data class MealEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val diaryId: Long,
    val totalCalories: Int = 0,
    @Embedded(prefix = "total_")
    val totalNutrition: NutritionInfoEmbedded = NutritionInfoEmbedded()
)
