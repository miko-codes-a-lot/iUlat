package dev.cloudants.iulat.lib.components.context

import android.content.Context
import android.content.SharedPreferences

object UserSession {
    private const val PREF_NAME = "user_session"
    private const val KEY_ROLE = "user_role"

    fun saveUserRole(context: Context, role: String) {
        val sharedPref: SharedPreferences =
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPref.edit().putString(KEY_ROLE, role).apply()
    }

    fun getUserRole(context: Context): String? {
        val sharedPref: SharedPreferences =
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPref.getString(KEY_ROLE, null)
    }

    fun clearSession(context: Context) {
        val sharedPref: SharedPreferences =
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPref.edit().clear().apply()
    }
}