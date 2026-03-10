package com.example.diet.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.diet.core.database.entity.MealEntity
import com.example.diet.core.database.model.MealWithFoodItems
import com.example.diet.core.model.MealType
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(meal: MealEntity): Long

    @Update
    suspend fun update(meal: MealEntity)

    @Delete
    suspend fun delete(meal: MealEntity)

    @Query("SELECT * FROM meals WHERE id = :id")
    suspend fun getById(id: Long): MealEntity?

    @Query("SELECT * FROM meals WHERE diaryId = :diaryId")
    fun getMealsByDiaryId(diaryId: Long): Flow<List<MealEntity>>

    @Query("SELECT * FROM meals WHERE diaryId = :diaryId AND mealType = :mealType LIMIT 1")
    suspend fun getMealByDiaryIdAndType(diaryId: Long, mealType: MealType): MealEntity?

    @Transaction
    @Query("SELECT * FROM meals WHERE id = :id")
    suspend fun getMealWithFoodItems(id: Long): MealWithFoodItems?

    @Transaction
    @Query("SELECT * FROM meals WHERE diaryId = :diaryId")
    fun getMealsWithFoodItemsByDiaryId(diaryId: Long): Flow<List<MealWithFoodItems>>
}
