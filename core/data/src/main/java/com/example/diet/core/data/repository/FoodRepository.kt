package com.example.diet.core.data.repository

import com.example.diet.core.model.Food
import kotlinx.coroutines.flow.Flow

interface FoodRepository {
    fun getAllFoods(): Flow<List<Food>>
    fun getFoodById(foodId: Long): Flow<Food?>
    fun searchFoodsByName(query: String): Flow<List<Food>>
    suspend fun insertFood(food: Food): Long
    suspend fun insertFoods(foods: List<Food>)
    suspend fun updateFood(food: Food)
    suspend fun deleteFood(foodId: Long)
}
