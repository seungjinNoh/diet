package com.example.diet.core.domain.repository

import com.example.diet.core.model.Food
import kotlinx.coroutines.flow.Flow

interface FoodRepository {
    /**
     * 모든 음식 데이터를 조회합니다.
     */
    fun getAllFoods(): Flow<List<Food>>

    /**
     * 특정 ID의 음식을 조회합니다.
     */
    fun getFoodById(foodId: Long): Flow<Food?>

    /**
     * 음식 이름으로 검색합니다.
     */
    fun searchFoodsByName(query: String): Flow<List<Food>>

    /**
     * 새로운 음식을 추가합니다.
     * @return 생성된 Food의 ID
     */
    suspend fun insertFood(food: Food): Long

    /**
     * 여러 음식을 한 번에 추가합니다.
     */
    suspend fun insertFoods(foods: List<Food>)

    /**
     * 기존 음식 정보를 업데이트합니다.
     */
    suspend fun updateFood(food: Food)

    /**
     * 음식을 삭제합니다.
     */
    suspend fun deleteFood(foodId: Long)
}
