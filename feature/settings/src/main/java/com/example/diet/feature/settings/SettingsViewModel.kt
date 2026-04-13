package com.example.diet.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diet.core.data.repository.DiaryRepository
import com.example.diet.core.data.repository.FoodRepository
import com.example.diet.core.data.repository.UserProfileRepository
import com.example.diet.core.model.AppSettings
import com.example.diet.core.model.DarkMode
import com.example.diet.core.model.NotificationSettings
import com.example.diet.core.model.NutritionGoals
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
    private val diaryRepository: DiaryRepository,
    private val foodRepository: FoodRepository
) : ViewModel() {

    private val _errorMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<SettingsUiState> = combine(
        userProfileRepository.getProfile(),
        diaryRepository.getAllDiaries(),
        foodRepository.getAllFoods(),
        _errorMessage
    ) { profile, diaries, foods, error ->
        SettingsUiState(
            isLoading = false,
            errorMessage = error,
            consecutiveDays = calculateConsecutiveDays(diaries.map { it.date }),
            goalAchievementRate = calculateGoalAchievementRate(diaries, profile?.goals?.dailyCalorie ?: 2000),
            registeredFoodsCount = foods.size,
            nutritionGoals = profile?.goals ?: NutritionGoals(),
            notificationSettings = profile?.notifications ?: NotificationSettings(),
            appSettings = profile?.appSettings ?: AppSettings()
        )
    }.catch { e ->
        _errorMessage.value = e.message ?: "Unknown error"
        emit(SettingsUiState(isLoading = false, errorMessage = e.message))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState(isLoading = true)
    )

    fun updateCalorieGoal(goal: Int) {
        viewModelScope.launch {
            try {
                userProfileRepository.updateCalorieGoal(goal)
            } catch (e: Exception) {
                _errorMessage.value = "칼로리 목표 업데이트 실패"
            }
        }
    }

    fun updateMacroGoals(carb: Float, protein: Float, fat: Float, fiber: Float) {
        viewModelScope.launch {
            try {
                userProfileRepository.updateMacroGoals(carb, protein, fat, fiber)
            } catch (e: Exception) {
                _errorMessage.value = "매크로 목표 업데이트 실패"
            }
        }
    }

    fun updateNotificationSettings(settings: NotificationSettings) {
        viewModelScope.launch {
            try {
                val profile = userProfileRepository.getProfileOnce() ?: return@launch
                userProfileRepository.updateProfile(profile.copy(notifications = settings))
            } catch (e: Exception) {
                _errorMessage.value = "알림 설정 업데이트 실패"
            }
        }
    }

    fun updateDarkMode(darkMode: DarkMode) {
        viewModelScope.launch {
            try {
                val profile = userProfileRepository.getProfileOnce() ?: return@launch
                userProfileRepository.updateProfile(
                    profile.copy(appSettings = profile.appSettings.copy(darkMode = darkMode))
                )
            } catch (e: Exception) {
                _errorMessage.value = "다크 모드 설정 업데이트 실패"
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    private fun calculateConsecutiveDays(dates: List<LocalDate>): Int {
        if (dates.isEmpty()) return 0

        val sortedDates = dates.sortedDescending()
        val today = LocalDate.now()

        // 오늘 또는 어제부터 시작하지 않으면 0
        if (sortedDates.first() != today && sortedDates.first() != today.minusDays(1)) {
            return 0
        }

        var consecutive = 1
        for (i in 1 until sortedDates.size) {
            val diff = sortedDates[i - 1].toEpochDay() - sortedDates[i].toEpochDay()
            if (diff == 1L) {
                consecutive++
            } else {
                break
            }
        }

        return consecutive
    }

    private fun calculateGoalAchievementRate(diaries: List<com.example.diet.core.model.Diary>, calorieGoal: Int): Int {
        if (diaries.isEmpty() || calorieGoal <= 0) return 0

        val last30Days = diaries.takeLast(30)
        val achievedDays = last30Days.count { diary ->
            val totalCalories = diary.meals.sumOf { meal ->
                meal.foods.sumOf { it.calories }
            }
            totalCalories in (calorieGoal * 0.9).toInt()..(calorieGoal * 1.1).toInt()
        }

        return ((achievedDays.toFloat() / last30Days.size) * 100).toInt()
    }
}
