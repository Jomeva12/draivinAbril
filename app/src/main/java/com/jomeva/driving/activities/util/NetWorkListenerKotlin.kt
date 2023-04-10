package com.jomeva.driving.activities.util


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.jomeva.driving.R
import android.view.LayoutInflater
import android.view.View
import kotlinx.android.synthetic.main.check_internet_dialog.view.*


class NetWorkListenerKotlin : BroadcastReceiver() {

    //https://www.youtube.com/watch?v=BoiBuRwZ6RE
    override fun onReceive(context: Context?, intent: Intent?) {


        if (!CommonKotlin.isConnectToInternet(context!!)) {// si no hay
            Log.d("atento", "${CommonKotlin.isConnectToInternet(context!!)}")
            val alertDialog: androidx.appcompat.app.AlertDialog
            val builder: androidx.appcompat.app.AlertDialog.Builder =
                androidx.appcompat.app.AlertDialog.Builder(context!!)
            val v: View = LayoutInflater.from(context).inflate(R.layout.check_internet_dialog, null)
            builder.setView(v)
            builder.setCancelable(false)
            alertDialog = builder.create()

            alertDialog.show()
            v.comprobarInternet.setOnClickListener {
                alertDialog.dismiss()
                onReceive(context, intent)
            }
        }
        }
}