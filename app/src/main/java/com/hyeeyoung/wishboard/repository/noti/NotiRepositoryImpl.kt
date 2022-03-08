package com.hyeeyoung.wishboard.repository.noti

import android.util.Log
import com.hyeeyoung.wishboard.model.noti.NotiItem
import com.hyeeyoung.wishboard.service.RemoteService

class NotiRepositoryImpl : NotiRepository {
    private val api = RemoteService.api

    override suspend fun fetchNotiList(token: String): List<NotiItem>? {
        try {
            val response = api.fetchNotiList(token) ?: return null
            if (response.isSuccessful) {
                Log.d(TAG, "알림 가져오기 성공")
            } else {
                Log.e(TAG, "알림 가져오기 실패: ${response.code()}")
            }
            return response.body()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override suspend fun updateNotiReadState(token: String, itemId: Long) {
        try {
            val response = api.updateNotiReadState(token, itemId)
            if (response.isSuccessful) {
                Log.d(TAG, "알림 읽음 처리 성공")
            } else {
                Log.e(TAG, "알림 읽음 처리 실패: ${response.code()}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun updatePushNotiSettings(token: String, isSet: Boolean) {
        try {
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
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val TAG = "NotiRepositoryImpl"
    }
}