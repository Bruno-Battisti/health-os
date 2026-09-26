package com.healthos.app.domain.usecase

import com.healthos.app.domain.model.User
import com.healthos.app.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserProfileUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    operator fun invoke(): Flow<User?> = userRepository.observeUser()
}
