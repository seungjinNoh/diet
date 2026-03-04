# Diet App - Google App Architecture

## 선택 이유

- 다른 프로젝트는 Clean Architecture로 진행
- 이번 Diet 앱은 **Google 공식 App Architecture** 스타일로 스터디

---

## 모듈 구조

```
Diet/
├── app/
│
├── feature/
│   ├── home/
│   ├── diary/
│   ├── analysis/
│   └── settings/
│
└── core/
    ├── designsystem/
    ├── ui/
    ├── data/
    ├── domain/
    ├── model/
    ├── database/
    ├── network/
    └── navigation/
```

---

## Convention Plugin 매핑

| 모듈 | Plugin |
|------|--------|
| app | `diet.android.application` + `diet.android.application.compose` |
| feature/* | `diet.android.feature` + `diet.android.hilt` |
| core/designsystem | `diet.android.library.compose` |
| core/ui | `diet.android.library.compose` |
| core/data | `diet.android.library` + `diet.android.hilt` |
| core/domain | `diet.android.library` |
| core/model | `diet.android.library` |
| core/database | `diet.android.library` + `diet.android.hilt` |
| core/network | `diet.android.library` + `diet.android.hilt` |
| core/navigation | `diet.android.library` |

---

## 각 모듈 설명

### app
- Navigation Host 설정
- Hilt Application 설정
- Activity 진입점

### feature/*
- Composable UI (Screen)
- ViewModel (비즈니스 로직)
- 각 feature의 Navigation 정의
- `core:designsystem`, `core:domain`, `core:model`, `core:navigation` 자동 의존

### core/designsystem
- Theme (Color, Typography, Shape)
- 공통 Composable 컴포넌트

### core/ui
- Shared UI 유틸 (Modifier 확장 등)

### core/data
- Repository 구현체
- DataSource (Local/Remote)

### core/domain
- Repository Interface
- (UseCase는 복잡한 로직이 생길 때만 선택적으로 추가)

### core/model
- 앱 전체에서 사용하는 데이터 모델

### core/database
- Room Database
- DAO

### core/network
- Retrofit 설정
- API Interface (필요시)

### core/navigation
- Navigation Routes 정의 (Sealed class / Object)

---

## 레이어 구조 (Google App Architecture)

```
UI Layer
  ├── Screen (Composable)
  └── ViewModel
        ↓
Data Layer
  ├── Repository (Interface: domain / Impl: data)
  └── DataSource (database / network)
```

> UseCase는 필요한 경우에만 선택적으로 추가

---

## 핵심 코딩 패턴

### ViewModel - Repository 직접 사용
```kotlin
// feature/home/HomeViewModel.kt
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val diaryRepository: DiaryRepository
) : ViewModel() {

    val diaries = diaryRepository.getDiaries()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )
}
```

### Repository
```kotlin
// core/data/DiaryRepository.kt
class DiaryRepositoryImpl @Inject constructor(
    private val diaryDao: DiaryDao
) : DiaryRepository {

    override fun getDiaries(): Flow<List<Diary>> =
        diaryDao.getAllDiaries()

    override suspend fun insertDiary(diary: Diary) =
        diaryDao.insert(diary)
}
```

### Navigation
```kotlin
// core/navigation/DietNavigation.kt
sealed class DietRoute(val route: String) {
    data object Home : DietRoute("home")
    data object Diary : DietRoute("diary")
    data object Analysis : DietRoute("analysis")
    data object Settings : DietRoute("settings")
}
```

### build.gradle.kts 예시

```kotlin
// feature/home/build.gradle.kts
plugins {
    id("diet.android.feature")
    id("diet.android.hilt")
}
```

```kotlin
// core/database/build.gradle.kts
plugins {
    id("diet.android.library")
    id("diet.android.hilt")
}

dependencies {
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
}
```

---

## Clean Architecture vs Google App Architecture 비교

| | Clean Architecture | Google App Architecture |
|---|---|---|
| UseCase | 항상 필수 | 선택적 |
| Repository Interface | 필수 | 선택적 |
| 레이어 경계 | 엄격 | 유연 |
| 보일러플레이트 | 많음 | 적음 |
| 테스트 | 완벽 분리 | 실용적 수준 |
| 학습 곡선 | 높음 | 낮음 |
| 적합한 규모 | 대규모 | 중소규모 |
