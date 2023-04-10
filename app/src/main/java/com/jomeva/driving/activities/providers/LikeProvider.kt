package com.jomeva.driving.activities.providers

import com.jomeva.driving.activities.modelos.Like
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*

class LikeProvider {
    private var mCollection: CollectionReference = FirebaseFirestore.getInstance().collection("likes")

    constructor()

    public fun nuevoLike(like: Like) : Task<Void> {
        val documento:DocumentReference=mCollection.document()
        val id=documento.id
        like.idLike=id.toString()
        return documento.set(like)
    }
    public fun getLikeByPostAndUserAndILoveOrHate(idPost:String,idUser:String,tipo:String):Query{
        return mCollection.whereEqualTo("tipo",tipo).whereEqualTo("idPost",idPost).whereEqualTo("uid",idUser).limit(1)
    }
    public fun getLikeByPostAndILoveOrHate(idPost:String,tipo:String):Query{
        return mCollection.whereEqualTo("tipo",tipo).whereEqualTo("idPost",idPost)
    }


    public fun getLikeByUser(idUser:String):Query{
        return mCollection.whereEqualTo("uid",idUser)
    }


    public fun deleteLike(idPost: String) : Task<Void> {
        return mCollection.document(idPost).delete()
    }
    public fun getLikestbyPost(id: String): Query {
        return mCollection.whereEqualTo("idPost", id)
    }

}