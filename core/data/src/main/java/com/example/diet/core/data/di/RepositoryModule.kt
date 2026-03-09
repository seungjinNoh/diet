package com.example.diet.core.data.di

import com.example.diet.core.data.repository.DiaryRepositoryImpl
import com.example.diet.core.data.repository.FoodRepositoryImpl
import com.example.diet.core.data.repository.UserProfileRepositoryImpl
import com.example.diet.core.data.repository.DiaryRepository
import com.example.diet.core.data.repository.FoodRepository
import com.example.diet.core.data.repository.UserProfileRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindDiaryRepository(
        impl: DiaryRepositoryImpl
    ): DiaryRepository

    @Binds
    @Singleton
    abstract fun bindFoodRepository(
        impl: FoodRepositoryImpl
    ): FoodRepository

    @Binds
    @Singleton
    abstract fun bindUserProfileRepository(
        impl: UserProfileRepositoryImpl
    ): UserProfileRepository
}
