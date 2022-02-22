package com.hyeeyoung.wishboard.repository.noti

import android.util.Log
import com.hyeeyoung.wishboard.remote.RemoteService

class NotiRepositoryImpl : NotiRepository {
    private val api = RemoteService.api

    override suspend fun updatePushNotiSettings(token: String, isSet: Boolean) {
        val response = api.updatePushNotiSettings(token, isSet)

        val onOff = if (isSet) {
            "켜기"
        } else {
            "끄기"
        }

        if (response.isSuccessful) {
            Log.d(TAG, "푸시 알림 $onOff 성공")
        } else {
            Log.e(TAG, "푸시 알림 $onOff 실패: ${response.code()}")
        }
    }

    companion object {
        private const val TAG = "NotiRepositoryImpl"
    }
}