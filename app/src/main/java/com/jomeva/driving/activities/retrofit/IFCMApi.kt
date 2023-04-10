package com.jomeva.driving.activities.retrofit

import com.jomeva.driving.activities.modelos.FCMBody
import com.jomeva.driving.activities.modelos.FCMResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST


interface IFCMApi {
    @Headers(
        "Content-Type:application/json",
        "Authorization:key=AAAADK6OMIw:APA91bEggc3hrJ56uccaNvNZdDmcqnAqVMK-G1NM6iYhZheyezkfJITqEpUUcjC-8nPMyTkGOKmOWdJ6ItSKAmLQlQi-4mKlUHdnrldSmU_UzRMtNI7xrVZzmVcRugFrdSCX_89VQevA".toString()
    )
    @POST("fcm/send")
    fun send(@Body body: FCMBody?): Call<FCMResponse?>?
}

