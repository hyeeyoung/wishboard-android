package com.hyeeyoung.wishboard.repository.noti

interface NotiRepository {
    suspend fun updatePushNotiSettings(token: String, isSet: Boolean)
}