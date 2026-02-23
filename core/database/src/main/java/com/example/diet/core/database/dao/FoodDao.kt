package com.example.diet.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.diet.core.database.entity.FoodEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(food: FoodEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(foods: List<FoodEntity>): List<Long>

    @Update
    suspend fun update(food: FoodEntity)

    @Delete
    suspend fun delete(food: FoodEntity)

    @Query("SELECT * FROM foods WHERE id = :id")
    suspend fun getById(id: Long): FoodEntity?

    @Query("SELECT * FROM foods ORDER BY name ASC")
    fun getAllFoods(): Flow<List<FoodEntity>>

    @Query("SELECT * FROM foods WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchFoods(query: String): Flow<List<FoodEntity>>
}
