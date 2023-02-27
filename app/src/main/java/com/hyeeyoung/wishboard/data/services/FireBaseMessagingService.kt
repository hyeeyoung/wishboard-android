package com.hyeeyoung.wishboard.data.services

import android.content.Context
import android.provider.Settings
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.hyeeyoung.wishboard.util.NotificationUtil
import timber.log.Timber

class FireBaseMessagingService : FirebaseMessagingService() {
    /** 사용자 디바이스의 시스템 설정 > '날짜 및 시간'이 자동으로 되어 있는지 확인 (자동이 아닌 경우 알림이 잘못된 시간에 표시될 수 있음) */
    private fun isTimeAutomatic(context: Context): Boolean {
        return Settings.Global.getInt(
            context.contentResolver,
            Settings.Global.AUTO_TIME,
            0
        ) == 1
    }

    /** 포그라운드 상태인 앱에서 알림 메시지 또는 데이터 메시지를 수신 */
    override fun onMessageReceived(msg: RemoteMessage) {
        val title = msg.notification?.title
        val body = msg.notification?.body
        if (!isTimeAutomatic(applicationContext)) {
            Timber.d("`Automatic Date and Time` is not enabled")
            return
        }
        showNotification(title, body)
    }

    /** 푸시알림 생성 */
    private fun showNotification(title: String?, message: String?) {
        NotificationUtil(applicationContext).showNotification(title, message)
    }

    /** 대기중인 메세지를 삭제 시 호출 */
    override fun onDeletedMessages() {
        super.onDeletedMessages()
    }

    /** 새 토큰 생성 및 토큰 갱신 */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Timber.d("Refreshed token: $token")
    }
}