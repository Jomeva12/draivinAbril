package com.jomeva.driving.activities.providers


import com.jomeva.driving.activities.modelos.PeticionModerador
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class PeticionModeradorProvider {
    private lateinit var mCollection: CollectionReference

    constructor() {
        mCollection = FirebaseFirestore.getInstance().collection("PeticionModerador")
    }

    public fun save(peticionModerador: PeticionModerador): Task<Void> {

        peticionModerador.idPeticion = peticionModerador.uid
        return mCollection.document(peticionModerador.idPeticion).set(peticionModerador)
    }


    public fun getPeticionesByCity(ciudad: String): Query {
        return mCollection.whereEqualTo("ciudad", ciudad)
    }
    public fun getPeticionesId(id: String): Task<DocumentSnapshot> {
        return mCollection.document(id).get()
    }
    public fun getPeticionesByCityIfExist(id: String): Query {
        return mCollection.whereEqualTo("uid", id).limit(1)
    }

    public fun deletePost(idSolicitud: String) : Task<Void> {
        return mCollection.document(idSolicitud).delete()
    }
    public fun deletePostByUser(uid: String) : Task<Void> {
        return mCollection.document(uid).delete()
    }

}