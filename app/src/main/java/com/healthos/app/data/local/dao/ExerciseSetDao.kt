package com.healthos.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.healthos.app.data.local.entity.ExerciseSetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseSetDao {

    @Insert
    suspend fun insert(set: ExerciseSetEntity): Long

    @Update
    suspend fun update(set: ExerciseSetEntity)

    @Query("DELETE FROM exercise_sets WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("SELECT * FROM exercise_sets WHERE exerciseId = :exerciseId ORDER BY `order` ASC")
    fun observeByExercise(exerciseId: Long): Flow<List<ExerciseSetEntity>>

    @Query("SELECT * FROM exercise_sets WHERE exerciseId IN (:exerciseIds) ORDER BY `order` ASC")
    fun observeByExercises(exerciseIds: List<Long>): Flow<List<ExerciseSetEntity>>
}
