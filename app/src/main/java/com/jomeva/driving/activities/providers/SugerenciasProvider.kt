package com.jomeva.driving.activities.providers

import com.jomeva.driving.activities.modelos.SugerenciasUsuarios
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class SugerenciasProvider {
    private var mCollection: CollectionReference
    constructor() {
        mCollection = FirebaseFirestore.getInstance().collection("Sugerencias")
    }
    public fun save(sugerenciasUsuarios: SugerenciasUsuarios): Task<Void> {
        val document = mCollection.document()
        sugerenciasUsuarios.idSugerencia= document.id
        return mCollection.document(sugerenciasUsuarios.idSugerencia).set(sugerenciasUsuarios)
    }
    public fun getModeradorbyCity(ciudad: String): Query {
        return mCollection.whereEqualTo("ciudad", ciudad)
    }
    public fun deleteSugerencia(idSugerencia: String) : Task<Void> {
        return mCollection.document(idSugerencia).delete()
    }
}