package com.jomeva.driving.activities.providers

import com.jomeva.driving.activities.modelos.Post
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.FirebaseFirestore

class PostProvider {
    private lateinit var mCollection: CollectionReference
    constructor() {
        mCollection = FirebaseFirestore.getInstance().collection("posts")
    }

    public fun save(post: Post): Task<Void> {
        val document = mCollection.document()
        post.idPost = document.id
        return mCollection.document( post.idPost).set(post)
    }

    public fun getAll(): Query {
        return mCollection.orderBy("fecha", Query.Direction.DESCENDING)
    }

    public fun getPostByCiudad(adminArea:String): Query {
        return mCollection.whereEqualTo("adminArea", adminArea).orderBy("fecha", Query.Direction.DESCENDING)
    }

    public fun upDatePostnReporte(idPost: String,nReporteNegativo:String): Task<Void> {
        var mapa = mutableMapOf("nreporteNegativo" to nReporteNegativo)
        return mCollection.document(idPost).update(mapa as Map<String, Any>)
    }

    public fun getPostbyIdAndUser(idPost:String,idUser:String):Query{
        return mCollection.whereEqualTo("idPost",idPost).whereEqualTo("uid",idUser).limit(1)
    }

    public fun getAllPost(idPost: String): Task<DocumentSnapshot> {
        return mCollection.document(idPost).get()
    }

    public fun getPostbyUser(id: String): Query {
        return mCollection.whereEqualTo("uid", id)
    }
    public fun getUserByPost(idPost: String): Task<DocumentSnapshot> {
        return mCollection.document(idPost).get()
    }
    public fun getPostbyUserPerfil(id: String): Query {
        return mCollection.whereEqualTo("uid", id).orderBy("fecha", Query.Direction.DESCENDING)
    }
    public fun getPostbyId(id: String): Task<DocumentSnapshot> {
        return mCollection.document(id).get()
    }
    public fun deletePost(idpost: String) : Task<Void> {
        return mCollection.document(idpost).delete()
    }


}