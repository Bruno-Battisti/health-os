package com.healthos.app.domain.usecase

import com.healthos.app.domain.model.User
import com.healthos.app.domain.repository.UserRepository
import javax.inject.Inject

class SaveUserProfileUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(user: User) = userRepository.saveUser(user)
}
