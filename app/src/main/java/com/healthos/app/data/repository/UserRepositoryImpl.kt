package com.healthos.app.data.repository

import com.healthos.app.data.local.dao.UserDao
import com.healthos.app.data.local.entity.UserEntity
import com.healthos.app.domain.model.User
import com.healthos.app.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDate
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
) : UserRepository {

    override fun observeUser(): Flow<User?> =
        userDao.observeUser().map { it?.toDomain() }

    override suspend fun saveUser(user: User) {
        userDao.upsert(user.toEntity())
    }
}

private fun UserEntity.toDomain(): User = User(
    name = name,
    birthDate = LocalDate.ofEpochDay(birthDate),
    heightCm = height,
    createdAt = Instant.ofEpochMilli(createdAt),
    updatedAt = Instant.ofEpochMilli(updatedAt),
)

private fun User.toEntity(): UserEntity = UserEntity(
    id = User.SINGLE_USER_ID,
    name = name,
    birthDate = birthDate.toEpochDay(),
    height = heightCm,
    createdAt = createdAt.toEpochMilli(),
    updatedAt = updatedAt.toEpochMilli(),
)
