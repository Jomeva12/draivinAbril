package com.jomeva.driving.activities.providers

import com.jomeva.driving.activities.modelos.Raiting
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class RaitingProvider {
    private lateinit var mCollection: CollectionReference

    constructor(){
        mCollection= FirebaseFirestore.getInstance().collection("raiting")
    }

    public fun nuevoRaiting(raiting: Raiting) : Task<Void> {
        val documento: DocumentReference =mCollection.document()
        val id=documento.id
        raiting.idRaiting=id.toString()
        return documento.set(raiting)
    }
    public fun getRaitingByReporteAndUser(idReporte:String,idUser:String): Query {
        return mCollection.whereEqualTo("idReporte",idReporte).whereEqualTo("uid",idUser)
    }
    public fun getRaitingByUser(idUser:String): Query {
        return mCollection.whereEqualTo("uid",idUser)
    }

    public fun deleteLike(idLike: String) : Task<Void> {
        return mCollection.document(idLike).delete()
    }
    public fun getRaitingtbyReporte(id: String): Query {
        return mCollection.whereEqualTo("idReporte", id)
    }
}