package com.hyeeyoung.wishboard.data.repositories

import com.hyeeyoung.wishboard.data.model.noti.NotiItem
import com.hyeeyoung.wishboard.data.services.retrofit.NotiService
import com.hyeeyoung.wishboard.data.services.retrofit.UserService
import com.hyeeyoung.wishboard.domain.repositories.NotiRepository
import timber.log.Timber
import javax.inject.Inject

class NotiRepositoryImpl @Inject constructor(
    private val notiService: NotiService,
    private val userService: UserService
) : NotiRepository {
    override suspend fun fetchPreviousNotiList(token: String): List<NotiItem>? {
        try {
            val response = notiService.fetchPreviousNotiList(token) ?: return null
            if (response.isSuccessful) {
                Timber.d("지난 알림 가져오기 성공")
            } else {
                Timber.e("지난 알림 가져오기 실패: ${response.code()}")
            }
            return response.body()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override suspend fun fetchAllNotiList(token: String): List<NotiItem>? {
        try {
            val response = notiService.fetchAllNotiList(token) ?: return null
            if (response.isSuccessful) {
                Timber.d("모든 알림 가져오기 성공")
            } else {
                Timber.e("모든 알림 가져오기 실패: ${response.code()}")
            }
            return response.body()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override suspend fun updateNotiReadState(token: String, itemId: Long) {
        try {
            val response = notiService.updateNotiReadState(token, itemId)
            if (response.isSuccessful) {
                Timber.d("알림 읽음 처리 성공")
            } else {
                Timber.e("알림 읽음 처리 실패: ${response.code()}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun updatePushState(token: String, isSet: Boolean) {
        try {
            val response = userService.updatePushState(token, isSet)

            val onOff = if (isSet) {
                "켜기"
            } else {
                "끄기"
            }

            if (response.isSuccessful) {
                Timber.d("푸시 알림 $onOff 성공")
            } else {
                Timber.e("푸시 알림 $onOff 실패: ${response.code()}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}