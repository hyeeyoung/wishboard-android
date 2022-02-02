package com.hyeeyoung.wishboard.repository.sign

import android.util.Log
import com.hyeeyoung.wishboard.model.sign.SignInfo
import com.hyeeyoung.wishboard.remote.RemoteService
import com.hyeeyoung.wishboard.util.prefs

class SignRepositoryImpl : SignRepository {
    private val api = RemoteService.api

    override suspend fun signUp(email: String, password: String): Boolean {
        val response = api.signUpUser(SignInfo(email, password))
        val result = response.body()
        //TODO 네트워크에 연결되지 않은 경우 예외처리 필요
        if (response.isSuccessful) {
            Log.d(TAG, "회원가입 성공")
            result?.data?.let {
                prefs?.setUserInfo(it, email)
            }
        } else {
            Log.e(TAG, "회원가입 실패: ${response.code()}")
        }
        return response.isSuccessful
    }

    override suspend fun signIn(email: String, password: String): Boolean {
        val response = api.signInUser(SignInfo(email, password))
        val result = response.body()
        if (response.isSuccessful) {
            Log.d(TAG, "로그인 성공")
            result?.data?.let { prefs?.setUserInfo(it, email) }
        } else {
            Log.e(TAG, "로그인 실패: ${response.code()}")
        }
        return response.isSuccessful
    }

    companion object {
        private const val TAG = "SignRepositoryImpl"
    }
}