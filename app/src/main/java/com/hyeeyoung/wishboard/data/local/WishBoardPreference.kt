package com.hyeeyoung.wishboard.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.databinding.ktx.BuildConfig
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WishBoardPreference @Inject constructor(@ApplicationContext context: Context) {
    private val masterKey = MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val dataStore: SharedPreferences =
        if (BuildConfig.DEBUG) context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        else EncryptedSharedPreferences.create(
            context,
            FILE_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

    var userEmail: String
        set(value) = dataStore.edit { putString(USER_EMAIL, value) }
        get() = dataStore.getString(USER_EMAIL, null) ?: ""

    var userNickname: String
        set(value) = dataStore.edit { putString(USER_NICKNAME, value) }
        get() = dataStore.getString(USER_NICKNAME, null) ?: ""

    var accessToken: String
        set(value) = dataStore.edit { putString(ACCESS_TOKEN, value) }
        get() = dataStore.getString(
            ACCESS_TOKEN,
            null
        ) ?: ""

    var refreshToken: String
        set(value) = dataStore.edit { putString(REFRESH_TOKEN, value) }
        get() = dataStore.getString(
            REFRESH_TOKEN,
            null
        ) ?: ""

    var isLogin: Boolean
        set(value) = dataStore.edit { putBoolean(IS_LOGIN, value) }
        get() = dataStore.getBoolean(IS_LOGIN, false)

    var fcmToken: String
        set(value) = dataStore.edit { putString(FCM_TOKEN, value) }
        get() = dataStore.getString(
            FCM_TOKEN,
            null
        ) ?: ""

    var eventCloseBtnClickTime: LocalDateTime?
        set(value) = dataStore.edit { putString(EVENT_X_BTN_CLICK_TIME, value.toString()) }
        get() = dataStore.getString(
            EVENT_X_BTN_CLICK_TIME, null
        )?.let {
            try {
                LocalDateTime.parse(it, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            } catch (e: DateTimeParseException) {
                Timber.e(e.message)
                null
            }
        }

    fun setUserInfo(email: String, nickname: String?, accessToken: String, refreshToken: String) {
        isLogin = true
        userEmail = email
        nickname?.let { userNickname = it }
        this.accessToken = accessToken
        this.refreshToken = refreshToken
    }

    fun updateToken(accessToken: String, refreshToken: String) {
        this.accessToken = accessToken
        this.refreshToken = refreshToken
    }

    fun clear() {
        dataStore.edit {
            clear()
        }
    }

    companion object {
        const val FILE_NAME = "wishboardPreferences"
        const val ACCESS_TOKEN = "accessToken"
        const val REFRESH_TOKEN = "refreshToken"
        const val FCM_TOKEN = "fcmToken"
        const val IS_LOGIN = "isLogin"
        const val USER_EMAIL = "userEmail"
        const val USER_NICKNAME = "userNickname"
        const val EVENT_X_BTN_CLICK_TIME = "eventXBtnClickTime"
    }
}