package com.example.diet.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.diet.core.database.dao.DiaryDao
import com.example.diet.core.database.dao.FoodDao
import com.example.diet.core.database.dao.MealDao
import com.example.diet.core.database.dao.MealFoodItemDao
import com.example.diet.core.database.entity.DiaryEntity
import com.example.diet.core.database.entity.FoodEntity
import com.example.diet.core.database.entity.MealEntity
import com.example.diet.core.database.entity.MealFoodItem
import com.example.diet.core.database.util.DateConverter

@Database(
    entities = [
        DiaryEntity::class,
        MealEntity::class,
        FoodEntity::class,
        MealFoodItem::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class DietDatabase : RoomDatabase() {
    abstract fun diaryDao(): DiaryDao
    abstract fun mealDao(): MealDao
    abstract fun foodDao(): FoodDao
    abstract fun mealFoodItemDao(): MealFoodItemDao
}
