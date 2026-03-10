package com.example.diet.feature.diary

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.diet.core.designsystem.theme.*
import com.example.diet.core.model.MealType
import java.time.LocalDate

@Composable
fun FoodRegisterScreen(
    mealType: MealType,
    date: LocalDate,
    onNavigateBack: () -> Unit,
    viewModel: FoodRegisterViewModel = hiltViewModel()
) {
    val name by viewModel.name.collectAsStateWithLifecycle()
    val emoji by viewModel.emoji.collectAsStateWithLifecycle()
    val servingSize by viewModel.servingSize.collectAsStateWithLifecycle()
    val carbs by viewModel.carbs.collectAsStateWithLifecycle()
    val protein by viewModel.protein.collectAsStateWithLifecycle()
    val fat by viewModel.fat.collectAsStateWithLifecycle()
    val fiber by viewModel.fiber.collectAsStateWithLifecycle()
    val autoCalories by viewModel.autoCalories.collectAsStateWithLifecycle()
    val canSave by viewModel.canSave.collectAsStateWithLifecycle()
    var showEmojiPicker by remember { mutableStateOf(false) }

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
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(RoundedCornerShape(9.dp))
                    .background(Color(0xFFF5F5F5))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onNavigateBack
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("‹", fontSize = 20.sp, color = Color(0xFF555555), fontWeight = FontWeight.Light)
            }
            Text(
                "새 음식 등록",
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                color = DietTextPrimary,
                modifier = Modifier.weight(1f)
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(9.dp))
                    .background(if (canSave) DietGreen else Color(0xFFF0F0F0))
                    .clickable(enabled = canSave) {
                        viewModel.saveAndAdd { onNavigateBack() }
                    }
                    .padding(horizontal = 13.dp, vertical = 5.dp)
            ) {
                Text(
                    "저장",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (canSave) Color.White else Color(0xFFCCCCCC)
                )
            }
        }

        HorizontalDivider(thickness = 1.dp, color = DietDivider)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(13.dp)
        ) {
            // 이모지 + 이름 행
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFF1F8E9))
                        .border(2.dp, Color(0xFFA5D6A7), RoundedCornerShape(14.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { showEmojiPicker = true },
                    contentAlignment = Alignment.Center
                ) {
                    Text(emoji, fontSize = 26.sp)
                }
                Column {
                    Text("이모지 선택", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DietGreen)
                    Text("탭해서 음식 이모지를 골라보세요", fontSize = 10.sp, color = DietTextLight)
                }
            }

            if (showEmojiPicker) {
                EmojiPickerBottomSheet(
                    onEmojiSelected = {
                        viewModel.emoji.value = it
                        showEmojiPicker = false
                    },
                    onDismiss = { showEmojiPicker = false }
                )
            }

            // 음식 이름
            FormInputField(
                label = "음식 이름",
                value = name,
                onValueChange = { viewModel.name.value = it },
                placeholder = "예) 닭가슴살 샐러드",
                required = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 1회 제공량 + 칼로리
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    FormInputField(
                        label = "1회 제공량",
                        value = servingSize,
                        onValueChange = { viewModel.servingSize.value = it },
                        placeholder = "100",
                        suffix = "g",
                        required = true,
                        keyboardType = KeyboardType.Decimal
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "칼로리 (자동)",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF777777),
                        modifier = Modifier.padding(bottom = 5.dp)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (autoCalories != null) Color(0xFFF1F8E9) else Color(0xFFF8F8F8))
                            .border(
                                1.5.dp,
                                if (autoCalories != null) Color(0xFFC8E6C9) else Color(0xFFF0F0F0),
                                RoundedCornerShape(10.dp)
                            )
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = autoCalories?.toString() ?: "—",
                                fontSize = if (autoCalories != null) 14.sp else 12.sp,
                                fontWeight = if (autoCalories != null) FontWeight.ExtraBold else FontWeight.Normal,
                                color = if (autoCalories != null) DietGreen else Color(0xFFCCCCCC)
                            )
                            Text("kcal", fontSize = 10.sp, color = if (autoCalories != null) Color(0xFF81C784) else DietTextLight)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 영양소 입력 그리드
            Text(
                "영양소 (1회 제공량 기준)",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF777777),
                modifier = Modifier.padding(bottom = 7.dp)
            )
            MacroInputGrid(
                carbs = carbs, onCarbsChange = { viewModel.carbs.value = it },
                protein = protein, onProteinChange = { viewModel.protein.value = it },
                fat = fat, onFatChange = { viewModel.fat.value = it },
                fiber = fiber, onFiberChange = { viewModel.fiber.value = it }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 칼로리 자동 계산 안내
            val allMacrosFilled = carbs.isNotBlank() && protein.isNotBlank() && fat.isNotBlank()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (allMacrosFilled) DietGreen else Color(0xFFF8F8F8))
                    .padding(horizontal = 12.dp, vertical = 9.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(if (allMacrosFilled) "✓" else "ℹ", fontSize = 14.sp, color = if (allMacrosFilled) Color.White else Color(0xFFAAAAAA))
                Text(
                    text = if (allMacrosFilled && autoCalories != null)
                        "탄${carbs}×4 + 단${protein}×4 + 지${fat}×9 = ${autoCalories} kcal"
                    else
                        "지방까지 입력하면 칼로리가 자동 계산돼요 · 탄×4 + 단×4 + 지×9",
                    fontSize = 10.sp,
                    color = if (allMacrosFilled) Color.White.copy(alpha = 0.9f) else Color(0xFFAAAAAA)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 저장 버튼
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (canSave) DietGreen else Color(0xFFF0F0F0))
                    .clickable(enabled = canSave) {
                        viewModel.saveAndAdd { onNavigateBack() }
                    }
                    .padding(vertical = 13.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "저장하고 ${mealType.displayName}에 추가하기",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (canSave) Color.White else Color(0xFFCCCCCC)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF5F5F5))
                    .clickable(enabled = canSave) {
                        viewModel.saveOnly { onNavigateBack() }
                    }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("나중에 추가할게요", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF555555))
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun FormInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    suffix: String? = null,
    required: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF777777))
            if (required) Text(" *", fontSize = 12.sp, color = DietRed)
        }
        Spacer(modifier = Modifier.height(5.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            placeholder = { Text(placeholder, fontSize = 12.sp, color = Color(0xFFCCCCCC)) },
            suffix = suffix?.let { { Text(it, fontSize = 10.sp, color = DietTextLight) } },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = DietGreen,
                unfocusedBorderColor = Color(0xFFF0F0F0),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color(0xFFF8F8F8)
            ),
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DietTextPrimary)
        )
    }
}

@Composable
private fun MacroInputGrid(
    carbs: String, onCarbsChange: (String) -> Unit,
    protein: String, onProteinChange: (String) -> Unit,
    fat: String, onFatChange: (String) -> Unit,
    fiber: String, onFiberChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MacroCard(label = "탄수화물", value = carbs, onValueChange = onCarbsChange, color = DietOrange, modifier = Modifier.weight(1f))
            MacroCard(label = "단백질", value = protein, onValueChange = onProteinChange, color = DietBlue, modifier = Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MacroCard(label = "지방", value = fat, onValueChange = onFatChange, color = DietRed, modifier = Modifier.weight(1f))
            MacroCard(label = "식이섬유", value = fiber, onValueChange = onFiberChange, color = DietPurple, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun MacroCard(label: String, value: String, onValueChange: (String) -> Unit, color: Color, modifier: Modifier = Modifier) {
    val filled = value.isNotBlank()
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (filled) Color.White else Color(0xFFF8F8F8))
            .border(1.5.dp, if (filled) color.copy(alpha = 0.5f) else Color(0xFFE8E8E8), RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(bottom = 6.dp)
        ) {
            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(color))
            Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (filled) color else Color(0xFFAAAAAA))
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                textStyle = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = DietTextPrimary),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                cursorBrush = SolidColor(DietGreen),
                decorationBox = { innerTextField ->
                    Box {
                        if (value.isEmpty()) Text("0", fontSize = 15.sp, color = Color(0xFFCCCCCC))
                        innerTextField()
                    }
                }
            )
            Text("g", fontSize = 10.sp, color = DietTextLight)
        }
    }
}

private val foodEmojis = listOf(
    "🍚", "🍜", "🍝", "🍛", "🍲", "🥘", "🥗", "🍱", "🍙", "🍘",
    "🍣", "🍤", "🍥", "🥟", "🌮", "🌯", "🥙", "🥪", "🍔", "🍟",
    "🌭", "🍕", "🥓", "🥩", "🍗", "🍖", "🥞", "🧇", "🥚", "🍳",
    "🧀", "🥐", "🍞", "🥖", "🥨", "🧆", "🥜", "🌰", "🍫", "🍬",
    "🧁", "🍰", "🎂", "🍩", "🍪", "🍦", "🍧", "🍨", "🍡", "🍢",
    "🍇", "🍓", "🫐", "🍈", "🍉", "🍊", "🍋", "🍌", "🍍", "🥭",
    "🍎", "🍏", "🍐", "🍑", "🍒", "🥝", "🍄", "🌽", "🥦", "🥬",
    "🥒", "🍆", "🥑", "🍅", "🧄", "🧅", "🥕", "🥔", "🍠", "🫚"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EmojiPickerBottomSheet(
    onEmojiSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = DietSurface
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp).navigationBarsPadding()) {
            Text(
                "음식 이모지 선택",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = DietTextPrimary,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(8),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.heightIn(max = 320.dp)
            ) {
                items(foodEmojis) { emoji ->
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF5F5F5))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onEmojiSelected(emoji) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(emoji, fontSize = 22.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
