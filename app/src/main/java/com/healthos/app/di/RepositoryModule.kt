package com.healthos.app.di

import com.healthos.app.data.preferences.UserPreferencesDataSource
import com.healthos.app.data.repository.GoalRepositoryImpl
import com.healthos.app.data.repository.HabitRepositoryImpl
import com.healthos.app.data.repository.MeasurementRepositoryImpl
import com.healthos.app.data.repository.UserRepositoryImpl
import com.healthos.app.data.repository.WeightRepositoryImpl
import com.healthos.app.data.repository.WorkoutRepositoryImpl
import com.healthos.app.domain.repository.GoalRepository
import com.healthos.app.domain.repository.HabitRepository
import com.healthos.app.domain.repository.MeasurementRepository
import com.healthos.app.domain.repository.PreferencesRepository
import com.healthos.app.domain.repository.UserRepository
import com.healthos.app.domain.repository.WeightRepository
import com.healthos.app.domain.repository.WorkoutRepository
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

    @Binds
    @Singleton
    abstract fun bindMeasurementRepository(impl: MeasurementRepositoryImpl): MeasurementRepository

    @Binds
    @Singleton
    abstract fun bindWorkoutRepository(impl: WorkoutRepositoryImpl): WorkoutRepository

    @Binds
    @Singleton
    abstract fun bindHabitRepository(impl: HabitRepositoryImpl): HabitRepository

    @Binds
    @Singleton
    abstract fun bindGoalRepository(impl: GoalRepositoryImpl): GoalRepository
}
