package com.jomeva.driving.activities.modelos


import android.content.Context
import android.content.Intent
import com.jomeva.driving.activities.activities.HomeActivity
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import com.jomeva.driving.activities.util.PreferenciasGenerales

class LogicaFirestoreUsuarios {
    private var mFirestore: CollectionReference =
        FirebaseFirestore.getInstance().collection("usuarios")

    constructor()

    public fun crearUsuario(context: Context) {
        val user = Firebase.auth.currentUser
        val uid = user!!.uid
        mFirestore.document(uid).get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
            } else {
                registrarEnBd(user,context)
            }
        }
    }

    public fun getUserByUid(uid: String): Query {
        return mFirestore.whereEqualTo("uid", uid)
    }

    //65011112345
    public fun crearUsuario(uid: String, phone: String, nombre: String, photo: String) {

        mFirestore.document(uid).get().addOnSuccessListener { it ->
            if (it.exists()) {

            } else {

            }

        }

    }

    //650-111-1234
    public fun registrarEnBdPhone(
        context: Context,
        uid: String,
        phone: String,
        nombre: String,
        photo: String
    ) {
        var mapa = mutableMapOf(
            "nombre" to nombre
        )
        mapa.put("autenticacion", phone)
        mapa.put("uid", uid)
        mapa.put("ciudad", "")
        mapa.put("photo", photo)
        mapa.put("monedas", "100")
        mapa.put("tiempoEnLaApp", "0")

        mFirestore.document(uid).set(mapa).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val intent=Intent(context,HomeActivity::class.java)
                context.startActivity(intent)
            } else {

            }

        }
    }

    private fun  registrarEnBd(user: FirebaseUser, context: Context) {
        val photo = user.photoUrl
        val name = user.displayName
        val autenticacion = user.email
        val uid = user.uid
        var mapa = mutableMapOf("nombre" to name)
        mapa.put("autenticacion", autenticacion)
        mapa.put("uid", uid)
        mapa.put("photo", photo.toString())
        mapa.put("monedas", "100")
        mapa.put("ciudad", PreferenciasGenerales(context!!).getpreferenceCiudadUsuario())
        mapa.put("tiempoEnLaApp", "0")
        println(mapa)
        mFirestore.document(uid).set(mapa).addOnCompleteListener { task ->
            if (task.isSuccessful) {

            } else {

            }

        }
    }
}
