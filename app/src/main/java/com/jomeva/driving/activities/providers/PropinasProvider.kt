package com.jomeva.driving.activities.providers


import com.jomeva.driving.activities.modelos.Propinas
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class PropinasProvider {
    private lateinit var mCollection: CollectionReference

    constructor() {
        mCollection = FirebaseFirestore.getInstance().collection("propinas")
    }
    public fun getAll(): Query {
        return mCollection.orderBy("fecha", Query.Direction.DESCENDING)
    }
    public fun getPropinaByCiudad(ciudad:String): Query {
        return mCollection.whereEqualTo("ciudad", ciudad).orderBy("fecha", Query.Direction.DESCENDING)
    }
    public fun save(propinas: Propinas): Task<Void> {
        val document = mCollection.document()
        propinas.idPropinas = document.id
        return mCollection.document("${propinas.idPropinas}").set(propinas)
    }
    public fun getPropinabyId(id: String): Task<DocumentSnapshot> {
        return mCollection.document(id).get()
    }
    /* public fun getGanadorByPropinaAndUser(idPropina:String,idUser:String):Query{
         return mCollection.whereEqualTo("idPropinas",idPropina).whereEqualTo("idAporte",idUser)
     }*/
    public fun upDatePropinas(idPropina: String,idAporte:String): com.google.android.gms.tasks.Task<Void> {
        var mapa = mutableMapOf("idAporte" to idAporte)
        return mCollection.document(idPropina).update(mapa as Map<String, Any>)
    }

    public fun getNmonedasByUserAndIdPropinas(idPropina: String, uid: String): Query {
        return mCollection.whereEqualTo("idPropinas", idPropina).whereEqualTo("uid", uid)
    }
    public fun deletePropina(idPropina: String) : Task<Void> {
        return mCollection.document(idPropina).delete()
    }


}