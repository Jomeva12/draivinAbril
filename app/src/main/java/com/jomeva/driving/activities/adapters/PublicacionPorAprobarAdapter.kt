package com.jomeva.driving.activities.adapters

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jomeva.driving.R
import com.jomeva.driving.activities.modelos.Post
import com.jomeva.driving.activities.modelos.PublicacionPorAprobar
import com.jomeva.driving.activities.providers.PostProvider
import com.jomeva.driving.activities.providers.PublicacionPorAprobarProvider
import com.jomeva.driving.activities.providers.UserProviders
import com.jomeva.driving.activities.util.RelativeTime
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.cardview_publicaion_pendiente.view.*


class PublicacionPorAprobarAdapter(
    options: FirestoreRecyclerOptions<PublicacionPorAprobar?>?,
    var context: Context?
) :
    FirestoreRecyclerAdapter<PublicacionPorAprobar, PublicacionPorAprobarAdapter.ViewHolder>(options!!)
{
    private lateinit var mUserProvider: UserProviders
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mPostProvider: PostProvider
    private lateinit var mPublicacionPorAprobarProvider: PublicacionPorAprobarProvider
    private  var bandera=false
    override fun onBindViewHolder(holder: ViewHolder, position: Int, publicacionPorAprobar: PublicacionPorAprobar) {
        mAuth = FirebaseAuth.getInstance()

        mPublicacionPorAprobarProvider=PublicacionPorAprobarProvider()
        holder.itemView.textCiudad.text = publicacionPorAprobar.adminArea
        holder.itemView.aprobarPublicacion.setOnClickListener {
           if (!bandera){
               bandera=true
               val post=Post()
               mPostProvider= PostProvider()
               post.imagen = publicacionPorAprobar.imagen
               post.type = publicacionPorAprobar.type
               post.nota = publicacionPorAprobar.nota
               post.descripcion = publicacionPorAprobar.descripcion
               post.uid = publicacionPorAprobar.uid
               post.fecha = publicacionPorAprobar.fecha
               post.adminArea = publicacionPorAprobar.adminArea
               post.lat = publicacionPorAprobar.lat
               post.lng = publicacionPorAprobar.lng
               post.nameFile=publicacionPorAprobar.nameFile
               post.nreporteNegativo="0"
               mPostProvider.save(post).addOnCompleteListener {
                   if (it.isSuccessful) {
                       mPublicacionPorAprobarProvider.deletePost(publicacionPorAprobar.idPost)
                       //mostrar cuadro de eliminada
                       mensaje(R.layout.cuadro_mensaje_check)
                       bandera=false
                   }
               }
           }

        }
        holder.itemView.rechazarPublicacion.setOnClickListener {
            mPublicacionPorAprobarProvider.deletePost(publicacionPorAprobar.idPost)
            mensaje(R.layout.cuadro_eliminar_reporte)
        }
        /*mediaPlayer = MediaPlayer()
            handler = Handler(Looper.myLooper()!!)*/
        // val document: DocumentSnapshot = snapshots.getSnapshot(position)
        if (publicacionPorAprobar.type == "texto") {
            textoPost(publicacionPorAprobar, holder)
            holder.itemView.cardReproducir.visibility = View.GONE
        } else if (publicacionPorAprobar.type == "audio") {
            holder.itemView.cardReproducir.visibility = View.VISIBLE
            holder.itemView.descripcion.visibility = View.GONE
            holder.itemView.time.text = publicacionPorAprobar.nota
            holder.itemView.soloDescripcion.visibility = View.GONE
        } else if (publicacionPorAprobar.type == "imagen") {
            imagenPost(publicacionPorAprobar, holder)
            holder.itemView.cardReproducir.visibility = View.GONE
        }
        var fecha: Long? = publicacionPorAprobar.fecha
        var mRelativeTime: String? =
            fecha?.let { it1 -> RelativeTime.RelativeTime.getTimeAgo(it1, context) }
        holder.itemView.txtRelaiveTime.text = "$mRelativeTime"
        getUserPost(publicacionPorAprobar.uid, holder)
        if (publicacionPorAprobar.imagen != null) {
            if (publicacionPorAprobar.imagen.isNotEmpty()) {
                Picasso.get().load(publicacionPorAprobar.imagen).into(holder.itemView.imagepostCard)
            } else {
                holder.itemView.imagepostCard.visibility = View.GONE

            }
        }

    }


    private fun mensaje(cuadro: Int) {
// val cuadro=R.layout.cuadro_mensaje_check
        val alertDialog: androidx.appcompat.app.AlertDialog
        val builder: androidx.appcompat.app.AlertDialog.Builder =
            androidx.appcompat.app.AlertDialog.Builder(context!!)
        val v: View = LayoutInflater.from(context).inflate(cuadro, null)
        builder.setView(v)

        builder.setCancelable(true)
        alertDialog = builder.create()
        alertDialog.show()
        Handler(Looper.myLooper()!!).postDelayed({ alertDialog.dismiss() }, 2500)

    }


    private fun imagenPost(publicacionPorAprobar: PublicacionPorAprobar, holder: ViewHolder) {

        holder.itemView.imagepostCard.visibility = View.VISIBLE
        holder.itemView.soloDescripcion.visibility = View.GONE
        holder.itemView.descripcion.visibility = View.VISIBLE
        Picasso.get().load(publicacionPorAprobar.imagen).into(holder.itemView.imagepostCard)
        if (publicacionPorAprobar.descripcion.length > 150) {
            holder.itemView.vermas.text == "Ver más...".toString()
            holder.itemView.vermas.visibility = View.VISIBLE
            holder.itemView.descripcion.text = publicacionPorAprobar.descripcion.substring(0, 150) + "..."
        } else {
            holder.itemView.vermas.visibility = View.GONE
            holder.itemView.descripcion.text = publicacionPorAprobar.descripcion
        }
    }

    fun textoPost(publicacionPorAprobar: PublicacionPorAprobar, holder: ViewHolder) {
        if (publicacionPorAprobar.descripcion.length > 150) {
            holder.itemView.vermasSinFoto.text == "Ver más..."
            holder.itemView.vermasSinFoto.visibility = View.VISIBLE
            holder.itemView.soloDescripcion.text =
                publicacionPorAprobar.descripcion.substring(0, 150) + "..."
        } else {
            holder.itemView.vermasSinFoto.visibility = View.GONE
            holder.itemView.soloDescripcion.text = publicacionPorAprobar.descripcion
        }
        // holder.itemView.framepost.background =
        // context!!.resources.getDrawable(R.drawable.back_color_full_black, null)
        holder.itemView.soloDescripcion.visibility = View.VISIBLE
        holder.itemView.descripcion.visibility = View.GONE
    }
    private fun getUserPost(idUser: String, holder: ViewHolder) {
        mUserProvider = UserProviders()
        mUserProvider.getUsuario(idUser).addOnSuccessListener {
            if (it.exists()) {
                if (it.contains("nombre")) {
                    holder.itemView.nombrePost.setText("${it.get("nombre")}")
                }
                if (it.contains("photo")) {
                    var imagenPerfil: String = it.get("photo").toString()
                    if (it.get("photo").toString() == "") {
                        Picasso.get().load(R.drawable.ic_person_azul)
                            .error(R.drawable.ic_person_azul).into(holder.itemView.photoPerfilPost)
                    } else {

                        Picasso.get().load(imagenPerfil).error(R.drawable.ic_person_azul)
                            .into(holder.itemView.photoPerfilPost)
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

        Log.d("ciudadcidasdasdaudad35", "wwww")
        val view: View =
            LayoutInflater.from(parent.context).
            inflate(R.layout.cardview_publicaion_pendiente, parent, false)
        return ViewHolder(view)

    }



}