package com.example.diet.core.database.model

import androidx.room.Embedded
import androidx.room.Relation
import com.example.diet.core.database.entity.DiaryEntity
import com.example.diet.core.database.entity.MealEntity

data class DiaryWithMeals(
    @Embedded
    val diary: DiaryEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "diaryId"
    )
    val meals: List<MealEntity>
)
