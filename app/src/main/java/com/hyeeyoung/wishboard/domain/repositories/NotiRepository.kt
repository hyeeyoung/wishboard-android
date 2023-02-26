package com.hyeeyoung.wishboard.domain.repositories

import com.hyeeyoung.wishboard.data.model.noti.NotiItem

interface NotiRepository {
    suspend fun fetchPreviousNotiList(): List<NotiItem>?
    suspend fun fetchAllNotiList(): List<NotiItem>?
    suspend fun updateNotiReadState(itemId: Long)
    suspend fun updatePushState(isSet: Boolean)
}