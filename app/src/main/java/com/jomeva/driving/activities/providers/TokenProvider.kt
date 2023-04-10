package com.jomeva.driving.activities.providers

//import bolts.Task
import android.util.Log
import androidx.annotation.Keep
import com.jomeva.driving.activities.modelos.Token
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

import com.google.firebase.messaging.FirebaseMessaging
@Keep
class TokenProvider {

    public lateinit var mCollection: CollectionReference
    public lateinit var token:Token
    constructor() {
        mCollection = FirebaseFirestore.getInstance().collection("tokens")

    }
/*  public fun crearToken(idUser: String) {
        if (idUser == null) return

        FirebaseMessaging.getInstance().token
            .addOnSuccessListener {
                val token = Token(it)
                mCollection.document(idUser).set(token)
            }
    }*/

   /* public fun crearToken(idUser: String) {
        if (idUser == null) return

        FirebaseMessaging.getInstance().token
            .addOnCompleteListener {
                val token = Token(it.result)
                mCollection.document(idUser).set(token)
            }
    }*/

    fun crearToken(idUser: String?) {
        Log.d("idDeUsuario","$idUser")
        if (idUser == null) return
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                 token = Token(task.result)
                Log.d("idDeUsuario2","$token")
                mCollection.document(idUser).set(token)
            }
    }


    public fun getToken(idUser: String): com.google.android.gms.tasks.Task<DocumentSnapshot> {
        Log.d("idDeUsuario3","${mCollection.document(idUser).get()}")
        return mCollection.document(idUser).get()
    }

}