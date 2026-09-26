package com.healthos.app.di

import com.healthos.app.data.preferences.UserPreferencesDataSource
import com.healthos.app.data.repository.UserRepositoryImpl
import com.healthos.app.data.repository.WeightRepositoryImpl
import com.healthos.app.domain.repository.PreferencesRepository
import com.healthos.app.domain.repository.UserRepository
import com.healthos.app.domain.repository.WeightRepository
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
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindWeightRepository(impl: WeightRepositoryImpl): WeightRepository

    @Binds
    @Singleton
    abstract fun bindPreferencesRepository(impl: UserPreferencesDataSource): PreferencesRepository
}
