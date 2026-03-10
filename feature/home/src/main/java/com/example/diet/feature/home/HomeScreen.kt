package com.example.diet.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.diet.core.designsystem.component.AchievementChip
import com.example.diet.core.designsystem.component.CalorieRing
import com.example.diet.core.designsystem.component.DayBarData
import com.example.diet.core.designsystem.component.DietSectionCard
import com.example.diet.core.designsystem.component.MacroBarRow
import com.example.diet.core.designsystem.component.MealAddCard
import com.example.diet.core.designsystem.component.MealCard
import com.example.diet.core.designsystem.component.WeeklyBarChart
import com.example.diet.core.designsystem.theme.DietBackground
import com.example.diet.core.designsystem.theme.DietBlue
import com.example.diet.core.designsystem.theme.DietDivider
import com.example.diet.core.designsystem.theme.DietGreen
import com.example.diet.core.designsystem.theme.DietOrange
import com.example.diet.core.designsystem.theme.DietPurple
import com.example.diet.core.designsystem.theme.DietRed
import com.example.diet.core.designsystem.theme.DietSurface
import com.example.diet.core.designsystem.theme.DietTextLight
import com.example.diet.core.designsystem.theme.DietTextMuted
import com.example.diet.core.designsystem.theme.DietTextPrimary
import com.example.diet.core.model.Diary
import com.example.diet.core.model.MealType
import com.example.diet.core.model.NutritionGoals
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onNavigateToDiary: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is HomeUiState.Loading -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = DietGreen)
            }
        }
        is HomeUiState.Success -> {
            HomeContent(state = state, modifier = modifier, onNavigateToDiary = onNavigateToDiary)
        }
    }
}

@Composable
private fun HomeContent(state: HomeUiState.Success, modifier: Modifier = Modifier, onNavigateToDiary: () -> Unit = {}) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DietBackground)
            .verticalScroll(rememberScrollState())
    ) {
        HomeTopBar(userName = state.userName, today = state.today)
        Spacer(modifier = Modifier.height(12.dp))
        CalorieRingCard(diary = state.todayDiary, goals = state.goals)
        Spacer(modifier = Modifier.height(12.dp))
        MacroBarsCard(diary = state.todayDiary, goals = state.goals)
        Spacer(modifier = Modifier.height(12.dp))
        MealsSection(diary = state.todayDiary, onMealAddClick = onNavigateToDiary)
        Spacer(modifier = Modifier.height(12.dp))
        WeeklyChartCard(
            weeklyDiaries = state.weeklyDiaries,
            today = state.today,
            calorieGoal = state.goals.dailyCalorie
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun HomeTopBar(userName: String, today: LocalDate) {
    val greeting = if (today.dayOfWeek == DayOfWeek.MONDAY) "새로운 한 주 시작이에요 👋" else "좋은 하루예요 👋"
    val dateText = today.format(DateTimeFormatter.ofPattern("M월 d일 EEEE", Locale.KOREAN))
    val initial = userName.firstOrNull()?.uppercaseChar()?.toString() ?: "U"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(DietSurface)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = greeting, fontSize = 13.sp, color = DietTextMuted)
            Text(text = dateText, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DietTextPrimary)
        }
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(colors = listOf(Color(0xFF4CAF50), Color(0xFF8BC34A)))),
            contentAlignment = Alignment.Center
        ) {
            Text(text = initial, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
private fun CalorieRingCard(diary: Diary?, goals: NutritionGoals) {
    val consumed = diary?.totalCalories ?: 0
    val goal = goals.dailyCalorie
    val remaining = (goal - consumed).coerceAtLeast(0)
    val progress = if (goal > 0) (consumed.toFloat() / goal).coerceIn(0f, 1f) else 0f
    val percentage = (progress * 100).toInt()

    DietSectionCard(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = "오늘의 칼로리",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = DietTextMuted,
            letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            CalorieRing(
                consumed = consumed,
                progress = progress,
                modifier = Modifier.size(120.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CalorieItem(label = "섭취", value = "%,d".format(consumed), valueColor = DietGreen)
                    Box(modifier = Modifier.width(1.dp).height(32.dp).background(DietDivider))
                    CalorieItem(label = "목표", value = "%,d".format(goal), valueColor = DietTextPrimary)
                    Box(modifier = Modifier.width(1.dp).height(32.dp).background(DietDivider))
                    CalorieItem(label = "남은", value = "%,d".format(remaining), valueColor = DietOrange)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    AchievementChip(text = "목표의 $percentage% 달성")
                }
            }
        }
    }
}

@Composable
private fun CalorieItem(label: String, value: String, valueColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = valueColor)
        Text(text = label, fontSize = 11.sp, color = DietTextLight, modifier = Modifier.padding(top = 2.dp))
    }
}

@Composable
private fun MacroBarsCard(diary: Diary?, goals: NutritionGoals) {
    val nutrition = diary?.totalNutrition

    DietSectionCard(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = "영양소",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = DietTextMuted,
            letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        MacroBarRow(name = "탄수", current = nutrition?.carbs ?: 0f, goal = goals.carbohydrate, color = DietOrange)
        Spacer(modifier = Modifier.height(12.dp))
        MacroBarRow(name = "단백", current = nutrition?.protein ?: 0f, goal = goals.protein, color = DietBlue)
        Spacer(modifier = Modifier.height(12.dp))
        MacroBarRow(name = "지방", current = nutrition?.fat ?: 0f, goal = goals.fat, color = DietRed)
        Spacer(modifier = Modifier.height(12.dp))
        MacroBarRow(name = "식이", current = nutrition?.fiber ?: 0f, goal = goals.fiber, color = DietPurple)
    }
}

@Composable
private fun MealsSection(diary: Diary?, onMealAddClick: () -> Unit = {}) {
    val mealsMap = diary?.meals?.associateBy { it.mealType } ?: emptyMap()

    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "끼니 기록", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = DietTextPrimary)
            Text(text = "전체보기", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DietGreen)
        }
        Spacer(modifier = Modifier.height(10.dp))
        MealType.entries.forEachIndexed { index, mealType ->
            if (index > 0) Spacer(modifier = Modifier.height(8.dp))
            val meal = mealsMap[mealType]
            if (meal != null) {
                MealCard(meal = meal)
            } else {
                MealAddCard(mealName = "${mealType.displayName} 기록하기", onClick = onMealAddClick)
            }
        }
    }
}

@Composable
private fun WeeklyChartCard(weeklyDiaries: List<Diary>, today: LocalDate, calorieGoal: Int) {
    val startOfWeek = today.with(DayOfWeek.MONDAY)
    val diaryByDate = weeklyDiaries.associateBy { it.date }
    val dayLabels = listOf("월", "화", "수", "목", "금", "토", "일")

    val bars = (0..6).map { offset ->
        val date = startOfWeek.plusDays(offset.toLong())
        DayBarData(
            label = if (date == today) "오늘" else dayLabels[offset],
            calories = diaryByDate[date]?.totalCalories ?: 0,
            isToday = date == today,
            calorieGoal = calorieGoal
        )
    }

    val recordedDays = bars.count { it.calories > 0 }
    val avgCalories = if (recordedDays > 0) bars.sumOf { it.calories } / recordedDays else 0

    DietSectionCard(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "이번 주 칼로리", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = DietTextPrimary)
            AchievementChip(
                text = if (avgCalories > 0) "평균 %,d kcal".format(avgCalories) else "기록 없음",
                color = DietTextMuted,
                backgroundColor = Color(0xFFF5F5F5)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        WeeklyBarChart(bars = bars)
    }
}
