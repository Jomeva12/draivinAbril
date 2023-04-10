package com.jomeva.driving.activities.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log

class CommonKotlin {
    companion object{


    fun isConnectToInternet(context: Context): Boolean {
        Log.d("atento2", "->")
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val info = connectivityManager.allNetworkInfo
            if (info != null) {
                for (i in info.indices) {
                    Log.d("atento4", "->")
                    if (info[i].state == NetworkInfo.State.CONNECTED) {
                        return true
                    }
                }
            }
        }
        return false
    }
    }
}