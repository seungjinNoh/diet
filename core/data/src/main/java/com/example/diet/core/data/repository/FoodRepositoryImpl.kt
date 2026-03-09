package com.example.diet.core.data.repository

import com.example.diet.core.data.mapper.toDomain
import com.example.diet.core.data.mapper.toEntity
import com.example.diet.core.database.dao.FoodDao
import com.example.diet.core.data.repository.FoodRepository
import com.example.diet.core.model.Food
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FoodRepositoryImpl @Inject constructor(
    private val foodDao: FoodDao
) : FoodRepository {

    override fun getAllFoods(): Flow<List<Food>> {
        return foodDao.getAllFoods().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getFoodById(foodId: Long): Flow<Food?> {
        return foodDao.getAllFoods().map { entities ->
            entities.find { it.id == foodId }?.toDomain()
        }
    }

    override fun searchFoodsByName(query: String): Flow<List<Food>> {
        return foodDao.searchFoods(query).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertFood(food: Food): Long {
        return foodDao.insert(food.toEntity())
    }

    override suspend fun insertFoods(foods: List<Food>) {
        foodDao.insertAll(foods.map { it.toEntity() })
    }

    override suspend fun updateFood(food: Food) {
        foodDao.update(food.toEntity())
    }

    override suspend fun deleteFood(foodId: Long) {
        val food = foodDao.getById(foodId)
        if (food != null) {
            foodDao.delete(food)
        }
    }
}
