package com.example.diet.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.diet.core.database.entity.DiaryEntity
import com.example.diet.core.database.model.DiaryWithMeals
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface DiaryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(diary: DiaryEntity): Long

    @Update
    suspend fun update(diary: DiaryEntity)

    @Delete
    suspend fun delete(diary: DiaryEntity)

    @Query("SELECT * FROM diaries WHERE id = :id")
    suspend fun getById(id: Long): DiaryEntity?

    @Query("SELECT * FROM diaries WHERE date = :date")
    suspend fun getByDate(date: LocalDate): DiaryEntity?

    @Query("SELECT * FROM diaries ORDER BY date DESC")
    fun getAllDiaries(): Flow<List<DiaryEntity>>

    @Transaction
    @Query("SELECT * FROM diaries WHERE id = :id")
    suspend fun getDiaryWithMeals(id: Long): DiaryWithMeals?

    @Transaction
    @Query("SELECT * FROM diaries WHERE date = :date")
    suspend fun getDiaryWithMealsByDate(date: LocalDate): DiaryWithMeals?

    @Transaction
    @Query("SELECT * FROM diaries ORDER BY date DESC")
    fun getAllDiariesWithMeals(): Flow<List<DiaryWithMeals>>
}
