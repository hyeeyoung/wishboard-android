package com.hyeeyoung.wishboard.data.interceptor

import android.app.Application
import android.content.Intent
import com.google.gson.Gson
import com.hyeeyoung.wishboard.BuildConfig
import com.hyeeyoung.wishboard.data.local.WishBoardPreference
import com.hyeeyoung.wishboard.data.model.auth.ResponseRefresh
import com.hyeeyoung.wishboard.data.model.auth.Token
import com.hyeeyoung.wishboard.util.extension.toPlainRequestBody
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val gson: Gson,
    private val context: Application,
    private val localStorage: WishBoardPreference,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val authRequest =
            originalRequest.newBuilder()
                .addHeader(AUTHORIZATION, "Bearer ${localStorage.accessToken}").build()
        val response = chain.proceed(authRequest)

        when (response.code) {
            401 -> {
                val refreshTokenRequest = originalRequest.newBuilder().get()
                    .url("${BuildConfig.BASE_URL}auth/refresh")
                    .post(
                        Token(localStorage.accessToken, localStorage.refreshToken).toString()
                            .toPlainRequestBody()
                    )
                    .build()
                val refreshTokenResponse = chain.proceed(refreshTokenRequest)

                if (refreshTokenResponse.isSuccessful) {
                    Timber.d("토큰 리프레시 성공")
                    gson.fromJson(
                        refreshTokenResponse.body?.string(),
                        ResponseRefresh::class.java
                    ).also { responseRefresh ->
                        responseRefresh.token.let {
                            localStorage.updateToken(it.accessToken, it.refreshToken)
                        }
                    }

                    val newRequest =
                        originalRequest.newBuilder()
                            .addHeader(AUTHORIZATION, "Bearer ${localStorage.accessToken}")
                            .build()
                    return chain.proceed(newRequest)
                } else {
                    Timber.e("토큰 리프레시 실패(${refreshTokenResponse.message})")
                    with(context) {
                        CoroutineScope(Dispatchers.Main).launch {
                            startActivity(
                                Intent.makeRestartActivityTask(
                                    packageManager.getLaunchIntentForPackage(packageName)?.component
                                )
                            )
                            // TODO 자동 로그인 만료 토스트 띄우기
                            localStorage.clear()
                            cancel()
                        }
                    }
                }
            }
        }
        return response
    }

    companion object {
        private const val AUTHORIZATION = "Authorization"
    }
}
