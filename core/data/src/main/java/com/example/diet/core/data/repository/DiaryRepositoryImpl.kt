package com.example.diet.core.data.repository

import com.example.diet.core.data.mapper.toDomain
import com.example.diet.core.data.mapper.toEntity
import com.example.diet.core.database.dao.DiaryDao
import com.example.diet.core.database.dao.MealFoodItemDao
import com.example.diet.core.data.repository.DiaryRepository
import com.example.diet.core.model.Diary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class DiaryRepositoryImpl @Inject constructor(
    private val diaryDao: DiaryDao,
    private val mealFoodItemDao: MealFoodItemDao
) : DiaryRepository {

    override fun getDiaryByDate(date: LocalDate): Flow<Diary?> = flow {
        val diaryWithMeals = diaryDao.getDiaryWithMealsByDate(date)

        if (diaryWithMeals == null) {
            emit(null)
            return@flow
        }

        // 각 Meal에 대해 FoodItems를 조회하여 Meal 도메인 모델로 변환
        val meals = diaryWithMeals.meals.map { mealEntity ->
            val foodItems = mealFoodItemDao.getItemsWithDetailsByMealId(mealEntity.id).first()
            mealEntity.toDomain(foodItems)
        }

        // Diary 도메인 모델로 변환
        val diary = diaryWithMeals.diary.toDomain(meals)
        emit(diary)
    }

    override fun getDiariesByDateRange(
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<List<Diary>> = flow {
        val allDiariesWithMeals = diaryDao.getAllDiariesWithMeals().first()

        val filteredDiaries = allDiariesWithMeals
            .filter { it.diary.date in startDate..endDate }
            .map { diaryWithMeals ->
                val meals = diaryWithMeals.meals.map { mealEntity ->
                    val foodItems = mealFoodItemDao.getItemsWithDetailsByMealId(mealEntity.id).first()
                    mealEntity.toDomain(foodItems)
                }
                diaryWithMeals.diary.toDomain(meals)
            }

        emit(filteredDiaries)
    }

    override fun getAllDiaries(): Flow<List<Diary>> = flow {
        val allDiariesWithMeals = diaryDao.getAllDiariesWithMeals().first()

        val diaries = allDiariesWithMeals.map { diaryWithMeals ->
            val meals = diaryWithMeals.meals.map { mealEntity ->
                val foodItems = mealFoodItemDao.getItemsWithDetailsByMealId(mealEntity.id).first()
                mealEntity.toDomain(foodItems)
            }
            diaryWithMeals.diary.toDomain(meals)
        }

        emit(diaries)
    }

    override suspend fun insertDiary(diary: Diary): Long {
        return diaryDao.insert(diary.toEntity())
    }

    override suspend fun updateDiary(diary: Diary) {
        diaryDao.update(diary.toEntity())
    }

    override suspend fun deleteDiary(diaryId: Long) {
        val diary = diaryDao.getById(diaryId)
        if (diary != null) {
            diaryDao.delete(diary)
        }
    }

    override suspend fun existsByDate(date: LocalDate): Boolean {
        return diaryDao.getByDate(date) != null
    }
}
