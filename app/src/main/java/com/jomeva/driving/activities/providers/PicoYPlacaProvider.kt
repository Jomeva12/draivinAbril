package com.jomeva.driving.activities.providers

import com.jomeva.driving.activities.modelos.PicoYPlaca
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class PicoYPlacaProvider {
    private lateinit var mCollection: CollectionReference

    constructor() {
        mCollection = FirebaseFirestore.getInstance().collection("PicoYplaca")
    }

    public fun save(picoYPlaca: PicoYPlaca): Task<Void> {
        val docu=mCollection.document()
        if (  picoYPlaca.idPicoYPlaca=="") picoYPlaca.idPicoYPlaca=docu.id
        return mCollection.document(picoYPlaca.idPicoYPlaca ).set(picoYPlaca)
    }


    public fun getPicoYplacaByCity(ciudad: String): Query {
        return mCollection.whereEqualTo("ciudad", ciudad).limit(1)
    }


}