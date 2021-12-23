package com.hyeeyoung.wishboard.util

import android.content.Context
import android.content.SharedPreferences

class PreferenceUtil(context: Context) {
    companion object {
        private const val PREF_USER_EMAIL = "email"
        private const val PREF_USER_TOKEN = "token"
        private const val PREF_FCM_TOKEN = "fcmToken"
    }

    private val prefs: SharedPreferences =
        context.getSharedPreferences("preferences", Context.MODE_PRIVATE)

    fun setUserInfo(token: String, email: String) {
        prefs.edit().putString(PREF_USER_TOKEN, token).apply()
        prefs.edit().putString(PREF_USER_EMAIL, email).apply()
    }

    fun getUserToken(): String? {
        return prefs.getString(PREF_USER_TOKEN, null)
    }

    fun getUserEmail(): String? {
        return prefs.getString(PREF_USER_EMAIL, null)
    }

    fun clearUserInfo() {
        prefs.edit().clear().apply()
    }
}