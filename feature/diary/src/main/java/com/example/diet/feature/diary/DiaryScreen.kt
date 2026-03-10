package com.example.diet.feature.diary

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.diet.core.designsystem.theme.*
import com.example.diet.core.model.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryScreen(
    modifier: Modifier = Modifier,
    onNavigateToFoodRegister: (MealType, LocalDate) -> Unit = { _, _ -> },
    viewModel: DiaryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val bottomSheetState by viewModel.bottomSheetState.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    val lastAddedFoodName by viewModel.lastAddedFoodName.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(lastAddedFoodName) {
        lastAddedFoodName?.let {
            Toast.makeText(context, "${it}이(가) 추가됐어요", Toast.LENGTH_SHORT).show()
            viewModel.clearLastAddedFoodName()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        when (val state = uiState) {
            is DiaryUiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = DietGreen)
                }
            }
            is DiaryUiState.Success -> {
                DiaryContent(
                    state = state,
                    onDateSelected = viewModel::onDateSelected,
                    onMealAddClick = viewModel::onMealAddClick
                )
            }
        }

        // 바텀 시트
        when (val sheet = bottomSheetState) {
            is DiaryBottomSheetState.FoodSearch -> {
                ModalBottomSheet(
                    onDismissRequest = viewModel::onBottomSheetDismiss,
                    containerColor = DietSurface
                ) {
                    FoodSearchSheetContent(
                        mealType = sheet.mealType,
                        searchResults = searchResults,
                        onQueryChange = viewModel::onSearchQueryChange,
                        onFoodSelected = { food -> viewModel.onFoodSelected(food, sheet.mealType) },
                        onRegisterNewFood = {
                            viewModel.onBottomSheetDismiss()
                            val state = uiState as? DiaryUiState.Success
                            state?.let { onNavigateToFoodRegister(sheet.mealType, it.selectedDate) }
                        },
                        onDismiss = viewModel::onBottomSheetDismiss
                    )
                }
            }
            is DiaryBottomSheetState.AmountInput -> {
                ModalBottomSheet(
                    onDismissRequest = viewModel::onBottomSheetDismiss,
                    containerColor = DietSurface
                ) {
                    AmountInputSheetContent(
                        food = sheet.food,
                        mealType = sheet.mealType,
                        amount = sheet.amount,
                        onAmountChange = viewModel::onAmountChanged,
                        onConfirm = viewModel::onConfirmAdd,
                        onDismiss = viewModel::onBottomSheetDismiss
                    )
                }
            }
            is DiaryBottomSheetState.Hidden -> Unit
        }
    }
}

@Composable
private fun DiaryContent(
    state: DiaryUiState.Success,
    onDateSelected: (LocalDate) -> Unit,
    onMealAddClick: (MealType) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DietBackground)
    ) {
        // 상단 바
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DietSurface)
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("식단 일지", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = DietTextPrimary)
        }

        // 주간 캘린더
        WeeklyCalendarRow(
            weekDates = state.weekDates,
            selectedDate = state.selectedDate,
            diaryDates = emptySet(),
            onDateSelected = onDateSelected
        )

        // 구분선
        HorizontalDivider(thickness = 1.dp, color = DietDivider)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // 날짜 헤더
            val totalCalories = state.diary?.totalCalories ?: 0
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = state.selectedDate.format(DateTimeFormatter.ofPattern("yyyy년 M월 d일 EEEE", Locale.KOREAN)),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = DietTextPrimary
                )
                if (totalCalories > 0) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFFE8F5E9))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "총 %,d kcal".format(totalCalories),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = DietGreen
                        )
                    }
                }
            }

            // 끼니 블록 목록
            val mealsMap = state.diary?.meals?.associateBy { it.mealType } ?: emptyMap()
            MealType.entries.forEach { mealType ->
                val meal = mealsMap[mealType]
                if (meal != null && meal.foods.isNotEmpty()) {
                    MealBlock(meal = meal, onAddFoodClick = { onMealAddClick(mealType) })
                } else {
                    EmptyMealCard(
                        mealType = mealType,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp),
                        onClick = { onMealAddClick(mealType) }
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun WeeklyCalendarRow(
    weekDates: List<LocalDate>,
    selectedDate: LocalDate,
    diaryDates: Set<LocalDate>,
    onDateSelected: (LocalDate) -> Unit
) {
    val today = LocalDate.now()
    val dayLabels = listOf("월", "화", "수", "목", "금", "토", "일")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(DietSurface)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        weekDates.forEachIndexed { index, date ->
            val isSelected = date == selectedDate
            val isToday = date == today
            val hasDiary = date in diaryDates

            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) DietGreen else Color.Transparent)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onDateSelected(date) }
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = dayLabels[index],
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isSelected) Color.White.copy(alpha = 0.8f) else DietTextLight
                )
                Text(
                    text = date.dayOfMonth.toString(),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.White else DietTextPrimary
                )
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isSelected -> Color.White.copy(alpha = 0.6f)
                                hasDiary -> DietGreen
                                else -> Color.Transparent
                            }
                        )
                )
            }
        }
    }
}

@Composable
private fun MealBlock(meal: Meal, onAddFoodClick: () -> Unit) {
    val iconBg = when (meal.mealType) {
        MealType.BREAKFAST -> Color(0xFFFFF3E0)
        MealType.LUNCH -> Color(0xFFE8F5E9)
        MealType.DINNER -> Color(0xFFE3F2FD)
        MealType.SNACK -> Color(0xFFF3E5F5)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DietSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        // 헤더
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(13.dp, 13.dp, 16.dp, 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Text(text = meal.mealType.emoji, fontSize = 18.sp)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = meal.mealType.displayName, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = DietTextPrimary)
                meal.time?.let { Text(text = it, fontSize = 11.sp, color = DietTextLight) }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "%,d".format(meal.totalCalories), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF555555))
                Text(text = " kcal", fontSize = 11.sp, color = DietTextLight)
            }
        }

        HorizontalDivider(thickness = 1.dp, color = Color(0xFFF8F8F8))

        // 음식 목록
        meal.foods.forEach { food ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 13.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(DietOrange)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = food.name, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF333333))
                    Text(text = "%.0fg".format(food.amount), fontSize = 10.sp, color = DietTextLight)
                }
                Text(text = "%,d kcal".format(food.calories), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF555555))
            }
            HorizontalDivider(thickness = 1.dp, color = Color(0xFFF8F8F8))
        }

        // 음식 추가 버튼
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onAddFoodClick
                )
                .padding(horizontal = 13.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE8F5E9)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "+", fontSize = 13.sp, color = DietGreen)
            }
            Text(text = "음식 추가", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DietGreen)
        }
    }
}

@Composable
private fun EmptyMealCard(
    mealType: MealType,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val iconBg = when (mealType) {
        MealType.BREAKFAST -> Color(0xFFFFF3E0)
        MealType.LUNCH -> Color(0xFFE8F5E9)
        MealType.DINNER -> Color(0xFFE3F2FD)
        MealType.SNACK -> Color(0xFFF3E5F5)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DietSurface)
            .border(1.5.dp, Color(0xFFE8E8E8), RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(13.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(RoundedCornerShape(9.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Text(text = mealType.emoji, fontSize = 15.sp)
            }
            Column {
                Text(text = mealType.displayName, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFCCCCCC))
                Text(text = "+ 탭하여 ${mealType.displayName}을 기록하세요", fontSize = 10.sp, color = Color(0xFFDDDDDD))
            }
        }
    }
}

@Composable
private fun FoodSearchSheetContent(
    mealType: MealType,
    searchResults: List<Food>,
    onQueryChange: (String) -> Unit,
    onFoodSelected: (Food) -> Unit,
    onRegisterNewFood: () -> Unit,
    onDismiss: () -> Unit
) {
    var query by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(horizontal = 16.dp).navigationBarsPadding()) {
        // 헤더
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("음식 추가", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DietTextPrimary)
                Text(mealType.displayName, fontSize = 10.sp, color = DietTextLight)
            }
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF5F5F5))
                    .clickable(onClick = onDismiss),
                contentAlignment = Alignment.Center
            ) {
                Text("✕", fontSize = 14.sp, color = Color(0xFF888888))
            }
        }

        // 검색창
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFF5F5F5))
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("🔍", fontSize = 14.sp, color = DietTextLight)
            BasicTextField(
                value = query,
                onValueChange = {
                    query = it
                    onQueryChange(it)
                },
                modifier = Modifier.weight(1f),
                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 13.sp, color = DietTextPrimary),
                cursorBrush = SolidColor(DietGreen),
                singleLine = true,
                decorationBox = { inner ->
                    if (query.isEmpty()) Text("음식 검색...", fontSize = 13.sp, color = Color(0xFFBBBBBB))
                    inner()
                }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 새 음식 등록 배너
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(13.dp))
                .background(Color(0xFFF1F8E9))
                .border(1.5.dp, Color(0xFFA5D6A7), RoundedCornerShape(13.dp))
                .clickable(onClick = onRegisterNewFood)
                .padding(13.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(DietGreen),
                contentAlignment = Alignment.Center
            ) {
                Text("✏️", fontSize = 16.sp)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("새 음식 직접 등록하기", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                Text("원하는 음식이 없으면 직접 추가해요", fontSize = 10.sp, color = Color(0xFF81C784))
            }
            Text("›", fontSize = 18.sp, color = Color(0xFFA5D6A7))
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (searchResults.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🍽️", fontSize = 36.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        if (query.isBlank()) "아직 등록된 음식이 없어요" else "'$query' 검색 결과가 없어요",
                        fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF333333)
                    )
                    Text("위 버튼으로 음식을 직접 등록해보세요", fontSize = 12.sp, color = DietTextLight)
                }
            }
        } else {
            if (query.isBlank()) {
                Text("내가 등록한 음식 (${searchResults.size})", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = DietTextLight, modifier = Modifier.padding(vertical = 6.dp))
            }
            LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                items(searchResults) { food ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onFoodSelected(food) }
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(9.dp))
                                .background(Color(0xFFF5F5F5)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(food.emoji, fontSize = 16.sp)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(food.name, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF333333))
                            Text("%.0fg 기준 · %,d kcal".format(food.amount, food.calories), fontSize = 10.sp, color = DietTextLight)
                        }
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .clip(RoundedCornerShape(7.dp))
                                .background(DietGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("+", fontSize = 16.sp, color = Color.White)
                        }
                    }
                    HorizontalDivider(thickness = 1.dp, color = Color(0xFFF5F5F5))
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun AmountInputSheetContent(
    food: Food,
    mealType: MealType,
    amount: Float,
    onAmountChange: (Float) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val scale = amount / food.amount
    val scaledCalories = (food.calories * scale).toInt()
    val scaledCarbs = food.nutrition.carbs * scale
    val scaledProtein = food.nutrition.protein * scale
    val scaledFat = food.nutrition.fat * scale
    val scaledFiber = food.nutrition.fiber * scale

    Column(modifier = Modifier.padding(horizontal = 16.dp).navigationBarsPadding()) {
        // 헤더
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("섭취량 입력", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DietTextPrimary)
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF5F5F5))
                    .clickable(onClick = onDismiss),
                contentAlignment = Alignment.Center
            ) {
                Text("✕", fontSize = 14.sp, color = Color(0xFF888888))
            }
        }

        // 선택된 음식 카드
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF1F8E9))
                .padding(12.dp, 12.dp, 14.dp, 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Text(food.emoji, fontSize = 20.sp)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(food.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DietTextPrimary)
                Text("기준 %.0fg · %,d kcal".format(food.amount, food.calories), fontSize = 11.sp, color = Color(0xFF888888))
            }
            Text("%,d kcal".format(scaledCalories), fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = DietGreen)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 섭취량 입력
        Text("섭취량 (g)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF555555))
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF5F5F5))
                    .clickable { onAmountChange(-10f) },
                contentAlignment = Alignment.Center
            ) {
                Text("−", fontSize = 20.sp, color = Color(0xFF555555))
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White)
                    .border(2.dp, DietGreen, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("%.0f".format(amount), fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = DietTextPrimary)
                    Text(" g", fontSize = 12.sp, color = DietTextLight)
                }
            }
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF5F5F5))
                    .clickable { onAmountChange(10f) },
                contentAlignment = Alignment.Center
            ) {
                Text("+", fontSize = 20.sp, color = Color(0xFF555555))
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 영양소 미리보기
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf(
                Triple("%.1f".format(scaledCarbs), "탄수화물", DietOrange),
                Triple("%.1f".format(scaledProtein), "단백질", DietBlue),
                Triple("%.1f".format(scaledFat), "지방", DietRed),
                Triple("%.1f".format(scaledFiber), "식이섬유", DietPurple)
            ).forEach { (value, label, color) ->
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(9.dp))
                        .background(Color(0xFFF8F8F8))
                        .padding(vertical = 7.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("${value}g", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = color)
                    Text(label, fontSize = 9.sp, color = DietTextLight)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 확인 버튼
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(DietGreen)
                .clickable(onClick = onConfirm)
                .padding(vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "${mealType.displayName}에 추가하기",
                fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
