package com.example.diet.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diet.core.designsystem.theme.DietSurface
import com.example.diet.core.designsystem.theme.DietTextLight
import com.example.diet.core.designsystem.theme.DietTextPrimary
import com.example.diet.core.model.Meal
import com.example.diet.core.model.MealType

private val mealIconBackground = mapOf(
    MealType.BREAKFAST to Color(0xFFFFF3E0),
    MealType.LUNCH to Color(0xFFE8F5E9),
    MealType.DINNER to Color(0xFFE3F2FD),
    MealType.SNACK to Color(0xFFF3E5F5)
)

@Composable
fun MealCard(
    meal: Meal,
    modifier: Modifier = Modifier
) {
    val iconBg = mealIconBackground[meal.mealType] ?: Color(0xFFF5F5F5)
    val foodNames = meal.foods.joinToString(", ") { it.name }.ifEmpty { "음식 정보 없음" }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DietSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp, 14.dp, 16.dp, 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Text(text = meal.mealType.emoji, fontSize = 20.sp)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = meal.mealType.displayName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = DietTextPrimary
                )
                Text(
                    text = foodNames,
                    fontSize = 12.sp,
                    color = DietTextLight,
                    modifier = Modifier.padding(top = 2.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "%,d".format(meal.totalCalories),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = DietTextPrimary
                )
                Text(text = " kcal", fontSize = 11.sp, color = DietTextLight)
            }
        }
    }
}

@Composable
fun MealAddCard(
    mealName: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF8F8F8))
            .border(
                width = 2.dp,
                color = Color(0xFFE0E0E0),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF0F0F0)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "+", fontSize = 22.sp, color = Color(0xFFBBBBBB))
            }
            Text(
                text = mealName,
                fontSize = 14.sp,
                color = Color(0xFFBBBBBB),
                fontWeight = FontWeight.Medium
            )
        }
    }
}
