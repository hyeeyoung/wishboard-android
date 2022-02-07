package com.hyeeyoung.wishboard.remote

import android.content.Context
import android.provider.Settings
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.hyeeyoung.wishboard.util.NotificationUtil

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
        val title = msg.data["title"]
        val message = msg.data["message"]
        val itemId = msg.data["itemId"]
        if (!isTimeAutomatic(applicationContext)) {
            Log.d(TAG, "`Automatic Date and Time` is not enabled")
            return
        }

        val isScheduled = msg.data["isScheduled"] != null
        if (isScheduled) { // 예악한 상품 알림인 경우
            val scheduledTime = msg.data["scheduledTime"]
            scheduleAlarm(scheduledTime, title, message, itemId)
        } else { // 예약되지 않은 바로 전송될 알림인 경우 (사용자 전체에게 공지를 발송할 경우 사용)
            showNotification(title, message)
        }
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
        Log.d(TAG, "Refreshed token: $token")
    }

    /** schedule 알림 설정 */
    private fun scheduleAlarm(
        scheduledTimeString: String?,
        title: String?,
        message: String?,
        itemId: String?
    ) {
        // TODO not yet implemented
    }

    companion object {
        private const val TAG = "FireBaseMessagingService"
    }
}