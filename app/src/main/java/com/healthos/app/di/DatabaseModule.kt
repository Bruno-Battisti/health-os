package com.healthos.app.di

import android.content.Context
import androidx.room.Room
import com.healthos.app.data.local.AppDatabase
import com.healthos.app.data.local.dao.UserDao
import com.healthos.app.data.local.dao.WeightDao
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
        Room.databaseBuilder(context, AppDatabase::class.java, "health_os.db").build()

    @Provides
    fun provideUserDao(database: AppDatabase): UserDao = database.userDao()

    @Provides
    fun provideWeightDao(database: AppDatabase): WeightDao = database.weightDao()
}
