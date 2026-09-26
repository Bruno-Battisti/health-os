package com.healthos.app.domain.repository

import com.healthos.app.domain.model.WeightEntry
import kotlinx.coroutines.flow.Flow

interface WeightRepository {
    suspend fun addEntry(entry: WeightEntry)
    suspend fun updateEntry(entry: WeightEntry)
    suspend fun deleteEntry(id: Long)
    fun observeLatest(userId: Long): Flow<WeightEntry?>
    fun observeHistory(userId: Long): Flow<List<WeightEntry>>
}
