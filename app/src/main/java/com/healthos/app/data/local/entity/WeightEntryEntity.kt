package com.healthos.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "weight_entries",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("userId")],
)
data class WeightEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val weight: Float,
    val date: Long,
    val note: String? = null,
    // Cloud sync bookkeeping (etapa 0.7): null until the entry has been pushed once.
    val remoteId: String? = null,
    val updatedAt: Long = System.currentTimeMillis(),
)
