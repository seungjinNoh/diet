# UI 작업 계획

> feature 모듈 생성 → 앱 껍데기 → 각 화면 구현 순으로 진행

작성일: 2026-03-05

---

## 현재 상태

- `feature/` 모듈 없음 (home, diary, analysis, settings 모두 미생성)
- `MainActivity` 스텁 상태 ("Hello Android")
- `DietRoute` 정의 완료 (HomeRoute, DiaryRoute, AnalysisRoute, SettingsRoute)
- Convention Plugin 세팅 완료

---

## 작업 순서 개요

```
Phase 1: 앱 껍데기 (Bottom Nav + 4개 빈 화면)
    ↓
Phase 2: Home 화면
    ↓
Phase 3: Diary 화면
    ↓
Phase 4: Analysis 화면
    ↓
Phase 5: Settings 화면
```

---

## Phase 1: 앱 껍데기

> 바텀 네비게이션과 4개 화면의 틀을 잡는 단계

### 1.1 feature 모듈 4개 생성

각 모듈 구조:
```
feature/home/
feature/diary/
feature/analysis/
feature/settings/
```

각 모듈 `build.gradle.kts`:
```kotlin
plugins {
    id("diet.android.feature")
    id("diet.android.hilt")
}
```

각 모듈에 만들 파일 (최소한):
```
feature/home/
└── src/main/.../feature/home/
    ├── HomeScreen.kt      // Composable (빈 화면)
    └── HomeViewModel.kt   // (빈 ViewModel)
```

### 1.2 app 모듈 - Navigation 설정

```
app/
└── src/main/.../
    ├── MainActivity.kt         // NavHost, BottomBar 세팅
    └── navigation/
        └── DietNavHost.kt      // NavHost + 각 화면 composable 등록
```

**MainActivity.kt 구조:**
```kotlin
DietTheme {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { DietBottomBar(navController) }
    ) { innerPadding ->
        DietNavHost(navController, Modifier.padding(innerPadding))
    }
}
```

**바텀바 4개 탭:**
| 탭 | Route | 아이콘 | 라벨 |
|---|---|---|---|
| Home | HomeRoute | Home | 홈 |
| Diary | DiaryRoute | MenuBook | 식단 |
| Analysis | DiaryRoute | BarChart | 분석 |
| Settings | SettingsRoute | Settings | 설정 |

### 1.3 app 모듈 - 의존성 추가

`app/build.gradle.kts`에 feature 모듈 추가:
```kotlin
dependencies {
    implementation(project(":feature:home"))
    implementation(project(":feature:diary"))
    implementation(project(":feature:analysis"))
    implementation(project(":feature:settings"))
    implementation(project(":core:navigation"))
}
```

### Phase 1 체크리스트

- [ ] `feature/home` 모듈 생성 + `HomeScreen.kt` (빈 화면)
- [ ] `feature/diary` 모듈 생성 + `DiaryScreen.kt` (빈 화면)
- [ ] `feature/analysis` 모듈 생성 + `AnalysisScreen.kt` (빈 화면)
- [ ] `feature/settings` 모듈 생성 + `SettingsScreen.kt` (빈 화면)
- [ ] `DietNavHost.kt` 작성 (4개 화면 composable 등록)
- [ ] `DietBottomBar.kt` 작성 (바텀 네비게이션)
- [ ] `MainActivity.kt` 수정 (NavHost + BottomBar 연결)
- [ ] `settings.gradle.kts`에 feature 모듈 등록
- [ ] 빌드 + 탭 전환 동작 확인

---

## Phase 2: Home 화면

> 오늘의 식단 요약 화면

### 화면 구성 (예상)

```
┌─────────────────────────────┐
│  2026년 3월 5일 목요일  < >  │  ← 날짜 헤더 + 전/후 날짜 이동
├─────────────────────────────┤
│  오늘 섭취                  │
│  [칼로리 진행바]  1200/2000 │  ← 목표 대비 칼로리
│  탄수화물  단백질  지방      │  ← 주요 영양소 요약
├─────────────────────────────┤
│  🌅 아침                   │  ← MealType별 카드
│  - 닭가슴살 150g  165kcal  │
│  ☀️ 점심                   │
│  - 현미밥 200g   350kcal   │
│  🌙 저녁                   │
│  비어있음                   │
│  🍎 간식                   │
│  비어있음                   │
├─────────────────────────────┤
│  [+ 음식 추가]              │  ← FAB
└─────────────────────────────┘
```

### 필요한 파일

```
feature/home/
└── HomeScreen.kt
└── HomeViewModel.kt
└── component/
    ├── DateHeader.kt
    ├── CalorieSummaryCard.kt
    └── MealCard.kt
```

### Phase 2 체크리스트

- [ ] `HomeViewModel` - 오늘 날짜 DiaryRepository에서 데이터 로딩
- [ ] `DateHeader` 컴포넌트
- [ ] `CalorieSummaryCard` 컴포넌트 (칼로리 + 영양소)
- [ ] `MealCard` 컴포넌트 (MealType별)
- [ ] 빈 상태(empty state) 처리
- [ ] 날짜 이동 기능

---

## Phase 3: Diary 화면

> 날짜별 식단 기록 목록 화면

### 화면 구성 (예상)

```
┌─────────────────────────────┐
│  식단 기록                  │
│  [월 달력 or 주간 뷰]        │  ← 날짜 선택
├─────────────────────────────┤
│  3월 4일 (어제)             │
│  총 1,800 kcal              │
│  3월 3일 (2일 전)           │
│  총 2,100 kcal              │
└─────────────────────────────┘
```

### Phase 3 체크리스트

- [ ] `DiaryViewModel` - DiaryRepository에서 날짜 범위 데이터 로딩
- [ ] 캘린더/주간 뷰 컴포넌트
- [ ] 날짜별 기록 리스트 아이템

---

## Phase 4: Analysis 화면

> 영양소 통계 분석 화면

### 화면 구성 (예상)

```
┌─────────────────────────────┐
│  분석      [주간 | 월간]    │
├─────────────────────────────┤
│  평균 칼로리                │
│  [막대 그래프 - 요일별]      │
├─────────────────────────────┤
│  영양소 비율                │
│  [파이 차트 or 도넛 차트]   │
└─────────────────────────────┘
```

### Phase 4 체크리스트

- [ ] `AnalysisViewModel` - 기간별 집계 데이터
- [ ] 차트 라이브러리 결정 (Vico, MPAndroidChart 등)
- [ ] 칼로리 추이 차트
- [ ] 영양소 비율 차트

---

## Phase 5: Settings 화면

> 프로필 + 목표 + 알림 설정 화면

### 화면 구성 (예상)

```
┌─────────────────────────────┐
│  설정                       │
├─────────────────────────────┤
│  👤 홍길동                  │  ← 프로필
│  hong@example.com           │
├─────────────────────────────┤
│  목표 설정                  │
│  일일 칼로리  2,000 kcal >  │
│  영양소 목표             >  │
├─────────────────────────────┤
│  알림 설정               >  │
├─────────────────────────────┤
│  다크 모드    자동       >  │
└─────────────────────────────┘
```

### Phase 5 체크리스트

- [ ] `SettingsViewModel` - UserProfileRepository 연결
- [ ] 프로필 섹션
- [ ] 목표 설정 섹션 (칼로리, 영양소)
- [ ] 알림 설정 섹션
- [ ] 다크 모드 설정

---

## 작업 원칙

- **Phase 1 완료 후 나머지 진행** — 껍데기가 먼저
- **화면 하나씩 완성** — 여러 화면 동시 진행 X
- **컴포넌트는 feature 내부에 먼저** — 공통화가 필요해지면 `core/designsystem`으로 이동
- **ViewModel 없이 UI 먼저** — 데이터 연결은 UI 윤곽 잡힌 후

---

## 다음 단계

Phase 1부터 시작:
1. `feature/home`, `diary`, `analysis`, `settings` 모듈 생성
2. 각 모듈에 빈 Screen 파일 추가
3. `MainActivity` 수정
