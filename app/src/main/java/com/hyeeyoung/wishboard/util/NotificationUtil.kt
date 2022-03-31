package com.hyeeyoung.wishboard.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.hyeeyoung.wishboard.view.MainActivity
import com.hyeeyoung.wishboard.R
import java.util.*

class NotificationUtil(var context: Context) {
    // TODO 상태바에서 알림 클릭 시 알림화면(프래그먼트)이 바로 실행되도록 해야함
    fun showNotification(title: String?, message: String?) {
        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        // 알림 아이콘, 알림 내용, 알림음 등 푸시알림 부가 설정
        val channelId = context.getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val builder = NotificationCompat.Builder(
            context, channelId
        )
            .setSmallIcon(R.mipmap.ic_main)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // NotificationChannel 생성, 오레오(8.0) 이상일 경우 채널을 반드시 생성해야 함
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Default Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(Random().nextInt(), builder.build())
    }

    companion object {
        const val NOTIFICATION_TITLE = "title"
        const val NOTIFICATION_MESSAGE = "message"
        const val NOTIFICATION_ITEM_ID = "itemId"
    }
}