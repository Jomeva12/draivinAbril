package com.jomeva.driving.activities.providers


import com.jomeva.driving.activities.modelos.FCMBody
import com.jomeva.driving.activities.modelos.FCMResponse
import com.jomeva.driving.activities.retrofit.IFCMApi
import com.jomeva.driving.activities.retrofit.RetrofitClient.getClient
import retrofit2.Call


class NotificationProvider {
    private val url = "https://fcm.googleapis.com"
    fun sendNotification(body: FCMBody?): Call<FCMResponse?>? {
        return getClient(url).create(IFCMApi::class.java).send(body)
    }
}

