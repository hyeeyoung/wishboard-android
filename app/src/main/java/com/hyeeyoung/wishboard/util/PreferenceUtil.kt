package com.hyeeyoung.wishboard.util

import android.content.Context
import android.content.SharedPreferences

class PreferenceUtil(context: Context) { // TODO need refactoring
    companion object {
        private const val PREF_USER_EMAIL = "email"
        private const val PREF_USER_TOKEN = "token"
        private const val PREF_USER_NICKNAME = "nickname"
        private const val PREF_FCM_TOKEN = "fcmToken"
    }

    private val prefs: SharedPreferences =
        context.getSharedPreferences("preferences", Context.MODE_PRIVATE)

    fun setUserInfo(token: String, email: String, nickname: String?) {
        prefs.edit().apply {
            putString(PREF_USER_TOKEN, "Bearer $token")
            putString(PREF_USER_EMAIL, email)
            nickname?.let { putString(PREF_USER_NICKNAME, it) }
        }.apply()
    }

    fun setUserNickName(nickname: String) {
        prefs.edit().putString(PREF_USER_NICKNAME, nickname).apply()
    }

    fun getUserToken(): String? {
        return prefs.getString(PREF_USER_TOKEN, null)
    }

    fun getUserEmail(): String? {
        return prefs.getString(PREF_USER_EMAIL, null)
    }

    fun getUserNickName(): String? {
        return prefs.getString(PREF_USER_NICKNAME, "wish${getUserEmail().hashCode().toString().substring(0, 6)}")
    }

    fun clearUserInfo() {
        prefs.edit().clear().apply()
    }

    fun setFCMToken(token: String) {
        prefs.edit().putString(PREF_FCM_TOKEN, token).apply()
    }

    fun getFCMToken(): String? {
        return prefs.getString(PREF_FCM_TOKEN, null)
    }
}