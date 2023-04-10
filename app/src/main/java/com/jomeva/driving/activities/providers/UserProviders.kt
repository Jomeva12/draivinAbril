package com.jomeva.driving.activities.providers


import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.jomeva.driving.activities.modelos.Usuarios
import java.util.*


class UserProviders {
    private lateinit var mCollectionReference: CollectionReference
lateinit var mAuth:FirebaseAuth

    constructor() {
        mCollectionReference = FirebaseFirestore.getInstance().collection("usuarios")
mAuth=FirebaseAuth.getInstance()
    }

    public fun upDatePerfil(user: Usuarios): com.google.android.gms.tasks.Task<Void> {
        var mapa= hashMapOf<String,String>()
       // mapa["ciudad"]= user.ciudad
        if(user.photoPerfil!=""){mapa["photo"] = user.photoPerfil
       }
        if(user.nombre!=""){mapa["nombre"] = user.nombre
        }
        if(user.tiempoEnLaApp!="0"){mapa["tiempoEnLaApp"] = user.tiempoEnLaApp
        }
        if(user.ciudad!=""){mapa["ciudad"] = user.ciudad
        }
        return mCollectionReference.document(user.uid).update(mapa as Map<String, Any>)
    }
    public fun upDateCiudad(user: Usuarios): com.google.android.gms.tasks.Task<Void> {
       // var mapa = mutableMapOf("ciudad" to user.ciudad)
        var mapa :Map<String,String> = mutableMapOf("ciudad" to user.ciudad)

       return mCollectionReference.document(user.uid).update(mapa as Map<String, Any>)
    }
    fun updateOnline(idUser: String, status: Boolean):com.google.android.gms.tasks.Task<Void> {
        val map: MutableMap<String, Any> = HashMap()
        map["online"] = status
        map["lastConnect"] = Date().time
        return mCollectionReference.document(idUser).update(map)
    }

        public fun getUsuario(id: String): com.google.android.gms.tasks.Task<DocumentSnapshot> {
        return mCollectionReference.document(id).get()
    }
    public fun getUserByCiudad(ciudad:String): Query {
        return mCollectionReference.whereEqualTo("ciudad", ciudad)
    }

    public fun getNmonedasbyUser(idUser: String): com.google.android.gms.tasks.Task<DocumentSnapshot> {
        return mCollectionReference.document(idUser).get()
    }
    public fun upDateMonedas(idUser: String,Nmonedas:String): com.google.android.gms.tasks.Task<Void> {
        var mapa = mutableMapOf("monedas" to Nmonedas)
        return mCollectionReference.document(idUser).update(mapa as Map<String, Any>)
    }
    public fun getUserRealtime(id: String): com.google.firebase.firestore.DocumentReference {
        return mCollectionReference.document(id)
    }
    fun register(email: String?, password: String?): Task<AuthResult?>? {
        return mAuth.createUserWithEmailAndPassword(email, password)
    }
    fun login(email: String?, password: String?): Task<AuthResult?>? {
        return mAuth.signInWithEmailAndPassword(email, password)
    }


}