package com.healthos.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val birthDate: Long,
    val height: Float,
    val createdAt: Long,
    // Cloud sync bookkeeping (etapa 0.7): drives last-write-wins conflict resolution.
    val updatedAt: Long = System.currentTimeMillis(),
)
