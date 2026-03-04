package com.example.diet.core.data.repository

import app.cash.turbine.test
import com.example.diet.core.data.mapper.UserProfileMapper
import com.example.diet.core.database.dao.UserProfileDao
import com.example.diet.core.database.entity.UserProfileEntity
import com.example.diet.core.model.AppSettings
import com.example.diet.core.model.DarkMode
import com.example.diet.core.model.NotificationSettings
import com.example.diet.core.model.NutritionGoals
import com.example.diet.core.model.UserProfile
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class UserProfileRepositoryImplTest {

    private lateinit var userProfileDao: UserProfileDao
    private lateinit var repository: UserProfileRepositoryImpl

    private val sampleEntity = UserProfileEntity(
        id = 1L,
        name = "홍길동",
        email = "hong@example.com",
        avatarUrl = null,
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
        darkMode = "AUTO",
        createdAt = 1000L,
        updatedAt = 2000L
    )

    private val sampleProfile = UserProfile(
        id = 1L,
        name = "홍길동",
        email = "hong@example.com",
        avatarUrl = null,
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
        appSettings = AppSettings(darkMode = DarkMode.AUTO),
        createdAt = 1000L,
        updatedAt = 2000L
    )

    @Before
    fun setUp() {
        userProfileDao = mockk()
        repository = UserProfileRepositoryImpl(userProfileDao)
    }

    // ── getProfile ────────────────────────────────────────────

    @Test
    fun `getProfile - DAO가 entity를 emit하면 domain으로 변환해서 반환한다`() = runTest {
        every { userProfileDao.getProfile() } returns flowOf(sampleEntity)

        repository.getProfile().test {
            assertEquals(UserProfileMapper.toDomain(sampleEntity), awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `getProfile - DAO가 null을 emit하면 null을 반환한다`() = runTest {
        every { userProfileDao.getProfile() } returns flowOf(null)

        repository.getProfile().test {
            assertNull(awaitItem())
            awaitComplete()
        }
    }

    // ── getProfileOnce ────────────────────────────────────────

    @Test
    fun `getProfileOnce - DAO가 entity를 반환하면 domain으로 변환해서 반환한다`() = runTest {
        coEvery { userProfileDao.getProfileOnce() } returns sampleEntity

        assertEquals(sampleProfile, repository.getProfileOnce())
    }

    @Test
    fun `getProfileOnce - DAO가 null을 반환하면 null을 반환한다`() = runTest {
        coEvery { userProfileDao.getProfileOnce() } returns null

        assertNull(repository.getProfileOnce())
    }

    // ── createProfile / updateProfile ─────────────────────────

    @Test
    fun `createProfile - Mapper를 거쳐 변환된 entity로 DAO를 호출한다`() = runTest {
        coEvery { userProfileDao.insertProfile(any()) } returns Unit

        repository.createProfile(sampleProfile)

        coVerify(exactly = 1) {
            userProfileDao.insertProfile(UserProfileMapper.toEntity(sampleProfile))
        }
    }

    @Test
    fun `updateProfile - Mapper를 거쳐 변환된 entity로 DAO를 호출한다`() = runTest {
        coEvery { userProfileDao.updateProfile(any()) } returns Unit

        repository.updateProfile(sampleProfile)

        coVerify(exactly = 1) {
            userProfileDao.updateProfile(UserProfileMapper.toEntity(sampleProfile))
        }
    }
}
