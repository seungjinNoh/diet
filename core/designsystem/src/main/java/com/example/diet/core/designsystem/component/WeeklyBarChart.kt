package com.example.diet.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diet.core.designsystem.theme.DietDivider
import com.example.diet.core.designsystem.theme.DietGreen
import com.example.diet.core.designsystem.theme.DietTextLight

data class DayBarData(
    val label: String,
    val calories: Int,
    val isToday: Boolean,
    val calorieGoal: Int
)

@Composable
fun WeeklyBarChart(
    bars: List<DayBarData>,
    chartHeight: Dp = 80.dp,
    modifier: Modifier = Modifier
) {
    val maxCalories = bars.maxOfOrNull { it.calories }.takeIf { it != null && it > 0 }
        ?: bars.firstOrNull()?.calorieGoal ?: 2000

    Row(
        modifier = modifier.fillMaxWidth(),
    ) {
        bars.forEachIndexed { index, bar ->
            if (index > 0) Spacer(modifier = Modifier.width(6.dp))

            val fraction = if (maxCalories > 0) {
                (bar.calories.toFloat() / maxCalories).coerceIn(0f, 1f)
            } else 0f

            val barColor = when {
                bar.calories == 0 -> DietDivider
                bar.isToday -> DietGreen
                bar.calories > bar.calorieGoal -> Color(0xFFFF5722)
                else -> DietGreen
            }

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(chartHeight),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    val barHeight = if (fraction > 0f) chartHeight * fraction else 4.dp
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(barHeight)
                            .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                            .background(barColor)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = bar.label,
                    fontSize = 10.sp,
                    color = if (bar.isToday) DietGreen else DietTextLight,
                    fontWeight = if (bar.isToday) FontWeight.Bold else FontWeight.Medium
                )
            }
        }
    }
}
