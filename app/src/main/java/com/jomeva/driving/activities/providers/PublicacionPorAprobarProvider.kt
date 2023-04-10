package com.jomeva.driving.activities.providers

import com.jomeva.driving.activities.modelos.PublicacionPorAprobar
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class PublicacionPorAprobarProvider {
    private lateinit var mCollection: CollectionReference
    constructor() {
        mCollection = FirebaseFirestore.getInstance().collection("publicacionesPendientes")
    }

    public fun save(publicacionPorAprobar: PublicacionPorAprobar): Task<Void> {
        val document = mCollection.document()
        publicacionPorAprobar.idPost = document.id
        return mCollection.document( publicacionPorAprobar.idPost).set(publicacionPorAprobar)
    }

    public fun getAll(): Query {
        return mCollection.orderBy("fecha", Query.Direction.DESCENDING)
    }


    public fun getPublicacionesByCity(ciudad: String): Query {
        return mCollection.whereEqualTo("adminArea", ciudad)
    }


    public fun deletePost(idpost: String) : Task<Void> {
        return mCollection.document(idpost).delete()
    }
}