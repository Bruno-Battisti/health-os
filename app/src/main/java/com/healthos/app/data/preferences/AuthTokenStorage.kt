package com.healthos.app.data.preferences

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.healthos.app.domain.model.AuthSession
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

private const val PREFERENCES_FILE_NAME = "auth_secure_preferences"
private const val KEY_TOKEN = "token"
private const val KEY_ACCOUNT_ID = "account_id"

/**
 * Holds the JWT session in EncryptedSharedPreferences rather than the app's regular DataStore,
 * since it is a secret and not a plain preference.
 */
@Singleton
class AuthTokenStorage @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val preferences = EncryptedSharedPreferences.create(
        context,
        PREFERENCES_FILE_NAME,
        MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )

    private val _session = MutableStateFlow(readSession())
    val session: StateFlow<AuthSession?> = _session.asStateFlow()

    fun currentToken(): String? = _session.value?.token

    fun saveSession(session: AuthSession) {
        preferences.edit()
            .putString(KEY_TOKEN, session.token)
            .putString(KEY_ACCOUNT_ID, session.accountId)
            .apply()
        _session.value = session
    }

    fun clearSession() {
        preferences.edit().clear().apply()
        _session.value = null
    }

    private fun readSession(): AuthSession? {
        val token = preferences.getString(KEY_TOKEN, null) ?: return null
        val accountId = preferences.getString(KEY_ACCOUNT_ID, null) ?: return null
        return AuthSession(token, accountId)
    }
}
