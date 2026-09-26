package com.healthos.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.healthos.app.data.local.dao.ExerciseDao
import com.healthos.app.data.local.dao.ExerciseSetDao
import com.healthos.app.data.local.dao.MeasurementDao
import com.healthos.app.data.local.dao.UserDao
import com.healthos.app.data.local.dao.WeightDao
import com.healthos.app.data.local.dao.WorkoutDao
import com.healthos.app.data.local.entity.ExerciseEntity
import com.healthos.app.data.local.entity.ExerciseSetEntity
import com.healthos.app.data.local.entity.MeasurementEntity
import com.healthos.app.data.local.entity.UserEntity
import com.healthos.app.data.local.entity.WeightEntryEntity
import com.healthos.app.data.local.entity.WorkoutEntity

@Database(
    entities = [
        UserEntity::class,
        WeightEntryEntity::class,
        MeasurementEntity::class,
        WorkoutEntity::class,
        ExerciseEntity::class,
        ExerciseSetEntity::class,
    ],
    version = 3,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun weightDao(): WeightDao
    abstract fun measurementDao(): MeasurementDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun exerciseSetDao(): ExerciseSetDao
}
