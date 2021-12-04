package com.hyeeyoung.wishboard.repository.sign

import android.util.Log
import com.hyeeyoung.wishboard.model.SignInfo
import com.hyeeyoung.wishboard.remote.RemoteService

class SignRepositoryImpl : SignRepository {
    private val api = RemoteService.api

    override suspend fun signUp(email: String, password: String): Boolean {
        val response = api.signUpUser(SignInfo(email, password))
        //TODO 네트워크에 연결되지 않은 경우 예외처리 필요
        if (response.isSuccessful) {
            Log.d(TAG, "회원가입 성공")
        } else {
            Log.e("TAG", "회원가입 실페: ${response.code()}")
        }
        return response.isSuccessful
    }

    companion object {
        private val TAG = "SignRepositoryImpl"
    }
}