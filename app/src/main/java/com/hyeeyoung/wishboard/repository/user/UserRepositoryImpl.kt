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

    override suspend fun updateUserNickname(userToken: String, nickname: String): Boolean {
        val response = api.updateUserNickname(userToken, nickname)
        if (response.isSuccessful) {
            Log.d(TAG, "닉네임 수정 성공")
        } else {
            Log.e(TAG, "닉네임 수정 실패: ${response.code()}")
        }
        return response.isSuccessful
    }
    
    companion object {
        private const val TAG = "UserRepositoryImpl"
    }
}