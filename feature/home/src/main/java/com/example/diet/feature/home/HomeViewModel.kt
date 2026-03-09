package com.example.diet.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diet.core.data.repository.DiaryRepository
import com.example.diet.core.data.repository.UserProfileRepository
import com.example.diet.core.model.Diary
import com.example.diet.core.model.NutritionGoals
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(
        val userName: String,
        val today: LocalDate,
        val todayDiary: Diary?,
        val weeklyDiaries: List<Diary>,
        val goals: NutritionGoals
    ) : HomeUiState
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val diaryRepository: DiaryRepository,
    private val userProfileRepository: UserProfileRepository
) : ViewModel() {

    private val today = LocalDate.now()
    private val startOfWeek = today.with(DayOfWeek.MONDAY)
    private val endOfWeek = today.with(DayOfWeek.SUNDAY)

    val uiState: StateFlow<HomeUiState> = combine(
        diaryRepository.getDiaryByDate(today),
        diaryRepository.getDiariesByDateRange(startOfWeek, endOfWeek),
        userProfileRepository.getProfile()
    ) { todayDiary, weeklyDiaries, profile ->
        HomeUiState.Success(
            userName = profile?.name ?: "사용자",
            today = today,
            todayDiary = todayDiary,
            weeklyDiaries = weeklyDiaries,
            goals = profile?.goals ?: NutritionGoals()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState.Loading
    )
}
