package com.healthos.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.healthos.app.data.local.entity.MeasurementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MeasurementDao {

    @Insert
    suspend fun insert(measurement: MeasurementEntity): Long

    @Update
    suspend fun update(measurement: MeasurementEntity)

    @Query("DELETE FROM measurements WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("SELECT * FROM measurements WHERE userId = :userId AND type = :type ORDER BY date DESC")
    fun observeHistory(userId: Long, type: String): Flow<List<MeasurementEntity>>
}
