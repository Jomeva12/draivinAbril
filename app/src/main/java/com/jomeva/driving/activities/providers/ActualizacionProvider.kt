package com.jomeva.driving.activities.providers

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.jomeva.driving.activities.modelos.ActualizacionDisponible
import com.jomeva.driving.activities.modelos.Like

class ActualizacionProvider {
    private var mCollection: CollectionReference = FirebaseFirestore.getInstance().collection("Actualizacion")

    constructor()

    public fun crearVersion(version: ActualizacionDisponible) : Task<Void> {
        val documento: DocumentReference =mCollection.document()
        val id=documento.id
        version.idVersion=id
        return documento.set(version)
    }
    public fun getVersion(): Query {
        return mCollection
    }


}