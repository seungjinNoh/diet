package com.example.diet.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.diet.core.model.DarkMode
import com.example.diet.core.model.NotificationSettings
import com.example.diet.core.model.NutritionGoals

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.isLoading) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA))
            .verticalScroll(rememberScrollState())
    ) {
        // Top Bar
        TopBar()

        Spacer(modifier = Modifier.height(12.dp))

        // Stats Row
        StatsRow(
            consecutiveDays = uiState.consecutiveDays,
            achievementRate = uiState.goalAchievementRate,
            foodsCount = uiState.registeredFoodsCount
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Goals Section
        SectionLabel("목표 설정")
        CalorieGoalSlider(
            currentGoal = uiState.nutritionGoals.dailyCalorie,
            onGoalChange = { viewModel.updateCalorieGoal(it) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        MacroGoalsCard(
            goals = uiState.nutritionGoals,
            onGoalsChange = { carb, protein, fat, fiber ->
                viewModel.updateMacroGoals(carb, protein, fat, fiber)
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Notifications Section
        SectionLabel("알림")
        NotificationsCard(
            settings = uiState.notificationSettings,
            onSettingsChange = { viewModel.updateNotificationSettings(it) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // App Settings Section
        SectionLabel("앱 설정")
        AppSettingsCard(
            darkMode = uiState.appSettings.darkMode,
            onDarkModeChange = { viewModel.updateDarkMode(it) }
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun TopBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Text(
            text = "설정",
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF1A1A1A)
        )
    }
}

@Composable
private fun StatsRow(
    consecutiveDays: Int,
    achievementRate: Int,
    foodsCount: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StatCard(
            value = "${consecutiveDays}일",
            label = "연속 기록",
            modifier = Modifier.weight(1f)
        )
        StatCard(
            value = "${achievementRate}%",
            label = "목표 달성률",
            modifier = Modifier.weight(1f)
        )
        StatCard(
            value = "${foodsCount}개",
            label = "등록된 음식",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatCard(value: String, label: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1A1A1A)
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFAAAAAA)
            )
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFFAAAAAA),
        letterSpacing = 0.8.sp,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
    )
}

@Composable
private fun CalorieGoalSlider(
    currentGoal: Int,
    onGoalChange: (Int) -> Unit
) {
    var sliderValue by remember(currentGoal) { mutableFloatStateOf(currentGoal.toFloat()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "일일 칼로리 목표",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = String.format("%,d", sliderValue.toInt()),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF4CAF50)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "kcal",
                        fontSize = 12.sp,
                        color = Color(0xFFAAAAAA)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Slider(
                value = sliderValue,
                onValueChange = { sliderValue = it },
                onValueChangeFinished = { onGoalChange(sliderValue.toInt()) },
                valueRange = 1200f..3500f,
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFF4CAF50),
                    activeTrackColor = Color(0xFF4CAF50),
                    inactiveTrackColor = Color(0xFFF0F0F0)
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "1,200 kcal",
                    fontSize = 10.sp,
                    color = Color(0xFFCCCCCC)
                )
                Text(
                    text = "3,500 kcal",
                    fontSize = 10.sp,
                    color = Color(0xFFCCCCCC)
                )
            }
        }
    }
}

@Composable
private fun MacroGoalsCard(
    goals: NutritionGoals,
    onGoalsChange: (Float, Float, Float, Float) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "매크로 목표",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            MacroGoalBar(
                name = "탄수",
                value = goals.carbohydrate.toInt(),
                unit = "g",
                color = Color(0xFFFF9800),
                progress = goals.carbohydrate / 300f
            )
            Spacer(modifier = Modifier.height(10.dp))
            MacroGoalBar(
                name = "단백",
                value = goals.protein.toInt(),
                unit = "g",
                color = Color(0xFF2196F3),
                progress = goals.protein / 150f
            )
            Spacer(modifier = Modifier.height(10.dp))
            MacroGoalBar(
                name = "지방",
                value = goals.fat.toInt(),
                unit = "g",
                color = Color(0xFFF44336),
                progress = goals.fat / 100f
            )
        }
    }
}

@Composable
private fun MacroGoalBar(
    name: String,
    value: Int,
    unit: String,
    color: Color,
    progress: Float
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = name,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF555555),
            modifier = Modifier.width(32.dp)
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFFF0F0F0))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress.coerceIn(0f, 1f))
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(color)
            )
        }

        Text(
            text = "$value$unit",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF888888),
            modifier = Modifier.width(40.dp)
        )
    }
}

@Composable
private fun NotificationsCard(
    settings: NotificationSettings,
    onSettingsChange: (NotificationSettings) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            SettingsItem(
                icon = "🔔",
                iconBackground = Color(0xFFE3F2FD),
                title = "식사 알림",
                subtitle = "아침 ${settings.breakfastTime ?: "-"} · 점심 ${settings.lunchTime ?: "-"} · 저녁 ${settings.dinnerTime ?: "-"}",
                trailing = {
                    Switch(
                        checked = settings.mealNotificationEnabled,
                        onCheckedChange = {
                            onSettingsChange(settings.copy(mealNotificationEnabled = it))
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF4CAF50),
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color(0xFFE0E0E0)
                        )
                    )
                }
            )

            HorizontalDivider(color = Color(0xFFF8F8F8), thickness = 1.dp)

            SettingsItem(
                icon = "📊",
                iconBackground = Color(0xFFF3E5F5),
                title = "주간 리포트",
                subtitle = "매주 일요일 오전 9시",
                trailing = {
                    Switch(
                        checked = settings.weeklyReportEnabled,
                        onCheckedChange = {
                            onSettingsChange(settings.copy(weeklyReportEnabled = it))
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF4CAF50),
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color(0xFFE0E0E0)
                        )
                    )
                }
            )

            HorizontalDivider(color = Color(0xFFF8F8F8), thickness = 1.dp)

            SettingsItem(
                icon = "🎯",
                iconBackground = Color(0xFFE8F5E9),
                title = "목표 초과 알림",
                subtitle = "칼로리 목표 초과 시",
                trailing = {
                    Switch(
                        checked = settings.goalExceededNotificationEnabled,
                        onCheckedChange = {
                            onSettingsChange(settings.copy(goalExceededNotificationEnabled = it))
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF4CAF50),
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color(0xFFE0E0E0)
                        )
                    )
                }
            )
        }
    }
}

@Composable
private fun AppSettingsCard(
    darkMode: DarkMode,
    onDarkModeChange: (DarkMode) -> Unit
) {
    var showDarkModeDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        SettingsItem(
            icon = "🌙",
            iconBackground = Color(0xFFF3E5F5),
            title = "다크 모드",
            subtitle = "시스템 설정 따름",
            trailing = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = when (darkMode) {
                            DarkMode.AUTO -> "자동"
                            DarkMode.LIGHT -> "라이트"
                            DarkMode.DARK -> "다크"
                        },
                        fontSize = 13.sp,
                        color = Color(0xFF888888),
                        fontWeight = FontWeight.Medium
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = null,
                        tint = Color(0xFFCCCCCC),
                        modifier = Modifier.size(16.dp)
                    )
                }
            },
            onClick = { showDarkModeDialog = true }
        )
    }

    // 다크 모드 선택 다이얼로그는 실제 구현에서는 별도로 작성 필요
}

@Composable
private fun SettingsItem(
    icon: String,
    iconBackground: Color,
    title: String,
    subtitle: String,
    trailing: @Composable () -> Unit,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .let { if (onClick != null) it.clickable(onClick = onClick) else it }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(iconBackground),
            contentAlignment = Alignment.Center
        ) {
            Text(text = icon, fontSize = 16.sp)
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1A1A)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = Color(0xFFAAAAAA)
            )
        }

        trailing()
    }
}
