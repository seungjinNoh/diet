package com.example.diet.core.database.di

import android.content.Context
import androidx.room.Room
import com.example.diet.core.database.DietDatabase
import com.example.diet.core.database.dao.DiaryDao
import com.example.diet.core.database.dao.FoodDao
import com.example.diet.core.database.dao.MealDao
import com.example.diet.core.database.dao.MealFoodItemDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDietDatabase(
        @ApplicationContext context: Context
    ): DietDatabase {
        return Room.databaseBuilder(
            context,
            DietDatabase::class.java,
            "diet_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideDiaryDao(database: DietDatabase): DiaryDao {
        return database.diaryDao()
    }

    @Provides
    @Singleton
    fun provideMealDao(database: DietDatabase): MealDao {
        return database.mealDao()
    }

    @Provides
    @Singleton
    fun provideFoodDao(database: DietDatabase): FoodDao {
        return database.foodDao()
    }

    @Provides
    @Singleton
    fun provideMealFoodItemDao(database: DietDatabase): MealFoodItemDao {
        return database.mealFoodItemDao()
    }
}
