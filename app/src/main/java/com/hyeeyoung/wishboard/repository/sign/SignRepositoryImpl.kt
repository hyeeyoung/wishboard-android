package com.hyeeyoung.wishboard.repository.sign

import android.util.Log
import com.hyeeyoung.wishboard.model.user.UserInfo
import com.hyeeyoung.wishboard.remote.RemoteService
import com.hyeeyoung.wishboard.util.prefs

class SignRepositoryImpl : SignRepository {
    private val api = RemoteService.api

    override suspend fun signUp(email: String, password: String): Boolean {
        try {
            val response = api.signUpUser(UserInfo(email = email, password = password))
            val result = response.body()
            // TODO 네트워크에 연결되지 않은 경우 예외처리 필요
            if (response.isSuccessful) {
                Log.d(TAG, "회원가입 성공")
                result?.data?.token?.let {
                    prefs?.setUserInfo(it, email)
                }
            } else {
                Log.e(TAG, "회원가입 실패: ${response.code()}")
            }
            return response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    override suspend fun signIn(email: String, password: String): Boolean {
        try {
            val response = api.signInUser(UserInfo(email = email, password = password))
            val result = response.body()
            if (response.isSuccessful) {
                Log.d(TAG, "로그인 성공")
                result?.data?.token?.let {
                    prefs?.setUserInfo(it, email)
                }
            } else {
                Log.e(TAG, "로그인 실패: ${response.code()}")
            }
            return response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    override suspend fun signInEmail(email: String): Boolean {
        try {
            val response = api.signInEmail(true, email)
            val result = response.body()
            if (response.isSuccessful) {
                Log.d(TAG, "이메일 인증 로그인 성공")
                result?.data?.token?.let {
                    prefs?.setUserInfo(it, email)
                }
                // TODO save the pushState
            } else {
                Log.e(TAG, "이메일 인증 로그인 실패: ${response.code()}")
            }
            return response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    override suspend fun requestVerificationMail(email: String): Pair<Pair<Boolean, String?>, Int>? {
        try {
            val response = api.requestVerificationMail(email)
            val result = response.body()
            if (response.isSuccessful) {
                Log.d(TAG, "인증메일 요청 성공")
            } else {
                Log.e(TAG, "인증메일 요청 실패: ${response.code()}")
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
            val response = api.checkRegisteredUser(email)
            if (response.isSuccessful) {
                Log.d(TAG, "가입 가능 검사 성공")
            } else {
                Log.e(TAG, "가입 가능 검사 실패: ${response.code()}")
            }
            return Pair(response.isSuccessful, response.code())
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    companion object {
        private const val TAG = "SignRepositoryImpl"
    }
}