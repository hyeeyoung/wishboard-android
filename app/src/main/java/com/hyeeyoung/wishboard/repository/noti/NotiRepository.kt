package com.hyeeyoung.wishboard.repository.noti

import com.hyeeyoung.wishboard.model.noti.NotiItem

interface NotiRepository {
    suspend fun fetchNotiList(token: String): List<NotiItem>?
    suspend fun updatePushNotiSettings(token: String, isSet: Boolean)
}