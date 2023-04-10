package com.jomeva.driving.activities.adapters

import com.jomeva.driving.R
import android.content.Context
import android.content.Intent
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.jomeva.driving.activities.modelos.Comentarios
import com.jomeva.driving.activities.providers.UserProviders
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.jomeva.driving.activities.activities.ChatActivity
import com.jomeva.driving.activities.activities.PerfilActivityPost
import com.jomeva.driving.activities.modelos.Post
import com.jomeva.driving.activities.providers.ComentarioProvider
import com.jomeva.driving.activities.providers.PostProvider
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.cardview_comentarios.view.*


class ComentarioAdapter(options: FirestoreRecyclerOptions<Comentarios?>?, var context: Context?) :
    FirestoreRecyclerAdapter<Comentarios, ComentarioAdapter.ViewHolder>(options!!) {

    private lateinit var mUserProvider: UserProviders
private lateinit var mComentarioProvider: ComentarioProvider

    override fun onBindViewHolder(holder: ViewHolder, position: Int, comentario: Comentarios) {
        val document: DocumentSnapshot = snapshots.getSnapshot(position)
        val comentarioId = document.id
        getUserComentario(comentario.uid,holder)
        holder.itemView.descripcionComentario.setText(comentario.comentario)

        holder.itemView.imagenComentario.setOnClickListener{
            val uid=comentario.uid
            if (!uid.isEmpty()) {
                val intent = Intent(context!!, PerfilActivityPost::class.java)
                //intent.putExtra("idPost", comentario.idPost)
                intent.putExtra("uid", uid)
                context?.startActivity(intent)
            }
        }
        holder.itemView.linearComentario.setOnLongClickListener {
            val popup = PopupMenu(context, it)
            funcionMenu(popup, comentario, comentario.uid)
            return@setOnLongClickListener false
        }

    }
    private fun funcionMenu(popup: PopupMenu, comentario: Comentarios, user: String) {
        val inflater: MenuInflater = popup.getMenuInflater()
      val mAuth=FirebaseAuth.getInstance()
        if (user.toString() == mAuth.currentUser!!.uid) {
            inflater.inflate(R.menu.menu_my_perfil, popup.menu)

        }
//para cambiar el color de los item
        for (i in 0 until popup.menu.size()) {
            val item = popup.menu.getItem(i)
            val spanString = SpannableString(popup.menu.getItem(i).title.toString())
            spanString.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(context!!, R.color.white)),
                0,
                spanString.length,
                0
            ) //fix the color to white
            item.title = spanString
        }
        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.eliminarPost -> {
                    //mensaje de eliminar
                    mComentarioProvider=ComentarioProvider()
                    mComentarioProvider.deleteComentario(comentario.idComentario)


                    return@setOnMenuItemClickListener true
                }

                else -> {

                    return@setOnMenuItemClickListener true
                }
            }
        }
        popup.show()

    }
    private fun getUserComentario(uid: String,holder: ViewHolder) {
        mUserProvider=UserProviders()
        mUserProvider.getUsuario(uid).addOnSuccessListener {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.cardview_comentarios, parent, false)
        return ViewHolder(view)
    }

    public class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        init {
            var nombreComentario: TextView = view.findViewById(R.id.nombreComentario)
            var descripcionComentario: TextView = view.findViewById(R.id.descripcionComentario)
            var imagenComentario: ImageView = view.findViewById(R.id.imagenComentario)

        }

    }


}