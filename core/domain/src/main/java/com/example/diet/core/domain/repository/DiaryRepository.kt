package com.example.diet.core.domain.repository

import com.example.diet.core.model.Diary
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface DiaryRepository {
    /**
     * 특정 날짜의 식단 일지를 조회합니다.
     */
    fun getDiaryByDate(date: LocalDate): Flow<Diary?>

    /**
     * 날짜 범위 내의 모든 식단 일지를 조회합니다.
     */
    fun getDiariesByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<Diary>>

    /**
     * 모든 식단 일지를 조회합니다.
     */
    fun getAllDiaries(): Flow<List<Diary>>

    /**
     * 새로운 식단 일지를 추가합니다.
     * @return 생성된 Diary의 ID
     */
    suspend fun insertDiary(diary: Diary): Long

    /**
     * 기존 식단 일지를 업데이트합니다.
     */
    suspend fun updateDiary(diary: Diary)

    /**
     * 식단 일지를 삭제합니다.
     */
    suspend fun deleteDiary(diaryId: Long)

    /**
     * 특정 날짜의 식단 일지가 존재하는지 확인합니다.
     */
    suspend fun existsByDate(date: LocalDate): Boolean
}
