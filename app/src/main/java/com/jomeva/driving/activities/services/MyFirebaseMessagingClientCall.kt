package com.jomeva.driving.activities.services

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.jomeva.driving.activities.channel.NotificationHelper
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.jomeva.driving.activities.util.PreferenciasGenerales


class MyFirebaseMessagingClientCall : FirebaseMessagingService() {
    var claseComentario:String=""
    override fun onNewToken(s: String) {
        super.onNewToken(s)
    }

    companion object {
        val NOTIFICATIO_CODE = 100
    }


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
val notificacion: RemoteMessage.Notification? =remoteMessage.notification

        var data = remoteMessage.data
        val title = data["title"]
        val body = data["body"]
        var tipo = data["tipo"]


        if (title != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                when (tipo) {
                    "mensaje" -> {
                        //cambiar sonnido investigar si esty dentro de el chat

                        if (data["idChat"] ==  PreferenciasGenerales.getIdChat()) {

                         } else {

                            showNotificationApiOreoAction(title, body)
                        }

                    }
                    "Evento" -> {

                        showNotificationApiOreoAction(title, body)
                    }
                    "Propina" -> {

                        showNotificationApiOreoAction(title, body)
                    }
                    "comentarioPost" -> {

                        claseComentario="comentarioPost"
                        showNotificationApiOreoAction(title, body)
                    }
                    "comentarioPropina" -> {

                        showNotificationApiOreoAction(title, body)
                    }
                    "llamada" -> {

                    }
                    "videollamada" -> {

                    }

                    else -> {
                        "Nuevodaa mensaje"
                    }

                }

            }else{
                when (tipo) {
                    "mensaje" -> {

                        if (data["idChat"] ==  PreferenciasGenerales.getIdChat()) {

                        } else {
                            showNotificationAction(title, body)
                        }

                    }
                    "Evento" -> {

                        showNotificationAction(title, body)
                    }
                    "Propina" -> {

                        showNotificationAction(title, body)
                    }


                    "comentarioPost" -> {

                        claseComentario="comentarioPost"
                        showNotificationAction(title, body)
                    }
                    "comentarioPropina" -> {

                        showNotificationAction(title, body)
                    }
                    "llamada" -> {

                    }
                    "videollamada" -> {

                    }

                    else -> {
                        "Nuevodaa mensaje"
                    }

                }
            }
        }

    }



    private fun showNotificationAction(title: String,body: String?,) {
        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        var notificationHelper = NotificationHelper(baseContext)
        var builder:NotificationCompat.Builder = notificationHelper.getNotificationOldApiAction( uri, title, body)
        notificationHelper.manager?.notify(1, builder.build())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showNotificationApiOreoAction( title: String,body: String?) {
        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        var notificationHelper = NotificationHelper(baseContext)
        var builder = notificationHelper.getNotificationAction( uri, title, body)
        notificationHelper.manager?.notify(2, builder.build())
    }




    /*private fun showNotification(title: String, body: String?, tipo: String?, boton: String?) {
        val intent: PendingIntent =
            PendingIntent.getActivity(baseContext, 0, Intent(), PendingIntent.FLAG_ONE_SHOT)
        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        var notificationHelper = NotificationHelper(baseContext)
        var builder = notificationHelper.getNotificationOldApi(intent, uri, title, body)
        notificationHelper.manager?.notify(1, builder.build())
    }*/
   /* private fun showNotificationApiOreo(
        title: String,
        body: String?,
        tipo: String?,
        boton: String?
    ) {
        val intent: PendingIntent =
            PendingIntent.getActivity(baseContext, 0, Intent(), PendingIntent.FLAG_ONE_SHOT)
        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        var notificationHelper = NotificationHelper(baseContext)
        var builder = notificationHelper.getNotification(intent, uri, title, body)
        notificationHelper.manager?.notify(1, builder.build())
    }*/
}
