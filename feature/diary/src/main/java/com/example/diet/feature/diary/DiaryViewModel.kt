package com.example.diet.feature.diary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diet.core.data.repository.DiaryRepository
import com.example.diet.core.data.repository.FoodRepository
import com.example.diet.core.model.Diary
import com.example.diet.core.model.Food
import com.example.diet.core.model.MealType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject

sealed interface DiaryUiState {
    data object Loading : DiaryUiState
    data class Success(
        val selectedDate: LocalDate,
        val weekDates: List<LocalDate>,
        val diary: Diary?
    ) : DiaryUiState
}

sealed interface DiaryBottomSheetState {
    data object Hidden : DiaryBottomSheetState
    data class FoodSearch(val mealType: MealType) : DiaryBottomSheetState
    data class AmountInput(val food: Food, val mealType: MealType, val amount: Float = 100f) : DiaryBottomSheetState
}

@HiltViewModel
class DiaryViewModel @Inject constructor(
    private val diaryRepository: DiaryRepository,
    private val foodRepository: FoodRepository
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    private val _refreshTrigger = MutableStateFlow(0)
    private val _searchQuery = MutableStateFlow("")
    val bottomSheetState = MutableStateFlow<DiaryBottomSheetState>(DiaryBottomSheetState.Hidden)
    private val _lastAddedFoodName = MutableStateFlow<String?>(null)
    val lastAddedFoodName = _lastAddedFoodName.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<DiaryUiState> = combine(_selectedDate, _refreshTrigger) { date, _ -> date }
        .flatMapLatest { date ->
            diaryRepository.getDiaryByDate(date).map { diary ->
                DiaryUiState.Success(
                    selectedDate = date,
                    weekDates = getWeekDates(date),
                    diary = diary
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DiaryUiState.Loading)

    @OptIn(ExperimentalCoroutinesApi::class)
    val searchResults: StateFlow<List<Food>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) foodRepository.getAllFoods()
            else foodRepository.searchFoodsByName(query)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun onDateSelected(date: LocalDate) { _selectedDate.value = date }

    fun onMealAddClick(mealType: MealType) {
        _searchQuery.value = ""
        bottomSheetState.value = DiaryBottomSheetState.FoodSearch(mealType)
    }

    fun onSearchQueryChange(query: String) { _searchQuery.value = query }

    fun onFoodSelected(food: Food, mealType: MealType) {
        bottomSheetState.value = DiaryBottomSheetState.AmountInput(food, mealType)
    }

    fun onAmountChanged(delta: Float) {
        val current = bottomSheetState.value as? DiaryBottomSheetState.AmountInput ?: return
        val newAmount = (current.amount + delta).coerceIn(5f, 1000f)
        bottomSheetState.value = current.copy(amount = newAmount)
    }

    fun onConfirmAdd() {
        val current = bottomSheetState.value as? DiaryBottomSheetState.AmountInput ?: return
        val date = _selectedDate.value
        viewModelScope.launch {
            diaryRepository.addFoodToMeal(date, current.mealType, current.food, current.amount)
            _lastAddedFoodName.value = current.food.name
            bottomSheetState.value = DiaryBottomSheetState.Hidden
            _refreshTrigger.value++
        }
    }

    fun onBottomSheetDismiss() { bottomSheetState.value = DiaryBottomSheetState.Hidden }

    fun clearLastAddedFoodName() { _lastAddedFoodName.value = null }

    private fun getWeekDates(date: LocalDate): List<LocalDate> {
        val monday = date.with(DayOfWeek.MONDAY)
        return (0..6).map { monday.plusDays(it.toLong()) }
    }
}
