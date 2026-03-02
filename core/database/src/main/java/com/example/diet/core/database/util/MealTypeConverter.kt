package com.example.diet.core.database.util

import androidx.room.TypeConverter
import com.example.diet.core.model.MealType

class MealTypeConverter {
    @TypeConverter
    fun fromMealType(mealType: MealType): String {
        return mealType.name
    }

    @TypeConverter
    fun toMealType(value: String): MealType {
        return MealType.valueOf(value)
    }
}
