package com.example.diet.feature.analysis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diet.core.data.repository.DiaryRepository
import com.example.diet.core.data.repository.UserProfileRepository
import com.example.diet.core.designsystem.component.DayBarData
import com.example.diet.core.model.Diary
import com.example.diet.core.model.NutritionGoals
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject
import kotlin.math.roundToInt

// ──────────────────────────────────────────────
// UiState 데이터 클래스
// ──────────────────────────────────────────────

data class MacroUiData(val name: String, val grams: Float, val percent: Int)

data class TopFoodUiData(
    val rank: Int,
    val emoji: String,
    val name: String,
    val count: Int,
    val barFraction: Float
)

enum class CalorieStatus { NoData, Low, Mid, Achieved, Over }

data class HeatmapCellData(val day: Int, val status: CalorieStatus)

data class WeeklyUiData(
    val avgCalories: Int,
    val achievedDays: Int,
    val totalDays: Int,
    val totalCalories: Int,
    val calorieGoal: Int,
    val barData: List<DayBarData>,
    val macros: List<MacroUiData>,
    val topFoods: List<TopFoodUiData>
)

data class MonthlyUiData(
    val yearMonthLabel: String,
    val startWeekDayOffset: Int, // 일~토 그리드에서 1일 앞의 빈 칸 수
    val cells: List<HeatmapCellData>
)

sealed interface AnalysisUiState {
    data object Loading : AnalysisUiState
    data class Success(
        val weekly: WeeklyUiData,
        val monthly: MonthlyUiData
    ) : AnalysisUiState
}

// ──────────────────────────────────────────────
// ViewModel
// ──────────────────────────────────────────────

@HiltViewModel
class AnalysisViewModel @Inject constructor(
    private val diaryRepository: DiaryRepository,
    private val userProfileRepository: UserProfileRepository
) : ViewModel() {

    private val today = LocalDate.now()
    private val startOfWeek = today.with(DayOfWeek.MONDAY)
    private val endOfWeek = today.with(DayOfWeek.SUNDAY)
    private val startOfMonth = today.withDayOfMonth(1)
    private val endOfMonth = today.with(TemporalAdjusters.lastDayOfMonth())

    val uiState: StateFlow<AnalysisUiState> = combine(
        diaryRepository.getDiariesByDateRange(startOfWeek, endOfWeek),
        diaryRepository.getDiariesByDateRange(startOfMonth, endOfMonth),
        userProfileRepository.getProfile()
    ) { weeklyDiaries, monthlyDiaries, profile ->
        val goals = profile?.goals ?: NutritionGoals()
        AnalysisUiState.Success(
            weekly = buildWeeklyData(weeklyDiaries, goals),
            monthly = buildMonthlyData(monthlyDiaries, goals)
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AnalysisUiState.Loading
    )

    private fun buildWeeklyData(diaries: List<Diary>, goals: NutritionGoals): WeeklyUiData {
        val diaryByDate = diaries.associateBy { it.date }
        val dayLabels = listOf("월", "화", "수", "목", "금", "토", "일")

        val barData = (0..6).map { offset ->
            val date = startOfWeek.plusDays(offset.toLong())
            val calories = diaryByDate[date]?.totalCalories ?: 0
            DayBarData(
                label = if (date == today) "오늘" else dayLabels[offset],
                calories = calories,
                isToday = date == today,
                calorieGoal = goals.dailyCalorie
            )
        }

        val daysWithData = diaries.filter { it.totalCalories > 0 }
        val totalCalories = diaries.sumOf { it.totalCalories }
        val avgCalories = if (daysWithData.isNotEmpty()) totalCalories / daysWithData.size else 0
        // 목표 이하 달성 (초과하지 않은 날)
        val achievedDays = daysWithData.count { it.totalCalories <= goals.dailyCalorie }

        return WeeklyUiData(
            avgCalories = avgCalories,
            achievedDays = achievedDays,
            totalDays = 7,
            totalCalories = totalCalories,
            calorieGoal = goals.dailyCalorie,
            barData = barData,
            macros = buildMacros(daysWithData),
            topFoods = buildTopFoods(diaries)
        )
    }

    private fun buildMacros(diaries: List<Diary>): List<MacroUiData> {
        if (diaries.isEmpty()) {
            return listOf(
                MacroUiData("탄수화물", 0f, 0),
                MacroUiData("단백질", 0f, 0),
                MacroUiData("지방", 0f, 0)
            )
        }
        val count = diaries.size.toFloat()
        val avgCarbs = diaries.sumOf { it.totalNutrition.carbs.toDouble() }.toFloat() / count
        val avgProtein = diaries.sumOf { it.totalNutrition.protein.toDouble() }.toFloat() / count
        val avgFat = diaries.sumOf { it.totalNutrition.fat.toDouble() }.toFloat() / count

        val carbCals = avgCarbs * 4f
        val proteinCals = avgProtein * 4f
        val fatCals = avgFat * 9f
        val total = carbCals + proteinCals + fatCals

        return if (total > 0f) {
            listOf(
                MacroUiData("탄수화물", avgCarbs, (carbCals / total * 100).roundToInt()),
                MacroUiData("단백질", avgProtein, (proteinCals / total * 100).roundToInt()),
                MacroUiData("지방", avgFat, (fatCals / total * 100).roundToInt())
            )
        } else {
            listOf(
                MacroUiData("탄수화물", 0f, 0),
                MacroUiData("단백질", 0f, 0),
                MacroUiData("지방", 0f, 0)
            )
        }
    }

    private fun buildTopFoods(diaries: List<Diary>): List<TopFoodUiData> {
        // 음식 이름 기준으로 등장 횟수 집계
        val counts = mutableMapOf<String, Pair<String, Int>>() // name -> (emoji, count)
        diaries.forEach { diary ->
            diary.meals.forEach { meal ->
                meal.foods.forEach { food ->
                    val prev = counts[food.name]
                    counts[food.name] = Pair(food.emoji, (prev?.second ?: 0) + 1)
                }
            }
        }

        val sorted = counts.entries.sortedByDescending { it.value.second }
        val maxCount = sorted.firstOrNull()?.value?.second?.toFloat() ?: 1f

        return sorted.take(5).mapIndexed { index, (name, emojiAndCount) ->
            val (emoji, count) = emojiAndCount
            TopFoodUiData(
                rank = index + 1,
                emoji = emoji,
                name = name,
                count = count,
                barFraction = count / maxCount
            )
        }
    }

    private fun buildMonthlyData(diaries: List<Diary>, goals: NutritionGoals): MonthlyUiData {
        val firstDay = today.withDayOfMonth(1)
        val lastDayNum = today.with(TemporalAdjusters.lastDayOfMonth()).dayOfMonth

        // DayOfWeek: MON=1 ~ SUN=7 → 일~토 그리드 offset: SUN→0, MON→1, ..., SAT→6
        val startOffset = firstDay.dayOfWeek.value % 7

        val diaryByDate = diaries.associateBy { it.date }

        val cells = (1..lastDayNum).map { day ->
            val date = today.withDayOfMonth(day)
            val diary = diaryByDate[date]
            val status = when {
                date > today -> CalorieStatus.NoData
                diary == null || diary.totalCalories == 0 -> CalorieStatus.NoData
                diary.totalCalories > goals.dailyCalorie * 1.15f -> CalorieStatus.Over
                diary.totalCalories >= goals.dailyCalorie * 0.9f -> CalorieStatus.Achieved
                diary.totalCalories >= goals.dailyCalorie * 0.6f -> CalorieStatus.Mid
                else -> CalorieStatus.Low
            }
            HeatmapCellData(day = day, status = status)
        }

        return MonthlyUiData(
            yearMonthLabel = "${today.year}년 ${today.monthValue}월",
            startWeekDayOffset = startOffset,
            cells = cells
        )
    }
}
