package com.example.diet.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.diet.core.database.entity.MealFoodItem
import com.example.diet.core.database.model.FoodItemWithDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface MealFoodItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: MealFoodItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<MealFoodItem>): List<Long>

    @Update
    suspend fun update(item: MealFoodItem)

    @Delete
    suspend fun delete(item: MealFoodItem)

    @Query("DELETE FROM meal_food_items WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM meal_food_items WHERE id = :id")
    suspend fun getById(id: Long): MealFoodItem?

    @Query("SELECT * FROM meal_food_items WHERE mealId = :mealId")
    fun getItemsByMealId(mealId: Long): Flow<List<MealFoodItem>>

    @Transaction
    @Query("SELECT * FROM meal_food_items WHERE mealId = :mealId")
    fun getItemsWithDetailsByMealId(mealId: Long): Flow<List<FoodItemWithDetails>>

    @Query("DELETE FROM meal_food_items WHERE mealId = :mealId")
    suspend fun deleteAllByMealId(mealId: Long)
}
