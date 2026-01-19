package dev.cloudants.iulat.shared

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
private val Context.dataStore by preferencesDataStore(name = "user_prefs")

@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val USER_ID_KEY = stringPreferencesKey("user_id")
        private val IS_ADMIN_KEY = booleanPreferencesKey("is_admin")
        private val ADMIN_ID_KEY = stringPreferencesKey("admin_id")
    }

    val userIdFlow: Flow<String?> = context.dataStore.data.map { it[USER_ID_KEY] }
    val isAdminFlow: Flow<Boolean> = context.dataStore.data.map { it[IS_ADMIN_KEY] ?: false }
    val adminIdFlow: Flow<String?> = context.dataStore.data.map { it[ADMIN_ID_KEY] }

    suspend fun saveUserSession(userId: String, isAdmin: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[USER_ID_KEY] = userId
            prefs[IS_ADMIN_KEY] = isAdmin
        }
    }

    suspend fun saveGlobalAdminId(adminId: String) {
        context.dataStore.edit { it[ADMIN_ID_KEY] = adminId }
    }

    suspend fun clearSession() {
        context.dataStore.edit { it.clear() }
    }
}