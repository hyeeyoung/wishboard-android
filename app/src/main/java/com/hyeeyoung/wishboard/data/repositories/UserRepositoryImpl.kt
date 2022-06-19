package com.hyeeyoung.wishboard.data.repositories

import android.util.Log
import com.hyeeyoung.wishboard.data.model.user.UserInfo
import com.hyeeyoung.wishboard.data.services.retrofit.UserService
import com.hyeeyoung.wishboard.domain.repositories.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(private val userService: UserService) :
    UserRepository {
    override suspend fun fetchUserInfo(userToken: String): UserInfo? {
        try {
            val response = userService.fetchUserInfo(userToken) ?: return null
            if (response.isSuccessful) {
                Log.d(TAG, "사용자 정보 불러오기 성공")
            } else {
                Log.e(TAG, "사용자 정보 불러오기 실패: ${response.code()}")
            }
            return response.body()?.get(0)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override suspend fun registerFCMToken(userToken: String, fcmToken: String?): Boolean {
        try {
            val response = userService.updateFCMToken(userToken, fcmToken)
            if (response.isSuccessful) {
                Log.d(TAG, "FCM 토큰 등록 성공")
            } else {
                Log.e(TAG, "FCM 토큰 등록 실패: ${response.code()}")
            }
            return response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    override suspend fun updateUserInfo(
        userToken: String,
        nickname: String?,
        imageFileName: String?
    ): Pair<Boolean, Int>? {
        try {
            val userInfo = UserInfo(nickname = nickname, profileImage = imageFileName)
            val response = userService.updateUserInfo(userToken, userInfo)
            if (response.isSuccessful) {
                Log.d(TAG, "사용자 정보 수정 성공")
            } else {
                Log.e(TAG, "사용자 정보 수정 실패: ${response.code()}")
            }
            return Pair(response.isSuccessful, response.code())
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override suspend fun deleteUserAccount(userToken: String): Boolean {
        try {
            val response = userService.deleteUserAccount(userToken)
            if (response.isSuccessful) {
                Log.d(TAG, "사용자 탈퇴 처리 성공")
            } else {
                Log.e(TAG, "사용자 탈퇴 처리 실패: ${response.code()}")
            }
            return response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    companion object {
        private const val TAG = "UserRepositoryImpl"
    }
}