package com.example.diet.core.designsystem.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.diet.core.designsystem.theme.DietDivider
import com.example.diet.core.designsystem.theme.DietGreen
import com.example.diet.core.designsystem.theme.DietTextLight
import com.example.diet.core.designsystem.theme.DietTextPrimary

@Composable
fun CalorieRing(
    consumed: Int,
    progress: Float,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 10.dp.toPx()
            val radius = (size.minDimension - strokeWidth) / 2
            val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)
            val arcSize = Size(radius * 2, radius * 2)

            drawArc(
                color = Color(0xFFF0F0F0),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            drawArc(
                color = DietGreen,
                startAngle = -90f,
                sweepAngle = progress.coerceIn(0f, 1f) * 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "%,d".format(consumed),
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = DietTextPrimary
            )
            Text(text = "kcal", fontSize = 10.sp, color = DietTextLight)
        }
    }
}

@Composable
fun MacroBarRow(
    name: String,
    current: Float,
    goal: Float,
    color: Color,
    modifier: Modifier = Modifier
) {
    val progress = if (goal > 0f) (current / goal).coerceIn(0f, 1f) else 0f
    val currentText = if (current == current.toLong().toFloat()) {
        current.toLong().toString()
    } else {
        "%.1f".format(current)
    }
    val goalText = if (goal == goal.toLong().toFloat()) {
        "${goal.toLong()}g"
    } else {
        "%.1fg".format(goal)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Text(
            text = name,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF555555),
            modifier = Modifier.width(28.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Box(
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(DietDivider)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(color)
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "$currentText / $goalText",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = color,
            modifier = Modifier.width(80.dp),
            textAlign = TextAlign.End
        )
    }
}
