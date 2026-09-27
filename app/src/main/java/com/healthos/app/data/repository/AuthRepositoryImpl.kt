package com.healthos.app.data.repository

import android.content.Context
import com.healthos.app.data.preferences.AuthTokenStorage
import com.healthos.app.data.remote.AuthApi
import com.healthos.app.domain.model.AuthSession
import com.healthos.app.domain.repository.AuthRepository
import com.healthos.app.worker.SyncScheduler
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val tokenStorage: AuthTokenStorage,
    @ApplicationContext private val context: Context,
) : AuthRepository {

    override val session: StateFlow<AuthSession?> = tokenStorage.session

    override suspend fun register(email: String, password: String): Result<Unit> = runCatching {
        val response = authApi.register(email, password)
        tokenStorage.saveSession(AuthSession(response.token, response.accountId))
        SyncScheduler.schedulePeriodicSync(context)
    }

    override suspend fun login(email: String, password: String): Result<Unit> = runCatching {
        val response = authApi.login(email, password)
        tokenStorage.saveSession(AuthSession(response.token, response.accountId))
        SyncScheduler.schedulePeriodicSync(context)
    }

    override fun logout() {
        tokenStorage.clearSession()
        SyncScheduler.cancelPeriodicSync(context)
    }
}
