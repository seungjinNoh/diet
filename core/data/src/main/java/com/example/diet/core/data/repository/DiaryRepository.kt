package com.example.diet.core.data.repository

import com.example.diet.core.model.Diary
import com.example.diet.core.model.Food
import com.example.diet.core.model.MealType
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface DiaryRepository {
    fun getDiaryByDate(date: LocalDate): Flow<Diary?>
    fun getDiariesByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<Diary>>
    fun getAllDiaries(): Flow<List<Diary>>
    suspend fun insertDiary(diary: Diary): Long
    suspend fun updateDiary(diary: Diary)
    suspend fun deleteDiary(diaryId: Long)
    suspend fun existsByDate(date: LocalDate): Boolean
    suspend fun addFoodToMeal(date: LocalDate, mealType: MealType, food: Food, amount: Float)
}
