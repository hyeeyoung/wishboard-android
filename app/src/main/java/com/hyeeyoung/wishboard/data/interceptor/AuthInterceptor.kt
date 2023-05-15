package com.hyeeyoung.wishboard.data.interceptor

import android.app.Application
import android.content.Intent
import com.google.gson.Gson
import com.hyeeyoung.wishboard.BuildConfig
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.data.local.WishBoardPreference
import com.hyeeyoung.wishboard.data.model.auth.ResponseRefresh
import com.hyeeyoung.wishboard.util.extension.showToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import okhttp3.FormBody
import okhttp3.Interceptor
import okhttp3.Request
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
        val authRequest = originalRequest.newAuthBuilder().build()
        val response = chain.proceed(authRequest)

        when (response.code) {
            401 -> {
                response.close()
                val requestBody = FormBody.Builder()
                    .add(REFRESH_TOKEN, localStorage.refreshToken).build()

                val refreshTokenRequest = originalRequest.newBuilder().get()
                    .url("${BuildConfig.BASE_URL}auth/refresh")
                    .post(requestBody)
                    .build()
                val refreshTokenResponse = chain.proceed(refreshTokenRequest)

                if (refreshTokenResponse.isSuccessful) {
                    Timber.d("토큰 리프레시 성공")
                    val responseRefresh = gson.fromJson(
                        refreshTokenResponse.body?.string(),
                        ResponseRefresh::class.java
                    )

                    responseRefresh.data?.token?.let {
                        localStorage.updateToken(it.accessToken, it.refreshToken)
                    }
                    refreshTokenResponse.close()

                    val newRequest = originalRequest.newAuthBuilder().build()
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
                            showToast(getString(R.string.sign_auto_login_failure))
                            localStorage.clear()
                            cancel()
                        }
                    }
                }
            }
        }
        return response
    }

    private fun Request.newAuthBuilder() =
        this.newBuilder()
            .addHeader(AUTHORIZATION, "$TOKEN_PREF${localStorage.accessToken}")

    companion object {
        private const val AUTHORIZATION = "Authorization"
        private const val TOKEN_PREF = "Bearer "
        private const val REFRESH_TOKEN = "refreshToken"
    }
}
