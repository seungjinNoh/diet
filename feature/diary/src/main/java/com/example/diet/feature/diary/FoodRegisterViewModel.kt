package com.example.diet.feature.diary

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.diet.core.data.repository.DiaryRepository
import com.example.diet.core.data.repository.FoodRepository
import com.example.diet.core.model.Food
import com.example.diet.core.model.MealType
import com.example.diet.core.model.NutritionInfo
import com.example.diet.core.navigation.FoodRegisterRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class FoodRegisterViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val foodRepository: FoodRepository,
    private val diaryRepository: DiaryRepository
) : ViewModel() {

    private val route = savedStateHandle.toRoute<FoodRegisterRoute>()
    val mealType = MealType.valueOf(route.mealType)
    val date: LocalDate = LocalDate.parse(route.date)

    val emoji = MutableStateFlow("🍽️")
    val name = MutableStateFlow("")
    val servingSize = MutableStateFlow("")   // 단위: g
    val carbs = MutableStateFlow("")
    val protein = MutableStateFlow("")
    val fat = MutableStateFlow("")
    val fiber = MutableStateFlow("")

    val autoCalories: StateFlow<Int?> = combine(carbs, protein, fat) { c, p, f ->
        val cv = c.toFloatOrNull() ?: return@combine null
        val pv = p.toFloatOrNull() ?: return@combine null
        val fv = f.toFloatOrNull() ?: return@combine null
        ((cv * 4) + (pv * 4) + (fv * 9)).toInt()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val canSave: StateFlow<Boolean> = combine(name, servingSize, carbs, protein, fat) { n, s, c, p, f ->
        n.isNotBlank() && s.toFloatOrNull() != null && c.toFloatOrNull() != null &&
        p.toFloatOrNull() != null && f.toFloatOrNull() != null
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    private val _savedFood = MutableStateFlow<Food?>(null)
    val savedFood = _savedFood.asStateFlow()

    fun saveAndAdd(onDone: () -> Unit) {
        viewModelScope.launch {
            val food = buildFood() ?: return@launch
            val foodId = foodRepository.insertFood(food)
            val savedFood = food.copy(id = foodId)
            diaryRepository.addFoodToMeal(date, mealType, savedFood, food.amount)
            _savedFood.value = savedFood
            onDone()
        }
    }

    fun saveOnly(onDone: () -> Unit) {
        viewModelScope.launch {
            val food = buildFood() ?: return@launch
            foodRepository.insertFood(food)
            onDone()
        }
    }

    private fun buildFood(): Food? {
        val calories = autoCalories.value ?: return null
        return Food(
            name = name.value.trim(),
            emoji = emoji.value,
            amount = servingSize.value.toFloatOrNull() ?: return null,
            calories = calories,
            nutrition = NutritionInfo(
                carbs = carbs.value.toFloatOrNull() ?: 0f,
                protein = protein.value.toFloatOrNull() ?: 0f,
                fat = fat.value.toFloatOrNull() ?: 0f,
                fiber = fiber.value.toFloatOrNull() ?: 0f
            )
        )
    }
}
