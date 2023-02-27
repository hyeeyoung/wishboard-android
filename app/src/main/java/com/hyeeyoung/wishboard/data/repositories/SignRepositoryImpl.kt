package com.hyeeyoung.wishboard.data.repositories

import com.hyeeyoung.wishboard.data.local.WishBoardPreference
import com.hyeeyoung.wishboard.data.model.auth.RequestAuth
import com.hyeeyoung.wishboard.data.model.auth.ResponseAuth
import com.hyeeyoung.wishboard.data.services.retrofit.AuthService
import com.hyeeyoung.wishboard.domain.repositories.SignRepository
import timber.log.Timber
import javax.inject.Inject

class SignRepositoryImpl @Inject constructor(
    private val authService: AuthService,
    private val localStorage: WishBoardPreference
) : SignRepository {
    override suspend fun signUp(email: String, password: String): Result<ResponseAuth?> =
        runCatching {
            authService.signUpUser(RequestAuth(email, password)).body()?.data
        }.onSuccess { response ->
            Timber.d("회원가입 성공")
            response?.let {
                localStorage.setUserInfo(
                    email,
                    it.tempNickname,
                    it.token.accessToken,
                    it.token.refreshToken
                )
            }
        }.onFailure {
            Timber.e("회원가입 실패: ${it.message}")
        }

    override suspend fun signIn(email: String, password: String): Result<ResponseAuth?> =
        runCatching {
            authService.signInUser(RequestAuth(email, password)).body()?.data
        }.onSuccess { response ->
            Timber.d("로그인 성공")
            response?.let {
                localStorage.setUserInfo(
                    email,
                    it.tempNickname,
                    it.token.accessToken,
                    it.token.refreshToken
                )
            }
        }.onFailure {
            Timber.e("로그인 실패: ${it.message}")
        }

    override suspend fun signInEmail(email: String): Result<ResponseAuth?> = runCatching {
        authService.signInEmail(true, email).body()?.data
    }.onSuccess { response ->
        Timber.d("이메일 인증 로그인 성공")
        response?.let {
            localStorage.setUserInfo(
                email,
                it.tempNickname,
                it.token.accessToken,
                it.token.refreshToken
            )
        }
    }.onFailure {
        Timber.e("이메일 인증 로그인 실패: ${it.message}")
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

    override suspend fun logout(): Result<Boolean?> = runCatching {
        authService.logout().isSuccessful
    }.onSuccess {
        Timber.d("로그아웃 성공")
        localStorage.clear()
    }.onFailure {
        Timber.e("로그아웃 실패")
    }
}