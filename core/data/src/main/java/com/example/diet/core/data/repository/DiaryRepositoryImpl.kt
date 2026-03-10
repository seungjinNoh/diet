package com.example.diet.core.data.repository

import com.example.diet.core.data.mapper.toDomain
import com.example.diet.core.data.mapper.toEntity
import com.example.diet.core.database.dao.DiaryDao
import com.example.diet.core.database.dao.FoodDao
import com.example.diet.core.database.dao.MealDao
import com.example.diet.core.database.dao.MealFoodItemDao
import com.example.diet.core.database.entity.DiaryEntity
import com.example.diet.core.database.entity.MealEntity
import com.example.diet.core.database.entity.MealFoodItem
import com.example.diet.core.database.entity.NutritionInfoEmbedded
import com.example.diet.core.data.repository.DiaryRepository
import com.example.diet.core.model.Diary
import com.example.diet.core.model.Food
import com.example.diet.core.model.MealType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.time.LocalDate
import javax.inject.Inject

class DiaryRepositoryImpl @Inject constructor(
    private val diaryDao: DiaryDao,
    private val mealFoodItemDao: MealFoodItemDao,
    private val mealDao: MealDao,
    private val foodDao: FoodDao
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

    override suspend fun addFoodToMeal(date: LocalDate, mealType: MealType, food: Food, amount: Float) {
        val scale = amount / food.amount
        val scaledCalories = (food.calories * scale).toInt()
        val scaledNutrition = NutritionInfoEmbedded(
            carbs = food.nutrition.carbs * scale,
            protein = food.nutrition.protein * scale,
            fat = food.nutrition.fat * scale,
            fiber = food.nutrition.fiber * scale
        )

        // 1. 해당 날짜의 DiaryEntity 조회 또는 생성
        val diaryEntity = diaryDao.getByDate(date) ?: run {
            val newDiary = DiaryEntity(date = date)
            val id = diaryDao.insert(newDiary)
            diaryDao.getById(id)!!
        }

        // 2. diary + mealType에 해당하는 MealEntity 조회 또는 생성
        val existingMeal = mealDao.getMealByDiaryIdAndType(diaryEntity.id, mealType)
        val mealId: Long
        if (existingMeal != null) {
            // 기존 식사 합계 업데이트
            mealDao.update(
                existingMeal.copy(
                    totalCalories = existingMeal.totalCalories + scaledCalories,
                    totalNutrition = NutritionInfoEmbedded(
                        carbs = existingMeal.totalNutrition.carbs + scaledNutrition.carbs,
                        protein = existingMeal.totalNutrition.protein + scaledNutrition.protein,
                        fat = existingMeal.totalNutrition.fat + scaledNutrition.fat,
                        fiber = existingMeal.totalNutrition.fiber + scaledNutrition.fiber
                    )
                )
            )
            mealId = existingMeal.id
        } else {
            // 계산된 합계로 새 식사 생성
            val newMeal = MealEntity(
                diaryId = diaryEntity.id,
                mealType = mealType,
                totalCalories = scaledCalories,
                totalNutrition = scaledNutrition
            )
            mealId = mealDao.insert(newMeal)
        }

        // 3. MealFoodItem 삽입
        mealFoodItemDao.insert(MealFoodItem(mealId = mealId, foodId = food.id, amount = amount))

        // 4. DiaryEntity 합계 업데이트
        diaryDao.update(
            diaryEntity.copy(
                totalCalories = diaryEntity.totalCalories + scaledCalories,
                totalNutrition = NutritionInfoEmbedded(
                    carbs = diaryEntity.totalNutrition.carbs + scaledNutrition.carbs,
                    protein = diaryEntity.totalNutrition.protein + scaledNutrition.protein,
                    fat = diaryEntity.totalNutrition.fat + scaledNutrition.fat,
                    fiber = diaryEntity.totalNutrition.fiber + scaledNutrition.fiber
                )
            )
        )
    }
}
