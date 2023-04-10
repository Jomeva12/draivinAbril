package com.jomeva.driving.activities.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.*
import android.widget.PopupMenu
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.jomeva.driving.R
import com.jomeva.driving.activities.activities.FragmentFour
import com.jomeva.driving.activities.activities.MapaPropina
import com.jomeva.driving.activities.activities.PerfilActivityPost
import com.jomeva.driving.activities.modelos.Propinas
import com.jomeva.driving.activities.providers.ComentarioProvider
import com.jomeva.driving.activities.providers.PropinasProvider
import com.jomeva.driving.activities.providers.UserProviders
import com.jomeva.driving.activities.util.RelativeTime


import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.jomeva.driving.activities.modelos.Comentarios
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.cardview_propinas.view.*


class PropinasAdapter(options: FirestoreRecyclerOptions<Propinas?>?, var context: Context?) :
    FirestoreRecyclerAdapter<Propinas, PropinasAdapter.ViewHolder>(options!!) {


    private lateinit var mAuth: FirebaseAuth
    private var estado: String = "abierto"
    private lateinit var mUserProvider: UserProviders
    private lateinit var mComentarioProvider: ComentarioProvider
    private lateinit var mPropinasProvider: PropinasProvider
    private lateinit var mFragmentFour: FragmentFour


    override fun onBindViewHolder(holder: ViewHolder, position: Int, propinas: Propinas) {
        val document: DocumentSnapshot = snapshots.getSnapshot(position)
        val idPropinas = document.id
        val uid = propinas.uid

        mAuth = FirebaseAuth.getInstance()

       val fechatimeStamp:Long?=propinas.fecha!!.toLong()

holder.itemView.fecha.text= fechatimeStamp?.let { RelativeTime.RelativeTime.getTimeAgo(it,context!!) }

        holder.itemView.txtDescripcion.text = propinas.descripcion

        holder.itemView.frameSinLeer.setOnClickListener {
            mFragmentFour = FragmentFour()
           if (propinas.Lat != "" && propinas.Lon != "") {
                val intent = Intent(context, MapaPropina::class.java)

                intent.putExtra("Lat", "${propinas.Lat}")
                intent.putExtra("Lon", "${propinas.Lon}")
                intent.putExtra("idPropina", propinas.idPropinas)
                intent.putExtra("descripcion", propinas.descripcion)
                intent.putExtra("uid", uid)
                intent.putExtra("estado", propinas.idAporte)

                this.context?.startActivity(intent)
            } else {
                mFragmentFour.consultarPropinas(View(context), propinas)
           }
        }

        holder.itemView.txtDescripcion.setOnClickListener {
            mFragmentFour = FragmentFour()
           if (propinas.Lat != "" && propinas.Lon != "") {
                Log.d("mapa2->", "${propinas!!.Lat}")
                val intent = Intent(context, MapaPropina::class.java)
                intent.putExtra("Lat", "${propinas.Lat}")
                intent.putExtra("Lon", "${propinas.Lon}")
                intent.putExtra("idPropina", propinas.idPropinas)
                intent.putExtra("descripcion", propinas.descripcion)
                intent.putExtra("uid", uid)
                intent.putExtra("estado", propinas.idAporte)
                this.context?.startActivity(intent)
            } else {
                mFragmentFour.consultarPropinas(View(context), propinas)
           }
        }

        holder.itemView.nombrePropina.setOnClickListener {
            mFragmentFour = FragmentFour()

           if (propinas.Lat != "" && propinas.Lon != "") {
                val intent = Intent(context, MapaPropina::class.java)
                intent.putExtra("Lat", "${propinas.Lat}")
                intent.putExtra("Lon", "${propinas.Lon}")
                intent.putExtra("idPropina", propinas.idPropinas)
                intent.putExtra("descripcion", propinas.descripcion)
                intent.putExtra("uid", uid)
                intent.putExtra("estado", propinas.idAporte)
                this.context?.startActivity(intent)
            } else {
                mFragmentFour.consultarPropinas(View(context), propinas)
            }
        }
        holder.itemView.cardPropina.setOnClickListener {
            mFragmentFour = FragmentFour()
            if (propinas.Lat != "" && propinas.Lon != "") {
                val intent = Intent(context, MapaPropina::class.java)
                intent.putExtra("Lat", "${propinas.Lat}")
                intent.putExtra("Lon", "${propinas.Lon}")
                intent.putExtra("idPropina", propinas.idPropinas)
                                intent.putExtra("descripcion", propinas.descripcion)
                intent.putExtra("uid", uid)
                intent.putExtra("estado", propinas.idAporte)
                this.context?.startActivity(intent)
            } else {
                mFragmentFour.consultarPropinas(View(context), propinas)
            }

        }
holder.itemView.frameSinLeer.setOnLongClickListener{
    val popup = PopupMenu(context, it)
    funcionMenu(popup, propinas, uid)

    return@setOnLongClickListener false
}

        var fecha: Long? = propinas.fecha
        var mRelativeTime: String? =
            fecha?.let { it1 -> RelativeTime.RelativeTime.getTimeAgo(it1, context) }
        // holder.itemView.fecha.text = "${mRelativeTime}"
        getDatosusuario(uid, holder)
        getCerrado(idPropinas, holder)
      /*  getNcomentarios(idPropinas, holder)
        getIsMap(propinas, uid)*/

    }

    private fun funcionMenu(popup: PopupMenu, propinas: Propinas, user: String) {
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
                    mPropinasProvider=PropinasProvider()
                    mPropinasProvider.deletePropina(propinas.idPropinas)


                    return@setOnMenuItemClickListener true
                }

                else -> {

                    return@setOnMenuItemClickListener true
                }
            }
        }
        popup.show()

    }
  /*  private fun getIsMap(propinas: Propinas, uid: String) {

    }*/

/*    private fun getNcomentarios(idPropinas: String, holder: ViewHolder) {
        mComentarioProvider = ComentarioProvider()
        mComentarioProvider.getComentariobyPost(idPropinas).addSnapshotListener { it, error ->
            val N: Int? = it?.size()
            // holder.itemView.Naporte.text = "$N"

        }
    }*/


    @SuppressLint("UseCompatLoadingForColorStateLists")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun getCerrado(idPropinas: String, holder: ViewHolder) {
        mPropinasProvider = PropinasProvider()
        mPropinasProvider.getPropinabyId(idPropinas).addOnSuccessListener {
            if (it.exists()) {
                if (it.contains("idAporte")) {
                    val aporteId = it.get("idAporte").toString()
                    val aporte=it.getString("monedas")
                    if (aporteId != "") {
                        holder.itemView.estadoPropina.setImageResource(R.drawable.check_ok)
                        holder.itemView.textViewNmonedas.text = ""

                    } else {
                        holder.itemView.estadoPropina.setImageResource(R.drawable.corazon_like_off)
                        if (aporte!!.toInt() > 99) {
                            holder.itemView.textViewNmonedas.text = "99+"
                        } else {
                            holder.itemView.textViewNmonedas.text = aporte
                        }

                    }
                }
            }
        }
    }

    private fun getDatosusuario(uid: String, holder: ViewHolder) {
        mUserProvider = UserProviders()
        mUserProvider.getUsuario(uid).addOnSuccessListener {
            if (it.exists()) {
                if (it.contains("nombre")) {
                    holder.itemView.nombrePropina.text = it.get("nombre").toString()
                }
                if (it.contains("photo")) {
                    var imagenPerfil: String = it.get("photo").toString()
                    if (it.get("photo").toString() == "") {
                        Picasso.get().load(R.drawable.ic_person_azul)
                            .error(R.drawable.ic_person_azul).into(holder.itemView.imagenPerfil)
                    } else {

                        Picasso.get().load(imagenPerfil).error(R.drawable.ic_person_azul)
                            .into(holder.itemView.imagenPerfil)
                    }
                }
                holder.itemView.imagenPerfil.setOnClickListener {
                    val intent = Intent(context, PerfilActivityPost::class.java)
                    intent.putExtra("uid", uid)
                    context?.startActivity(intent)

                }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.cardview_propinas, parent, false)
        return ViewHolder(view)

    }

    public class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        init {

            /* var nombre: TextView = view.findViewById(R.id.nombrePropina)
             var fecha: TextView = view.findViewById(R.id.fecha)
             var descripcion: TextView = view.findViewById(R.id.txtDescripcion)
             var Nmonedas: TextView = view.findViewById(R.id.textViewNmonedas)
             var imageperfil: ImageView = view.findViewById(R.id.imagenPerfil)*/


        }

    }


}