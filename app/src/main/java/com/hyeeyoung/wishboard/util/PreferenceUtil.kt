package com.hyeeyoung.wishboard.util

import android.content.Context
import android.content.SharedPreferences

class PreferenceUtil(context: Context) {
    companion object {
        private const val PREF_USER_ID = "user_id"
        private const val PREF_USER_EMAIL = "email"
        private const val PREF_USER_CART = "isChecked_cartBtn"
        private const val PREF_FCM_TOKEN = "fcm_token"
    }

    private val prefs: SharedPreferences =
        context.getSharedPreferences("preferences", Context.MODE_PRIVATE)

    // TODO 이메일 -> 유저정보로 변경
    fun setUserEmail(email: String) {
        prefs.edit().putString(PREF_USER_EMAIL, email).apply()
    }

    fun getUserEmail(): String? {
        return prefs.getString(PREF_USER_EMAIL, null)
    }

    fun clearUserInfo() {
        prefs.edit().clear().apply()
    }
}