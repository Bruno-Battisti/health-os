package com.healthos.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.healthos.app.data.local.entity.WeightEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeightDao {

    @Insert
    suspend fun insert(entry: WeightEntryEntity): Long

    @Update
    suspend fun update(entry: WeightEntryEntity)

    @Query("DELETE FROM weight_entries WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("SELECT * FROM weight_entries WHERE userId = :userId ORDER BY date DESC LIMIT 1")
    fun observeLatest(userId: Long): Flow<WeightEntryEntity?>

    @Query("SELECT * FROM weight_entries WHERE userId = :userId ORDER BY date DESC")
    fun observeHistory(userId: Long): Flow<List<WeightEntryEntity>>

    @Query("SELECT * FROM weight_entries WHERE userId = :userId AND date >= :sinceEpochDay ORDER BY date ASC")
    fun observeSince(userId: Long, sinceEpochDay: Long): Flow<List<WeightEntryEntity>>

    @Query("SELECT * FROM weight_entries WHERE userId = :userId AND updatedAt > :since")
    suspend fun getUpdatedSince(userId: Long, since: Long): List<WeightEntryEntity>

    @Query("SELECT * FROM weight_entries WHERE remoteId = :remoteId LIMIT 1")
    suspend fun findByRemoteId(remoteId: String): WeightEntryEntity?

    @Query("UPDATE weight_entries SET remoteId = :remoteId WHERE id = :id")
    suspend fun assignRemoteId(id: Long, remoteId: String)
}
