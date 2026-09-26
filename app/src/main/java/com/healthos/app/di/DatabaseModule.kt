package com.healthos.app.di

import android.content.Context
import androidx.room.Room
import com.healthos.app.data.local.AppDatabase
import com.healthos.app.data.local.dao.ExerciseDao
import com.healthos.app.data.local.dao.ExerciseSetDao
import com.healthos.app.data.local.dao.GoalCheckInDao
import com.healthos.app.data.local.dao.GoalDao
import com.healthos.app.data.local.dao.HabitDao
import com.healthos.app.data.local.dao.HabitEntryDao
import com.healthos.app.data.local.dao.MeasurementDao
import com.healthos.app.data.local.dao.UserDao
import com.healthos.app.data.local.dao.WeightDao
import com.healthos.app.data.local.dao.WorkoutDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "health_os.db")
            // Pre-v1.0: no released data to preserve yet, so schema changes destroy and recreate instead of migrating.
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()

    @Provides
    fun provideUserDao(database: AppDatabase): UserDao = database.userDao()

    @Provides
    fun provideWeightDao(database: AppDatabase): WeightDao = database.weightDao()

    @Provides
    fun provideMeasurementDao(database: AppDatabase): MeasurementDao = database.measurementDao()

    @Provides
    fun provideWorkoutDao(database: AppDatabase): WorkoutDao = database.workoutDao()

    @Provides
    fun provideExerciseDao(database: AppDatabase): ExerciseDao = database.exerciseDao()

    @Provides
    fun provideExerciseSetDao(database: AppDatabase): ExerciseSetDao = database.exerciseSetDao()

    @Provides
    fun provideHabitDao(database: AppDatabase): HabitDao = database.habitDao()

    @Provides
    fun provideHabitEntryDao(database: AppDatabase): HabitEntryDao = database.habitEntryDao()

    @Provides
    fun provideGoalDao(database: AppDatabase): GoalDao = database.goalDao()

    @Provides
    fun provideGoalCheckInDao(database: AppDatabase): GoalCheckInDao = database.goalCheckInDao()
}
