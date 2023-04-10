package com.jomeva.driving.activities.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jomeva.driving.R
import com.jomeva.driving.activities.modelos.Moderadores
import com.jomeva.driving.activities.providers.UserProviders
import com.jomeva.driving.activities.util.RelativeTime
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.cardview_moderador.view.*

class ModeradorAdapter (
    options: FirestoreRecyclerOptions<Moderadores?>?,
    var context: Context?
) :
    FirestoreRecyclerAdapter<Moderadores, ModeradorAdapter.ViewHolder>(options!!)
{
    private lateinit var mUserProvider: UserProviders
    private lateinit var mAuth: FirebaseAuth
    override fun onBindViewHolder(holder: ViewHolder, position: Int, mModeradores: Moderadores) {
        mAuth = FirebaseAuth.getInstance()

        getUserPost(mModeradores.uid, holder)
    }





    private fun getUserPost(idUser: String, holder: ViewHolder) {
        mUserProvider = UserProviders()
        mUserProvider.getUsuario(idUser).addOnSuccessListener {
            if (it.exists()) {
                if (it.contains("nombre")) {
                    holder.itemView.nombreModerador.setText("${it.get("nombre")}")
                }
                if (it.contains("photo")) {
                    var imagenPerfil: String = it.get("photo").toString()
                    if (it.get("photo").toString() == "") {
                        Picasso.get().load(R.drawable.ic_person_azul)
                            .error(R.drawable.ic_person_azul).into(holder.itemView.imagenModerador)
                    } else {

                        Picasso.get().load(imagenPerfil).error(R.drawable.ic_person_azul)
                            .into(holder.itemView.imagenModerador)
                    }
                }
                if (it.contains("tiempoEnLaApp")) {
                    var tiempo = it.get("tiempoEnLaApp").toString().toLong()
                   holder.itemView.tiempoModerador.setText(RelativeTime.RelativeTime.timeAUsoApp(tiempo))
                }
                if (it.contains("ciudad")) {
                    var ciudad = it.get("ciudad").toString()
                    holder.itemView.ciudad.text=ciudad
                }
            }
        }
    }
    public class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        init {
            Log.d("ciudadcidasdasdaudad34", "sasaassaa")



        }

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {


        val view: View =
            LayoutInflater.from(parent.context).
            inflate(R.layout.cardview_moderador, parent, false)
        return ViewHolder(view)

    }



}