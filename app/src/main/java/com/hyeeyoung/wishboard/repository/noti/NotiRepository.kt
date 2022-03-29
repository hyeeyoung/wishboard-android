package com.hyeeyoung.wishboard.repository.noti

import com.hyeeyoung.wishboard.model.noti.NotiItem

interface NotiRepository {
    suspend fun fetchPreviousNotiList(token: String): List<NotiItem>?
    suspend fun updateNotiReadState(token: String, itemId: Long)
    suspend fun updatePushState(token: String, isSet: Boolean)
}