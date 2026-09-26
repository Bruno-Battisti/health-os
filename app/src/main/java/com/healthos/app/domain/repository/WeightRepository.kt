package com.healthos.app.domain.repository

import com.healthos.app.domain.model.WeightEntry
import kotlinx.coroutines.flow.Flow

interface WeightRepository {
    suspend fun addEntry(entry: WeightEntry)
    fun observeLatest(userId: Long): Flow<WeightEntry?>
}
