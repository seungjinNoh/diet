package com.example.diet.feature.analysis

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.diet.core.designsystem.component.DietSectionCard
import com.example.diet.core.designsystem.component.WeeklyBarChart
import com.example.diet.core.designsystem.theme.DietBackground
import com.example.diet.core.designsystem.theme.DietBlue
import com.example.diet.core.designsystem.theme.DietDivider
import com.example.diet.core.designsystem.theme.DietGreen
import com.example.diet.core.designsystem.theme.DietOrange
import com.example.diet.core.designsystem.theme.DietRed
import com.example.diet.core.designsystem.theme.DietSurface
import com.example.diet.core.designsystem.theme.DietTextLight
import com.example.diet.core.designsystem.theme.DietTextMuted
import com.example.diet.core.designsystem.theme.DietTextPrimary
import java.time.LocalDate

private val macroColors = listOf(DietOrange, DietBlue, DietRed)

@Composable
fun AnalysisScreen(
    modifier: Modifier = Modifier,
    viewModel: AnalysisViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is AnalysisUiState.Loading -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = DietGreen)
            }
        }
        is AnalysisUiState.Success -> {
            AnalysisContent(state = state, modifier = modifier)
        }
    }
}

@Composable
private fun AnalysisContent(state: AnalysisUiState.Success, modifier: Modifier = Modifier) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val today = LocalDate.now()
    val monthLabel = "${today.monthValue}월"

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DietBackground)
    ) {
        AnalysisTopBar(monthLabel = monthLabel)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            AnalysisTabSelector(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )

            if (selectedTab == 0) {
                WeeklyContent(weekly = state.weekly, monthly = state.monthly)
            } else {
                MonthlyContent(monthly = state.monthly)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ──────────────────────────────────────────────
// 상단 바
// ──────────────────────────────────────────────

@Composable
private fun AnalysisTopBar(monthLabel: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(DietSurface)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "영양 분석",
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = DietTextPrimary
        )
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFFE8F5E9))
                .padding(horizontal = 12.dp, vertical = 5.dp)
        ) {
            Text(text = monthLabel, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DietGreen)
        }
    }
}

// ──────────────────────────────────────────────
// 탭 셀렉터
// ──────────────────────────────────────────────

@Composable
private fun AnalysisTabSelector(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(DietSurface)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        listOf("주간", "월간").forEachIndexed { index, label ->
            val isSelected = selectedTab == index
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) DietGreen else Color.Transparent)
                    .clickable { onTabSelected(index) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isSelected) Color.White else DietTextLight
                )
            }
        }
    }
}

// ──────────────────────────────────────────────
// 주간 탭 콘텐츠
// ──────────────────────────────────────────────

@Composable
private fun WeeklyContent(weekly: WeeklyUiData, monthly: MonthlyUiData) {
    SummaryRow(weekly = weekly)
    WeeklyCalorieSection(weekly = weekly)
    MacroPieSection(weekly = weekly)
    TopFoodsSection(weekly = weekly)
    HeatmapSection(monthly = monthly)
}

// ──────────────────────────────────────────────
// 월간 탭 콘텐츠
// ──────────────────────────────────────────────

@Composable
private fun MonthlyContent(monthly: MonthlyUiData) {
    HeatmapSection(monthly = monthly)
}

// ──────────────────────────────────────────────
// 요약 카드 행
// ──────────────────────────────────────────────

@Composable
private fun SummaryRow(weekly: WeeklyUiData) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        SummaryCard(
            label = "평균 칼로리",
            value = "%,d".format(weekly.avgCalories),
            unit = "kcal / 일",
            valueColor = DietGreen,
            modifier = Modifier.weight(1f)
        )
        SummaryCard(
            label = "목표 달성",
            value = "${weekly.achievedDays}/${weekly.totalDays}",
            unit = "일 달성",
            valueColor = DietOrange,
            modifier = Modifier.weight(1f)
        )
        SummaryCard(
            label = "총 섭취",
            value = "%,d".format(weekly.totalCalories),
            unit = "kcal 이번주",
            valueColor = DietTextPrimary,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SummaryCard(
    label: String,
    value: String,
    unit: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(DietSurface)
            .padding(horizontal = 12.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = label, fontSize = 11.sp, color = DietTextLight, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = valueColor)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = unit, fontSize = 11.sp, color = DietTextMuted)
        }
    }
}

// ──────────────────────────────────────────────
// 주간 칼로리 바 차트
// ──────────────────────────────────────────────

@Composable
private fun WeeklyCalorieSection(weekly: WeeklyUiData) {
    DietSectionCard(modifier = Modifier.padding(horizontal = 16.dp)) {
        SectionHeader(title = "주간 칼로리", sub = "목표 %,d kcal".format(weekly.calorieGoal))
        WeeklyBarChart(bars = weekly.barData, chartHeight = 100.dp)
    }
    Spacer(modifier = Modifier.height(12.dp))
}

// ──────────────────────────────────────────────
// 매크로 도넛 차트
// ──────────────────────────────────────────────

@Composable
private fun MacroPieSection(weekly: WeeklyUiData) {
    DietSectionCard(modifier = Modifier.padding(horizontal = 16.dp)) {
        SectionHeader(title = "매크로 비율", sub = "이번 주 평균")
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(modifier = Modifier.size(100.dp), contentAlignment = Alignment.Center) {
                MacroDonutChart(macros = weekly.macros)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "%,d".format(weekly.avgCalories),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = DietTextPrimary
                    )
                    Text(text = "kcal", fontSize = 10.sp, color = DietTextLight)
                }
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                weekly.macros.forEachIndexed { index, macro ->
                    MacroLegendItem(macro = macro, color = macroColors.getOrElse(index) { DietGreen })
                }
            }
        }
    }
    Spacer(modifier = Modifier.height(12.dp))
}

@Composable
private fun MacroDonutChart(macros: List<MacroUiData>) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val strokeWidth = 12.dp.toPx()
        val diameter = size.minDimension - strokeWidth
        val topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f)
        val arcSize = Size(diameter, diameter)

        var startAngle = -90f
        macros.forEachIndexed { index, macro ->
            val sweepAngle = macro.percent / 100f * 360f
            drawArc(
                color = macroColors.getOrElse(index) { Color.Gray },
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
            )
            startAngle += sweepAngle
        }

        // 데이터 없을 때 회색 링
        if (macros.all { it.percent == 0 }) {
            drawArc(
                color = Color(0xFFF0F0F0),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
            )
        }
    }
}

@Composable
private fun MacroLegendItem(macro: MacroUiData, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(color)
        )
        Text(
            text = macro.name,
            fontSize = 12.sp,
            color = Color(0xFF555555),
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "%.0fg".format(macro.grams),
            fontSize = 11.sp,
            color = DietTextLight
        )
        Text(
            text = "${macro.percent}%",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

// ──────────────────────────────────────────────
// 자주 먹은 음식 TOP 5
// ──────────────────────────────────────────────

@Composable
private fun TopFoodsSection(weekly: WeeklyUiData) {
    DietSectionCard(modifier = Modifier.padding(horizontal = 16.dp)) {
        SectionHeader(title = "자주 먹은 음식 TOP 5", sub = "이번 주")

        if (weekly.topFoods.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "기록된 음식이 없어요", fontSize = 13.sp, color = DietTextLight)
            }
        } else {
            weekly.topFoods.forEachIndexed { index, food ->
                if (index > 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color(0xFFF8F8F8))
                    )
                }
                TopFoodItem(food = food)
            }
        }
    }
    Spacer(modifier = Modifier.height(12.dp))
}

@Composable
private fun TopFoodItem(food: TopFoodUiData) {
    val (rankBg, rankTextColor) = when (food.rank) {
        1 -> Color(0xFFFFF3E0) to Color(0xFFFF9800)
        2 -> Color(0xFFF5F5F5) to Color(0xFF9E9E9E)
        3 -> Color(0xFFEFEBE9) to Color(0xFF795548)
        else -> DietDivider to DietTextMuted
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(rankBg),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "${food.rank}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = rankTextColor)
        }
        Text(text = food.emoji, fontSize = 18.sp)
        Text(
            text = food.name,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF333333),
            modifier = Modifier.weight(1f)
        )
        Text(text = "${food.count}회", fontSize = 12.sp, color = DietTextLight, fontWeight = FontWeight.Medium)
        Box(
            modifier = Modifier
                .width(60.dp)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(DietDivider)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(food.barFraction)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(DietGreen)
            )
        }
    }
}

// ──────────────────────────────────────────────
// 월간 히트맵
// ──────────────────────────────────────────────

@Composable
private fun HeatmapSection(monthly: MonthlyUiData) {
    DietSectionCard(modifier = Modifier.padding(horizontal = 16.dp)) {
        SectionHeader(title = "월간 달성 히트맵", sub = monthly.yearMonthLabel)

        // 요일 헤더
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            listOf("일", "월", "화", "수", "목", "금", "토").forEach { label ->
                Text(
                    text = label,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 10.sp,
                    color = DietTextLight,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))

        // 빈 칸 + 날짜 셀을 합쳐서 7개씩 행 구성
        val emptyCells = List(monthly.startWeekDayOffset) { null }
        val allCells: List<HeatmapCellData?> = emptyCells + monthly.cells

        allCells.chunked(7).forEach { week ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                week.forEach { cell ->
                    HeatmapCell(cell = cell, modifier = Modifier.weight(1f))
                }
                // 마지막 행 패딩
                repeat(7 - week.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }

        // 범례
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            listOf(
                Color(0xFFC8E6C9) to "미달",
                DietGreen to "달성",
                Color(0xFFFF8A65) to "초과"
            ).forEach { (color, label) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(color)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = label, fontSize = 10.sp, color = DietTextLight)
                }
            }
        }
    }
    Spacer(modifier = Modifier.height(12.dp))
}

@Composable
private fun HeatmapCell(cell: HeatmapCellData?, modifier: Modifier = Modifier) {
    val (bg, textColor) = if (cell == null) {
        Color.Transparent to Color.Transparent
    } else {
        when (cell.status) {
            CalorieStatus.NoData -> Color(0xFFF5F5F5) to Color(0xFFDDDDDD)
            CalorieStatus.Low -> Color(0xFFC8E6C9) to Color(0xFF2E7D32)
            CalorieStatus.Mid -> Color(0xFF81C784) to Color(0xFF1B5E20)
            CalorieStatus.Achieved -> DietGreen to Color.White
            CalorieStatus.Over -> Color(0xFFFF8A65) to Color.White
        }
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(6.dp))
            .background(bg),
        contentAlignment = Alignment.Center
    ) {
        if (cell != null) {
            Text(
                text = "${cell.day}",
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = textColor
            )
        }
    }
}

// ──────────────────────────────────────────────
// 공통 섹션 헤더
// ──────────────────────────────────────────────

@Composable
private fun SectionHeader(title: String, sub: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = DietTextPrimary)
        Text(text = sub, fontSize = 12.sp, color = DietTextLight)
    }
}
