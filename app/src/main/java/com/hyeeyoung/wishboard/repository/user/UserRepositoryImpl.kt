package com.hyeeyoung.wishboard.repository.user

import android.util.Log
import com.hyeeyoung.wishboard.model.sign.UserInfo
import com.hyeeyoung.wishboard.remote.RemoteService

class UserRepositoryImpl : UserRepository {
    private val api = RemoteService.api

    override suspend fun fetchUserInfo(userToken: String): UserInfo? {
        val response = api.fetchUserInfo(userToken) ?: return null
        if (response.isSuccessful) {
            Log.d(TAG, "사용자 정보 불러오기 성공")
        } else {
            Log.e(TAG, "사용자 정보 불러오기 실패: ${response.code()}")
        }
        return response.body()?.get(0)
    }

    override suspend fun registerFCMToken(userToken: String, fcmToken: String): Boolean {
        val response = api.updateFCMToken(userToken, fcmToken)
        if (response.isSuccessful) {
            Log.d(TAG, "FCM 토큰 등록 성공")
        } else {
            Log.e(TAG, "FCM 토큰 등록 실패: ${response.code()}")
        }
        return response.isSuccessful
    }

    override suspend fun updateUserInfo(
        userToken: String,
        nickname: String?,
        imageFileName: String?
    ): Pair<Boolean, Int> {
        val userInfo = UserInfo(nickname = nickname, profileImage = imageFileName)
        val response = api.updateUserInfo(userToken, userInfo)
        if (response.isSuccessful) {
            Log.d(TAG, "사용자 정보 수정 성공")
        } else {
            Log.e(TAG, "사용자 정보 수정 실패: ${response.code()}")
        }
        return Pair(response.isSuccessful, response.code())
    }

    companion object {
        private const val TAG = "UserRepositoryImpl"
    }
}