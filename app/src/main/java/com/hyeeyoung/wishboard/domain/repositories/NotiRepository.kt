package com.hyeeyoung.wishboard.domain.repositories

import com.hyeeyoung.wishboard.data.model.noti.NotiItem

interface NotiRepository {
    suspend fun fetchPreviousNotiList(token: String): List<NotiItem>?
    suspend fun fetchAllNotiList(token: String): List<NotiItem>?
    suspend fun updateNotiReadState(token: String, itemId: Long)
    suspend fun updatePushState(token: String, isSet: Boolean)
}