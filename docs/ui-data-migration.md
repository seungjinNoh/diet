# UI Mockup 기반 데이터 구조 개선 작업

> UI mockup 분석 결과, 추가/수정이 필요한 데이터 구조 목록

작성일: 2026-03-02

---

## 📋 작업 목록

### Phase 1: 기존 모델 확장

#### 1.1 끼니 타입 추가
- [x] `MealType` enum 생성 (core/model)
- [x] `MealEntity` - `mealType`, `time` 필드 추가
- [x] `Meal` 도메인 모델 - `mealType`, `time` 필드 추가
- [x] `MealMapper` 수정

#### 1.2 음식 이모지 추가
- [x] `FoodEntity` - `emoji` 필드 추가
- [x] `Food` 도메인 모델 - `emoji` 필드 추가
- [x] `FoodMapper` 수정

### Phase 2: 사용자 프로필 시스템 추가

#### 2.1 Database Layer
- [x] `UserProfileEntity` 생성
- [x] `UserProfileDao` 생성
- [x] `DietDatabase`에 Entity 등록

#### 2.2 Domain Layer
- [x] `UserProfile` 도메인 모델 생성
- [x] `UserProfileRepository` 인터페이스 생성

#### 2.3 Data Layer
- [x] `UserProfileMapper` 생성
- [x] `UserProfileRepositoryImpl` 생성
- [x] `RepositoryModule`에 바인딩 추가

### Phase 3: 데이터베이스 마이그레이션

- [ ] Migration 전략 수립
- [ ] Migration 코드 작성
- [ ] 테스트 데이터 준비

---

## 📐 데이터 구조 상세

### 1. MealType Enum

```kotlin
// core/model/src/main/java/com/example/diet/core/model/MealType.kt
package com.example.diet.core.model

enum class MealType(val emoji: String, val displayName: String) {
    BREAKFAST("🌅", "아침"),
    LUNCH("☀️", "점심"),
    DINNER("🌙", "저녁"),
    SNACK("🍎", "간식")
}
```

### 2. MealEntity 수정

```kotlin
// core/database/entity/MealEntity.kt
@Entity(tableName = "meals")
data class MealEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val diaryId: Long,
    val mealType: MealType,              // 추가
    val time: String? = null,            // 추가 (LocalTime은 Room에서 직접 지원 안함)
    val totalCalories: Int = 0,
    @Embedded(prefix = "total_")
    val totalNutrition: NutritionInfoEmbedded = NutritionInfoEmbedded()
)
```

**TypeConverter 필요:**
```kotlin
@TypeConverter
fun fromMealType(value: MealType): String = value.name

@TypeConverter
fun toMealType(value: String): MealType = MealType.valueOf(value)
```

### 3. FoodEntity 수정

```kotlin
// core/database/entity/FoodEntity.kt
@Entity(tableName = "foods")
data class FoodEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val emoji: String = "🍽️",           // 추가
    val servingSize: Float = 100f,
    val calories: Int,
    @Embedded(prefix = "nutrition_")
    val nutrition: NutritionInfoEmbedded = NutritionInfoEmbedded()
)
```

### 4. UserProfileEntity (신규)

```kotlin
// core/database/entity/UserProfileEntity.kt
package com.example.diet.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey
    val id: Long = 1,
    val name: String,
    val email: String,
    val avatarUrl: String? = null,

    // 목표 설정
    val dailyCalorieGoal: Int = 2000,
    val carbohydrateGoal: Float = 250f,
    val proteinGoal: Float = 100f,
    val fatGoal: Float = 67f,
    val fiberGoal: Float = 25f,

    // 알림 설정
    val mealNotificationEnabled: Boolean = true,
    val weeklyReportEnabled: Boolean = true,
    val goalExceededNotificationEnabled: Boolean = false,

    // 식사 알림 시간
    val breakfastNotificationTime: String? = "08:00",
    val lunchNotificationTime: String? = "12:00",
    val dinnerNotificationTime: String? = "18:00",

    // 앱 설정
    val darkMode: String = "AUTO",  // AUTO, LIGHT, DARK

    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
```

### 5. UserProfileDao (신규)

```kotlin
// core/database/dao/UserProfileDao.kt
package com.example.diet.core.database.dao

import androidx.room.*
import com.example.diet.core.database.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getProfile(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id = 1")
    suspend fun getProfileOnce(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: UserProfileEntity)

    @Update
    suspend fun updateProfile(profile: UserProfileEntity)

    @Query("UPDATE user_profile SET dailyCalorieGoal = :goal WHERE id = 1")
    suspend fun updateCalorieGoal(goal: Int)

    @Query("""
        UPDATE user_profile
        SET carbohydrateGoal = :carb, proteinGoal = :protein, fatGoal = :fat, fiberGoal = :fiber
        WHERE id = 1
    """)
    suspend fun updateMacroGoals(carb: Float, protein: Float, fat: Float, fiber: Float)
}
```

### 6. UserProfile 도메인 모델 (신규)

```kotlin
// core/model/src/main/java/com/example/diet/core/model/UserProfile.kt
package com.example.diet.core.model

data class UserProfile(
    val id: Long = 1,
    val name: String,
    val email: String,
    val avatarUrl: String? = null,
    val goals: NutritionGoals,
    val notifications: NotificationSettings,
    val appSettings: AppSettings,
    val createdAt: Long,
    val updatedAt: Long
)

data class NutritionGoals(
    val dailyCalorie: Int = 2000,
    val carbohydrate: Float = 250f,
    val protein: Float = 100f,
    val fat: Float = 67f,
    val fiber: Float = 25f
)

data class NotificationSettings(
    val mealNotificationEnabled: Boolean = true,
    val weeklyReportEnabled: Boolean = true,
    val goalExceededNotificationEnabled: Boolean = false,
    val breakfastTime: String? = "08:00",
    val lunchTime: String? = "12:00",
    val dinnerTime: String? = "18:00"
)

data class AppSettings(
    val darkMode: DarkMode = DarkMode.AUTO
)

enum class DarkMode {
    AUTO, LIGHT, DARK
}
```

### 7. UserProfileRepository 인터페이스 (신규)

```kotlin
// core/domain/src/main/java/com/example/diet/core/domain/repository/UserProfileRepository.kt
package com.example.diet.core.domain.repository

import com.example.diet.core.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserProfileRepository {
    fun getProfile(): Flow<UserProfile?>
    suspend fun getProfileOnce(): UserProfile?
    suspend fun createProfile(profile: UserProfile)
    suspend fun updateProfile(profile: UserProfile)
    suspend fun updateCalorieGoal(goal: Int)
    suspend fun updateMacroGoals(carb: Float, protein: Float, fat: Float, fiber: Float)
}
```

---

## 🗄️ 데이터베이스 마이그레이션

### Migration 1 → 2: 테이블 수정

```kotlin
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // 1. meals 테이블에 컬럼 추가
        database.execSQL("ALTER TABLE meals ADD COLUMN mealType TEXT NOT NULL DEFAULT 'BREAKFAST'")
        database.execSQL("ALTER TABLE meals ADD COLUMN time TEXT")

        // 2. foods 테이블에 컬럼 추가
        database.execSQL("ALTER TABLE foods ADD COLUMN emoji TEXT NOT NULL DEFAULT '🍽️'")

        // 3. user_profile 테이블 생성
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS user_profile (
                id INTEGER PRIMARY KEY NOT NULL,
                name TEXT NOT NULL,
                email TEXT NOT NULL,
                avatarUrl TEXT,
                dailyCalorieGoal INTEGER NOT NULL DEFAULT 2000,
                carbohydrateGoal REAL NOT NULL DEFAULT 250.0,
                proteinGoal REAL NOT NULL DEFAULT 100.0,
                fatGoal REAL NOT NULL DEFAULT 67.0,
                fiberGoal REAL NOT NULL DEFAULT 25.0,
                mealNotificationEnabled INTEGER NOT NULL DEFAULT 1,
                weeklyReportEnabled INTEGER NOT NULL DEFAULT 1,
                goalExceededNotificationEnabled INTEGER NOT NULL DEFAULT 0,
                breakfastNotificationTime TEXT DEFAULT '08:00',
                lunchNotificationTime TEXT DEFAULT '12:00',
                dinnerNotificationTime TEXT DEFAULT '18:00',
                darkMode TEXT NOT NULL DEFAULT 'AUTO',
                createdAt INTEGER NOT NULL,
                updatedAt INTEGER NOT NULL
            )
        """.trimIndent())
    }
}
```

---

## ✅ 체크리스트

완료된 항목은 `[x]`로 표시

### Phase 1 ✅ 완료
- [x] MealType enum
- [x] MealEntity 수정
- [x] Meal 모델 수정
- [x] FoodEntity 수정
- [x] Food 모델 수정
- [x] Mapper 수정 (MealMapper, FoodMapper)
- [x] TypeConverter 추가 (MealTypeConverter)
- [x] 빌드 테스트 통과

### Phase 2 ✅ 완료
- [x] UserProfileEntity
- [x] UserProfile 모델
- [x] UserProfileDao
- [x] UserProfileRepository 인터페이스
- [x] UserProfileMapper
- [x] UserProfileRepositoryImpl
- [x] DietDatabase 업데이트
- [x] Module 바인딩
- [x] 빌드 테스트 통과

### Phase 3
- [ ] Migration 작성
- [ ] 버전 업데이트
- [ ] 빌드 테스트

---

## 📝 작업 순서

1. **Phase 1 먼저 완료** (기존 모델 확장)
   - 영향 범위가 작고 독립적
   - 빌드 에러 즉시 확인 가능

2. **Phase 2 진행** (UserProfile 시스템)
   - 완전히 새로운 기능이므로 기존 코드에 영향 없음

3. **Phase 3 마지막** (Migration)
   - 모든 변경이 완료된 후 DB 버전 업데이트

---

## 🎯 다음 단계

작업을 시작하려면:
```bash
# Phase 1 시작
# 1.1 MealType enum 생성부터 시작
```
