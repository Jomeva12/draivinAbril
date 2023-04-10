package com.jomeva.driving.activities.providers


import com.jomeva.driving.activities.modelos.ReporteEnMapa
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ReporteMapaProvider {

    private lateinit var mCollection: CollectionReference

    constructor() {
        mCollection = FirebaseFirestore.getInstance().collection("reporteMapa")

    }
    public fun save(reporteEnMapa: ReporteEnMapa): Task<Void> {
        val document = mCollection.document()
        reporteEnMapa.idReporte = document.id
        return mCollection.document( reporteEnMapa.idReporte).set(reporteEnMapa)
    }

    public fun getReporte(id: String): Task<DocumentSnapshot> {
        return mCollection.document(id).get()
    }
    public fun getReporteByCiudad(ciudad:String): Query {
        return mCollection.whereEqualTo("ciudad", ciudad).orderBy("fecha", Query.Direction.ASCENDING)
    }

    public fun deleteReporte(id: String): Task<Void> {
        return mCollection.document(id).delete()
    }
    public fun upDateReporte(mReporte: ReporteEnMapa): Task<Void> {
        var mapa = mutableMapOf("npersonasRating" to mReporte.nPersonasRating)
        mapa["raitingTotal"] = mReporte.raitingTotal
        return mCollection.document(mReporte.idReporte).update(mapa as Map<String, Any>)
    }


}