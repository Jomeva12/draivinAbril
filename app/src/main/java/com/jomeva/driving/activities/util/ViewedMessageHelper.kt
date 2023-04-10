package com.jomeva.driving.activities.util

import android.app.ActivityManager
import android.content.Context
import android.util.Log
import com.jomeva.driving.activities.providers.UserProviders
import com.google.firebase.auth.FirebaseAuth


class ViewedMessageHelper {
    private lateinit var mUserProviders: UserProviders
    private lateinit var mAuth: FirebaseAuth
    fun updateOnline(status: Boolean, context: Context) {
        mAuth = FirebaseAuth.getInstance()
        Log.d("entra a clase","")
        mUserProviders = UserProviders()
        if (mAuth.currentUser!!.uid != null) {
            Log.d("entra a mAuth","ss")
            if (isApplicationSentToBackground(context)) {
                mUserProviders.updateOnline(mAuth.currentUser!!.uid, status)
                Log.d("isApplicationSentTo->","$status")
            } else if (status) {
                mUserProviders.updateOnline(mAuth.currentUser!!.uid, status)

                Log.d("else->","$status")
            }
        }else{
            mUserProviders.updateOnline(mAuth.currentUser!!.uid, false)
        }
    }

    private fun isApplicationSentToBackground(context: Context): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val tasks = activityManager.getRunningTasks(1)
        if (!tasks.isEmpty()) {
            val topActivity = tasks[0].topActivity
            if (topActivity!!.packageName != context.packageName) {
                return true
            }
        }
        return false
    }

}