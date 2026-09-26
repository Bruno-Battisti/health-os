package com.healthos.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.healthos.app.data.local.dao.UserDao
import com.healthos.app.data.local.dao.WeightDao
import com.healthos.app.data.local.entity.UserEntity
import com.healthos.app.data.local.entity.WeightEntryEntity

@Database(
    entities = [UserEntity::class, WeightEntryEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun weightDao(): WeightDao
}
