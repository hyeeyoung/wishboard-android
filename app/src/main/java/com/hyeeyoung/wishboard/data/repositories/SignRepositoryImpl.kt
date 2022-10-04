package com.hyeeyoung.wishboard.data.repositories

import com.hyeeyoung.wishboard.WishBoardApp
import com.hyeeyoung.wishboard.data.model.user.UserInfo
import com.hyeeyoung.wishboard.data.services.retrofit.AuthService
import com.hyeeyoung.wishboard.domain.repositories.SignRepository
import timber.log.Timber
import javax.inject.Inject

class SignRepositoryImpl @Inject constructor(private val authService: AuthService) :
    SignRepository {
    override suspend fun signUp(email: String, password: String): Boolean {
        try {
            val response = authService.signUpUser(UserInfo(email = email, password = password))
            val result = response.body()
            // TODO 네트워크에 연결되지 않은 경우 예외처리 필요
            if (response.isSuccessful) {
                Timber.d("회원가입 성공")
                result?.data?.token?.let {
                    WishBoardApp.prefs.setUserInfo(it, email)
                }
            } else {
                Timber.e("회원가입 실패: ${response.code()}")
            }
            return response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    override suspend fun signIn(email: String, password: String): Boolean {
        try {
            val response = authService.signInUser(UserInfo(email = email, password = password))
            val result = response.body()
            if (response.isSuccessful) {
                Timber.d("로그인 성공")
                result?.data?.token?.let {
                    WishBoardApp.prefs.setUserInfo(it, email)
                }
            } else {
                Timber.e("로그인 실패: ${response.code()}")
            }
            return response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    override suspend fun signInEmail(email: String): Boolean {
        try {
            val response = authService.signInEmail(true, email)
            val result = response.body()
            if (response.isSuccessful) {
                Timber.d("이메일 인증 로그인 성공")
                result?.data?.token?.let {
                    WishBoardApp.prefs.setUserInfo(it, email)
                }
                // TODO save the pushState
            } else {
                Timber.e("이메일 인증 로그인 실패: ${response.code()}")
            }
            return response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    override suspend fun requestVerificationMail(email: String): Pair<Pair<Boolean, String?>, Int>? {
        try {
            val response = authService.requestVerificationMail(email)
            val result = response.body()
            if (response.isSuccessful) {
                Timber.d("인증메일 요청 성공")
            } else {
                Timber.e("인증메일 요청 실패: ${response.code()}")
            }
            return Pair(
                Pair(response.isSuccessful, result?.data?.verificationCode),
                response.code()
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override suspend fun checkRegisteredUser(email: String): Pair<Boolean, Int>? {
        try {
            val response = authService.checkRegisteredUser(email)
            if (response.isSuccessful) {
                Timber.d("가입 가능 검사 성공")
            } else {
                Timber.e("가입 가능 검사 실패: ${response.code()}")
            }
            return Pair(response.isSuccessful, response.code())
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}