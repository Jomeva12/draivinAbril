package com.jomeva.driving.activities.channel

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.jomeva.driving.R


class NotificationHelper(context: Context?) : ContextWrapper(context) {
    var manager: NotificationManager? = null
        get() {
            if (field == null) {
                field = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            }
            return field
        }
        private set
    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannels()
        }
    }
    companion object {
        private const val CHANNEL_ID = "com.jomeva.driving"
        private const val CHANNEL_NAME = "Draivin"
    }
@RequiresApi(api = Build.VERSION_CODES.O)
    private fun createChannels() {
        val notificationChannel =
           /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {*/
            NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
      /*  } else {

        }*/
        notificationChannel.enableLights(true)
        notificationChannel.enableVibration(true)
        notificationChannel.lightColor = Color.GRAY
        notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        manager!!.createNotificationChannel(notificationChannel)
    }

    fun getNotificationOldApi( intent:PendingIntent,sound: Uri, title: String?, body: String?): NotificationCompat.Builder{
        return NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(sound)
            .setContentIntent(intent)
            .setSmallIcon(R.mipmap.launcher)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body).setBigContentTitle(title))
            //.setStyle(NotificationCompat.BigTextStyle().bigText(body).setBigContentTitle(title))
    }

    fun getNotificationOldApiAction( sound: Uri, title: String?, body: String?): NotificationCompat.Builder{
        return NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(sound)
            .setSmallIcon(R.mipmap.launcher)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body).setBigContentTitle(title))
        //.setStyle(NotificationCompat.BigTextStyle().bigText(body).setBigContentTitle(title))
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    fun getNotification(intent:PendingIntent, sound: Uri, title: String?, body: String?):Notification.Builder{
        return  Notification.Builder(applicationContext, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(sound)
                .setContentIntent(intent)
                .setSmallIcon(R.mipmap.launcher)
                .setStyle(Notification.BigTextStyle().bigText(body).setBigContentTitle(title))
        //} else {

       // }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getNotificationAction(sound: Uri, title: String?, body: String?):Notification.Builder{
        Log.d("mapassss2", "$title")

        return    Notification.Builder(applicationContext!!, CHANNEL_ID!!)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(sound)
               // .addAction(acceptAction)
                .setSmallIcon(R.mipmap.launcher)
                .setStyle(Notification.BigTextStyle().bigText(body).setBigContentTitle(title))

    }

}


