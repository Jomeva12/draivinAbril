package com.jomeva.driving.activities.providers

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider


class AuthProviders {

    private lateinit var mAuth: FirebaseAuth

    constructor() {
        mAuth = FirebaseAuth.getInstance()
    }


    fun signInPhone(verificationId: String, code: String): Task<AuthResult?>? {
        val credential = PhoneAuthProvider.getCredential(verificationId, code)

        return mAuth.signInWithCredential(credential)
    }
    fun getId():String{
        return if( mAuth.currentUser!!.uid!=null){
            mAuth.currentUser!!.uid
        }else{
            null.toString()
        }

    }
}



