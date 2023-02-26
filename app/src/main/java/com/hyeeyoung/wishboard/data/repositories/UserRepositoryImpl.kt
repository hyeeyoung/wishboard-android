package com.hyeeyoung.wishboard.data.repositories

import com.hyeeyoung.wishboard.data.model.user.UserInfo
import com.hyeeyoung.wishboard.data.services.retrofit.UserService
import com.hyeeyoung.wishboard.domain.repositories.UserRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody
import timber.log.Timber
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(private val userService: UserService) :
    UserRepository {
    override suspend fun fetchUserInfo(): UserInfo? {
        try {
            val response = userService.fetchUserInfo() ?: return null
            if (response.isSuccessful) {
                Timber.d("사용자 정보 불러오기 성공")
            } else {
                Timber.e("사용자 정보 불러오기 실패: ${response.code()}")
            }
            return response.body()?.get(0)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override suspend fun registerFCMToken(fcmToken: String?): Boolean {
        try {
            val response = userService.updateFCMToken(fcmToken)
            if (response.isSuccessful) {
                Timber.d("FCM 토큰 등록 성공")
            } else {
                Timber.e("FCM 토큰 등록 실패: ${response.code()}")
            }
            return response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    override suspend fun updateUserInfo(
        nickname: RequestBody?,
        profileImg: MultipartBody.Part?
    ): Pair<Boolean, Int>? = runCatching {
        userService.updateUserInfo(nickname, profileImg)
    }.fold({
        Timber.d("사용자 프로필 수정 성공(${it.code()})")
        Pair(it.isSuccessful, it.code())
    }, {
        Timber.e("사용자 프로필 수정 실패: ${it.message}")
        null
    })

    override suspend fun deleteUserAccount() = runCatching {
        userService.deleteUserAccount()
    }.fold({
        Timber.d("사용자 탈퇴 처리 성공(${it.code()})")
        it.isSuccessful
    }, {
        Timber.e("사용자 탈퇴 처리 실패: ${it.message}")
        false
    })
}