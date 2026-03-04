package com.example.diet.core.data.mapper

import com.example.diet.core.database.entity.UserProfileEntity
import com.example.diet.core.model.AppSettings
import com.example.diet.core.model.DarkMode
import com.example.diet.core.model.NotificationSettings
import com.example.diet.core.model.NutritionGoals
import com.example.diet.core.model.UserProfile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class UserProfileMapperTest {

    private val sampleEntity = UserProfileEntity(
        id = 1L,
        name = "홍길동",
        email = "hong@example.com",
        avatarUrl = "https://example.com/avatar.jpg",
        dailyCalorieGoal = 2000,
        carbohydrateGoal = 250f,
        proteinGoal = 100f,
        fatGoal = 67f,
        fiberGoal = 25f,
        mealNotificationEnabled = true,
        weeklyReportEnabled = true,
        goalExceededNotificationEnabled = false,
        breakfastNotificationTime = "08:00",
        lunchNotificationTime = "12:00",
        dinnerNotificationTime = "18:00",
        darkMode = "DARK",
        createdAt = 1000L,
        updatedAt = 2000L
    )

    private val sampleDomain = UserProfile(
        id = 1L,
        name = "홍길동",
        email = "hong@example.com",
        avatarUrl = "https://example.com/avatar.jpg",
        goals = NutritionGoals(
            dailyCalorie = 2000,
            carbohydrate = 250f,
            protein = 100f,
            fat = 67f,
            fiber = 25f
        ),
        notifications = NotificationSettings(
            mealNotificationEnabled = true,
            weeklyReportEnabled = true,
            goalExceededNotificationEnabled = false,
            breakfastTime = "08:00",
            lunchTime = "12:00",
            dinnerTime = "18:00"
        ),
        appSettings = AppSettings(darkMode = DarkMode.DARK),
        createdAt = 1000L,
        updatedAt = 2000L
    )

    // ── darkMode 변환 (분기 로직) ──────────────────────────────

    @Test
    fun `toDomain - darkMode 문자열이 올바른 enum으로 변환된다`() {
        assertEquals(DarkMode.DARK, UserProfileMapper.toDomain(sampleEntity.copy(darkMode = "DARK")).appSettings.darkMode)
        assertEquals(DarkMode.LIGHT, UserProfileMapper.toDomain(sampleEntity.copy(darkMode = "LIGHT")).appSettings.darkMode)
        assertEquals(DarkMode.AUTO, UserProfileMapper.toDomain(sampleEntity.copy(darkMode = "AUTO")).appSettings.darkMode)
    }

    @Test
    fun `toDomain - 알 수 없는 darkMode 문자열은 AUTO로 폴백된다`() {
        val result = UserProfileMapper.toDomain(sampleEntity.copy(darkMode = "UNKNOWN"))
        assertEquals(DarkMode.AUTO, result.appSettings.darkMode)
    }

    // ── null 처리 ──────────────────────────────────────────────

    @Test
    fun `toDomain - avatarUrl과 알림 시간이 null이면 null로 매핑된다`() {
        val result = UserProfileMapper.toDomain(
            sampleEntity.copy(
                avatarUrl = null,
                breakfastNotificationTime = null,
                lunchNotificationTime = null,
                dinnerNotificationTime = null
            )
        )
        assertNull(result.avatarUrl)
        assertNull(result.notifications.breakfastTime)
        assertNull(result.notifications.lunchTime)
        assertNull(result.notifications.dinnerTime)
    }

    // ── 왕복 변환 ─────────────────────────────────────────────

    @Test
    fun `entity to domain to entity 왕복 변환 시 데이터가 보존된다`() {
        assertEquals(sampleEntity, UserProfileMapper.toEntity(UserProfileMapper.toDomain(sampleEntity)))
    }

    @Test
    fun `domain to entity to domain 왕복 변환 시 데이터가 보존된다`() {
        assertEquals(sampleDomain, UserProfileMapper.toDomain(UserProfileMapper.toEntity(sampleDomain)))
    }
}
