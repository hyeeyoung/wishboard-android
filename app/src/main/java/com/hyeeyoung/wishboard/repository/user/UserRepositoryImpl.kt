package com.hyeeyoung.wishboard.repository.user

import android.util.Log
import com.hyeeyoung.wishboard.remote.RemoteService

class UserRepositoryImpl : UserRepository {
    private val api = RemoteService.api

    override suspend fun registerFCMToken(userToken: String, fcmToken: String): Boolean {
        val response = api.updateFCMToken(userToken, fcmToken)
        if (response.isSuccessful) {
            Log.d(TAG, "FCM 토큰 등록 성공")
        } else {
            Log.e(TAG, "FCM 토큰 등록 실패: ${response.code()}")
        }
        return response.isSuccessful
    }

    companion object {
        private const val TAG = "UserRepositoryImpl"
    }
}