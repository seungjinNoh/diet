package com.example.diet.core.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.diet.core.model.MealType

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
    val mealType: MealType,
    val time: String? = null,  // HH:mm 형식
    val totalCalories: Int = 0,
    @Embedded(prefix = "total_")
    val totalNutrition: NutritionInfoEmbedded = NutritionInfoEmbedded()
)
