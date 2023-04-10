package com.jomeva.driving.activities.adapters

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.jomeva.driving.R
import com.jomeva.driving.activities.activities.PerfilActivityPost
import com.jomeva.driving.activities.modelos.Comentarios
import com.jomeva.driving.activities.providers.ComentarioProvider
import com.jomeva.driving.activities.providers.PropinasProvider
import com.jomeva.driving.activities.providers.UserProviders
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.cardview_comentario_propina.view.*


import java.lang.Integer.parseInt


class PropinaAporteAdapter(
    options: FirestoreRecyclerOptions<Comentarios?>?,
    var context: Context?
) :
    FirestoreRecyclerAdapter<Comentarios, PropinaAporteAdapter.ViewHolder>(options!!) {
    private lateinit var mListenerRegistration: ListenerRegistration
    private lateinit var mUserProvider: UserProviders
    private lateinit var mComentarioProvider: ComentarioProvider
    private lateinit var mPropinasProvider: PropinasProvider
    private lateinit var mAuth: FirebaseAuth
    var contador = 0

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: ViewHolder, position: Int, comentario: Comentarios) {
        val document: DocumentSnapshot = snapshots.getSnapshot(position)
        val comentarioId = document.id

        mAuth = FirebaseAuth.getInstance()
        getUserComentario(comentario.uid, holder)
        holder.itemView.descripcionComentario.setText(comentario.comentario)
        /* val cardColor = ContextCompat.getColor(context!!, R.color.colorGrisTransparente)
         holder.itemView.cardGanador.setCardBackgroundColor(cardColor)*/

        val uidPersonaQueComenta = comentario.uid.toString()
        val uidPostPropina = comentario.uidPost.toString()
        val idPropinas = comentario.idPost.toString()

        if (uidPostPropina == mAuth.uid) {
            holder.itemView.btnEntregarPropinas.visibility = View.VISIBLE
        } else {
            holder.itemView.btnEntregarPropinas.visibility = View.GONE
        }

        holder.itemView.btnEntregarPropinas.setOnClickListener {
            val alertDialog = AlertDialog.Builder(context)
                .setTitle("Entregar propina")
                .setMessage("Desea entregar propina")
                .setPositiveButton("Si") { dialog, which ->

                    dialog.dismiss()
                    entregarPropina(
                        comentarioId,
                        uidPersonaQueComenta,
                        uidPostPropina,
                        idPropinas,
                        holder
                    )
                }

                .setNegativeButton("No") { dialog, which ->
                    dialog.dismiss()
                }
                .show()


        }
        holder.itemView.imagenComentario.setOnClickListener {
            if (!uidPersonaQueComenta.isEmpty()) {
                val intent = Intent(context, PerfilActivityPost::class.java)
                intent.putExtra("uid", uidPersonaQueComenta)
                this.context?.startActivity(intent)
            }

        }
        holder.itemView.nombreComentario.setOnClickListener {

            if (!uidPersonaQueComenta.isEmpty()) {
                val intent = Intent(context, PerfilActivityPost::class.java)
                intent.putExtra("uid", uidPersonaQueComenta)
                this.context?.startActivity(intent)
            }

        }
        getGanador(comentarioId, idPropinas, holder)
    }

    //con este anuncio aportas a la mision de driving
    //voy por acá

    private fun getGanador(comentarioId: String, idPropinas: String, holder: ViewHolder) {
        mPropinasProvider = PropinasProvider()
        mComentarioProvider = ComentarioProvider()
        mPropinasProvider.getPropinabyId(idPropinas).addOnSuccessListener {
            if (it.exists()) {
                if (it.contains("idAporte")) {
                    val idAporte = it.get("idAporte").toString()
                    if (idAporte != "") {
                        holder.itemView.btnEntregarPropinas.visibility = View.GONE

                        mComentarioProvider.getcomentariobyId(idAporte).get()
                            .addOnSuccessListener { it2 ->
                                val size = it2.size()
                                Log.d("ganadorrr", "$size $idAporte-> $comentarioId")
                                if (size > 0) {
                                    if (idAporte == comentarioId) {
                                        Log.d("ganadorrr", "son iguales")
                                        val color = context!!.resources.getColorStateList(
                                            R.color.colorAcent,
                                            null
                                        )
                                        holder.itemView.colorTarjeta1.backgroundTintList = color
                                        holder.itemView.colorTarjeta2.backgroundTintList = color
                                        holder.itemView.nombreComentario.backgroundTintList = color
                                        holder.itemView.descripcionComentario.backgroundTintList = color

                                    }

                                }
                            }
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun entregarPropina(
        comentarioId: String,
        uidPersonaQueComenta: String,
        uidPostPropina: String,
        idPropinas: String,
        holder: ViewHolder
    ) {
        mPropinasProvider = PropinasProvider()
//acá se está duplicando, se ejecuta dos veces el getMonedasby... quitemos addSnap solucionado con el listener Registration
        mListenerRegistration =
            mPropinasProvider.getNmonedasByUserAndIdPropinas(idPropinas, uidPostPropina)
                .addSnapshotListener { it2, error ->
                    val size: Int = it2!!.size()
                    if (size in 1..1) {
                        mListenerRegistration.remove()
                       // MostrarSuccesEntregaPropina()
                        val monedasAentregar = it2.documents[0].getString("monedas").toString()
                        actualizarOferentedeMoneda(
                            comentarioId,
                            idPropinas,
                            uidPersonaQueComenta,
                            uidPostPropina,
                            monedasAentregar,
                            holder
                        )
                    } else {

                    }

                }


    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun actualizarOferentedeMoneda(
        comentarioId: String,
        idPropinas: String,
        uidPersonaQueComenta: String,
        uidPostPropina: String,
        monedasAentregar: String,
        holder: ViewHolder
    ) {
        mUserProvider = UserProviders()
        mUserProvider.getNmonedasbyUser(uidPostPropina).addOnSuccessListener { it ->
            if (it.exists()) {

                if (it.contains("monedas")) {
                    val monedero = it.get("monedas").toString()

                    val restaDeMonedas = parseInt(monedero) - parseInt(monedasAentregar)
                    mUserProvider.upDateMonedas(uidPostPropina, restaDeMonedas.toString())
                        .addOnCompleteListener { it3 ->
                            if (it3.isSuccessful) {
                                actualizarGanador(
                                    comentarioId,
                                    idPropinas,
                                    uidPersonaQueComenta,
                                    monedasAentregar,
                                    holder
                                )
                            } else {

                            }
                        }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun actualizarGanador(
        comentarioId: String,
        idPropinas: String,
        uidPersonaQueComenta: String,
        monedasAentregar: String,
        holder: ViewHolder
    ) {
        mPropinasProvider = PropinasProvider()
//val thread= Thread{
        mUserProvider.getNmonedasbyUser(uidPersonaQueComenta).addOnSuccessListener { it2 ->
            if (it2.exists()) {
                if (it2.contains("monedas")) {
                    val monedero = it2.get("monedas").toString()
                    val sumaDeMonedas = parseInt(monedero) + parseInt(monedasAentregar)
                    mUserProvider.upDateMonedas(uidPersonaQueComenta, sumaDeMonedas.toString())
                        .addOnCompleteListener { it3 ->
                            if (it3.isSuccessful) {
//poner en propinas cerrrado

                                mPropinasProvider.upDatePropinas(idPropinas, comentarioId)
                                    .addOnCompleteListener {
                                        if (it.isSuccessful) {
                                            getGanador(comentarioId, idPropinas, holder)

                                            Log.d("entrando-for", "entrando..")
                                            /* holder.itemView.cardGanador.backgroundTintList =
                                                 context!!.resources.getColorStateList(R.color.amarillo)*/
                                            /*  holder.itemView.nombreComentario.backgroundTintList =
                                                  context!!.resources.getColorStateList(R.color.amarillo)
                                              holder.itemView.descripcionComentario.backgroundTintList =
                                                  context!!.resources.getColorStateList(R.color.amarillo)*/

                                            holder.itemView.btnEntregarPropinas.visibility =
                                                View.GONE
                                            //holder.itemView.aporteRelevante.visibility=View.VISIBLE

                                        }
                                    }


                            } else {

                            }
                        }
                }
            }
        }
// }
//  thread.start()

    }

    private fun getUserComentario(idUser: String, holder: ViewHolder) {
        mUserProvider = UserProviders()
        mUserProvider.getUsuario(idUser).addOnSuccessListener {
            if (it.exists()) {
                if (it.contains("nombre")) {
                    holder.itemView.nombreComentario.setText("${it.get("nombre")}")
                }
                if (it.contains("photo")) {
                    var imagenPerfil: String = it.get("photo").toString()
                    if (it.get("photo").toString() == "") {
                        Picasso.get().load(R.drawable.ic_person_azul)
                            .error(R.drawable.ic_person_azul).into(holder.itemView.imagenComentario)
                    } else {

                        Picasso.get().load(imagenPerfil).error(R.drawable.ic_person_azul)
                            .into(holder.itemView.imagenComentario)
                    }

                }

            }
        }
    }

    fun MostrarSuccesEntregaPropina() {
        val cuadro = R.layout.cuadro_mensaje_check
        val alertDialog: androidx.appcompat.app.AlertDialog
        val builder: androidx.appcompat.app.AlertDialog.Builder =
            androidx.appcompat.app.AlertDialog.Builder(context!!)
        val v: View = LayoutInflater.from(context).inflate(cuadro, null)
        builder.setView(v)

        builder.setCancelable(true)
        alertDialog = builder.create()
        alertDialog.show()
        Handler().postDelayed({
            alertDialog.dismiss()
            (context as Activity).finish()
        }, 2500)
/* v.cerrarcuadroLikes.setOnClickListener {
alertDialog.dismiss()
(context as Activity).finish()
}*/
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.cardview_comentario_propina, parent, false)
        return ViewHolder(view)
    }

    public class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        init {
/*
var nombreComentario: TextView = view.findViewById(R.id.nombreComentario)
var descripcionComentario: TextView = view.findViewById(R.id.descripcionComentario)
var imagenComentario: ImageView = view.findViewById(R.id.imagenComentario)
*/
        }

    }


}