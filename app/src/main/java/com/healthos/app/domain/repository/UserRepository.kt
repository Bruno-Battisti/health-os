package com.healthos.app.domain.repository

import com.healthos.app.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun observeUser(): Flow<User?>
    suspend fun saveUser(user: User)
}
