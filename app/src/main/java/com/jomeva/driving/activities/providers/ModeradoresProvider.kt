package com.jomeva.driving.activities.providers

import com.jomeva.driving.activities.modelos.Moderadores
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.jomeva.driving.activities.modelos.Usuarios

class ModeradoresProvider {
    private lateinit var mCollectionReference: CollectionReference


    constructor() {
        mCollectionReference = FirebaseFirestore.getInstance().collection("Moderadores")

    }
    public fun save(moderadores: Moderadores): Task<Void> {
        //val document = mCollectionReference.document()
        //moderadores.uid = document.id
        return mCollectionReference.document( moderadores.uid).set(moderadores)
    }
    public fun getModeradorbyCity(ciudad: String): Query {
        return mCollectionReference.whereEqualTo("ciudad", ciudad)
    }
    public fun getModeradorbyId(uid: String): Query {
        return mCollectionReference.whereEqualTo("uid", uid).limit(1)
    }
    public fun upDateCiudad(user: Usuarios): com.google.android.gms.tasks.Task<Void> {
        // var mapa = mutableMapOf("ciudad" to user.ciudad)
        var mapa :Map<String,String> = mutableMapOf("ciudad" to user.ciudad)

        return mCollectionReference.document(user.uid).update(mapa as Map<String, Any>)
    }

}