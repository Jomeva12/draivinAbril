package com.jomeva.driving.activities.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.jomeva.driving.R
import com.jomeva.driving.activities.modelos.Like
import com.jomeva.driving.activities.modelos.Moderadores
import com.jomeva.driving.activities.modelos.PeticionModerador
import com.jomeva.driving.activities.providers.LikeProvider
import com.jomeva.driving.activities.providers.ModeradoresProvider
import com.jomeva.driving.activities.providers.PeticionModeradorProvider
import com.jomeva.driving.activities.providers.UserProviders
import com.jomeva.driving.activities.util.RelativeTime
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.cardview_moderadores_pendientes.view.*
import java.util.*

class ModeradorSolicitudAdapter(
    options: FirestoreRecyclerOptions<PeticionModerador?>?,
    var context: Context?
) :
    FirestoreRecyclerAdapter<PeticionModerador, ModeradorSolicitudAdapter.ViewHolder>(options!!) {
    private lateinit var mUserProvider: UserProviders
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mPeticionModeradorProvider: PeticionModeradorProvider
    private lateinit var mLikeProvider: LikeProvider
    private lateinit var mModeradoresProvider: ModeradoresProvider
    private lateinit var mModeradores: Moderadores
    var banderaClickAprobar = true
    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
        peticionModerador: PeticionModerador
    ) {
        mAuth = FirebaseAuth.getInstance()
        holder.itemView.linearAprobar.setOnClickListener {
            if (banderaClickAprobar) {
                banderaClickAprobar = !banderaClickAprobar
                val mlike = Like()
                mlike.uid = mAuth.uid.toString()
                mlike.idPost = peticionModerador.idPeticion
                mlike.fecha = Date().time
                mlike.tipo = "AprobarModerador"
                like(mlike, holder)

            }
        }

        holder.itemView.linearLRechazar.setOnClickListener {
            if (banderaClickAprobar) {
                banderaClickAprobar = !banderaClickAprobar
                val mlike = Like()
                mlike.uid = mAuth.uid.toString()
                mlike.idPost = peticionModerador.idPeticion
                mlike.fecha = Date().time
                mlike.tipo = "Rechazar"
                like(mlike, holder)

            }
        }
        getUserPeticiones(peticionModerador, holder)
        getNAprobadosByPeticion(peticionModerador, holder)
    }

    private fun like(like: Like, holder: ViewHolder) {
        mAuth = FirebaseAuth.getInstance()
        mLikeProvider = LikeProvider()
        mLikeProvider.getLikeByPostAndUserAndILoveOrHate(
            like.idPost,
            mAuth.uid.toString(),
            like.tipo
        ).get()
            .addOnSuccessListener {
                val numberDocuments: Int = it.size()
                if (numberDocuments > 0) {
                    val idLike: String = it.documents[0].id
                    mLikeProvider.deleteLike(idLike)
                    banderaClickAprobar = true
                } else {
                    mLikeProvider.nuevoLike(like)
                    banderaClickAprobar = true


                }

            }

    }

    private fun getNAprobadosByPeticion(peticion: PeticionModerador, holder: ViewHolder) {
        mLikeProvider = LikeProvider()
        mLikeProvider.getLikeByPostAndILoveOrHate(peticion.idPeticion, "AprobarModerador")
            .addSnapshotListener { it, error ->
                val N: Int? = it?.size()
                holder.itemView.Naprobados.setText("${N}")
                if (N!! > 2) {
                    //borrar solicitud y guardar moderador
                    mModeradoresProvider=ModeradoresProvider()
                    mModeradores=Moderadores()

                    mModeradores.uid = peticion.uid
                    mModeradores.ciudad = peticion.ciudad
                    mModeradoresProvider.save(mModeradores).addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(context, "Asignado como moderador", Toast.LENGTH_SHORT).show()
                            mPeticionModeradorProvider.deletePost(peticion.idPeticion)
                        }
                    }

                }

            }
        mLikeProvider.getLikeByPostAndILoveOrHate(peticion.idPeticion, "Rechazar")
            .addSnapshotListener { it, error ->
                val N: Int? = it?.size()
                if (N!! > 2) {
    //usuario rechazado, eliminado desde el like y la solicitud
                    mPeticionModeradorProvider.deletePost(peticion.idPeticion)

                }
                holder.itemView.NRechazado.setText("${N}")

            }

    }

    private fun getUserPeticiones(peticion: PeticionModerador, holder: ViewHolder) {
        mUserProvider = UserProviders()

        mUserProvider.getUsuario(peticion.uid).addOnSuccessListener {
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
                    holder.itemView.tiempoModerador.setText(
                        RelativeTime.RelativeTime.timeAUsoApp(
                            tiempo
                        )
                    )
                }

                if (it.contains("ciudad")) {
                    var ciudad = it.get("ciudad").toString()
                    holder.itemView.ciudad.text = ciudad
                    mPeticionModeradorProvider = PeticionModeradorProvider()
                    mPeticionModeradorProvider.getPeticionesId(peticion.idPeticion.toString())
                        .addOnSuccessListener {
                            if (it.exists()) {
                                if (it.contains("mensaje")) {
                                    var mensaje = it.get("mensaje").toString()
                                    holder.itemView.mensajePeticion.setText(mensaje)
                                }
                                if (it.contains("fecha")) {
                                    var fecha = it.get("fecha").toString().toLong()
                                    holder.itemView.fecha.setText(
                                        RelativeTime.RelativeTime.getTimeAgo(
                                            fecha,
                                            context
                                        )
                                    )
                                }
                            }
                        }
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
            LayoutInflater.from(parent.context)
                .inflate(R.layout.cardview_moderadores_pendientes, parent, false)
        return ViewHolder(view)

    }


}