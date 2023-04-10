package com.jomeva.driving.activities.providers

import com.jomeva.driving.activities.modelos.Comentarios
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ComentarioProvider {
    private lateinit var mCollection: CollectionReference

    constructor(){
        mCollection=FirebaseFirestore.getInstance().collection("comentarios")
    }
    public fun crearComentario(coment: Comentarios):Task<Void>{
        val document = mCollection.document()
        coment.idComentario = document.id
         return mCollection.document("${coment.idComentario.toString()}").set(coment)
    }
    public fun getComentariobyPost(idPost: String): Query {
        return mCollection.whereEqualTo("idPost", idPost)
            .orderBy("fecha", Query.Direction.ASCENDING)
    }

    public fun getGanadorByComentario(idPost: String): Query {
        return mCollection.whereEqualTo("idPost", idPost)
    }


    public fun getcomentariobyId(idAporte:String):Query{
        return mCollection.whereEqualTo("idComentario",idAporte)
    }
    public fun getComentario(id: String): Query {
        return mCollection.whereEqualTo("idPost", id)
    }
    public fun deleteComentario(idComentario: String) : Task<Void> {
        return mCollection.document(idComentario).delete()
    }
}
