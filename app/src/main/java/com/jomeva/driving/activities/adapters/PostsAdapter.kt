package com.jomeva.driving.activities.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.jomeva.driving.R
import com.jomeva.driving.activities.modelos.Like
import com.jomeva.driving.activities.modelos.Post
import com.jomeva.driving.activities.modelos.PublicacionPorAprobar
import com.jomeva.driving.activities.providers.*
import com.jomeva.driving.activities.util.RelativeTime
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.jomeva.driving.activities.activities.*
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.cardview_post.view.*
import kotlinx.android.synthetic.main.cardview_post.view.cardReproducir
import kotlinx.android.synthetic.main.cardview_post.view.nombrePost
import kotlinx.android.synthetic.main.cardview_post.view.time
import kotlinx.android.synthetic.main.cuadro_comentarios_eventos_transparentes.*
import kotlinx.android.synthetic.main.cuadro_post.view.*
import kotlinx.android.synthetic.main.cuadro_preguntar.view.*
import org.jsoup.Jsoup
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


class PostsAdapter(
    options: FirestoreRecyclerOptions<Post?>?,
    var context: Context?
) :
    FirestoreRecyclerAdapter<Post, PostsAdapter.ViewHolder>(options!!) {
    val client = OkHttpClient()
    var isUrl = false
    var textUrl = ""
    private lateinit var mUserProvider: UserProviders
    private lateinit var mPostProvider: PostProvider
    private lateinit var mModeradorProvider: ModeradoresProvider
    private lateinit var mLikeProvider: LikeProvider
    private lateinit var mComentarioProvider: ComentarioProvider
    private lateinit var mAuth: FirebaseAuth
    private lateinit var monedasput: String
    private lateinit var mFragmentTwo: FragmentTwo
    private lateinit var mFragmentOne: FragmentOne
    private var selectedPosition = 0
    private var mediaPlayer: MediaPlayer? = null
    private var myLat: Double = 0.0
    private var myLon: Double = 0.0
    var distancia = 0
    var banderita = "stop"
    var idDelBenditoPost = ""
    var banderaClickLike = true
    var banderaClickYaNoEsta = true
    var contadorTarjetas = 0
    var card = 0
    var isUrlPost = false
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var mImageProvider: ImageProvider

    private lateinit var mPublicacionPorAprobarProvider: PublicacionPorAprobarProvider

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: ViewHolder, position: Int, post: Post) {
        mImageProvider = ImageProvider()
        mFragmentOne = FragmentOne()
        val tam = snapshots!!.size


        Log.d("positionnnnn2b", "$contadorTarjetas -> $tam")
        mAuth = FirebaseAuth.getInstance()
        holder.itemView.textViewCiudad.text = post.adminArea
        /*mediaPlayer = MediaPlayer()
            handler = Handler(Looper.myLooper()!!)*/
        // val document: DocumentSnapshot = snapshots.getSnapshot(position)
        if (post.type == "texto") {
            textoPost(post, holder)
            holder.itemView.cardReproducir.visibility = View.GONE
        } else if (post.type == "audio") {
            holder.itemView.cardReproducir.visibility = View.VISIBLE
            holder.itemView.descripcion.visibility = View.GONE
            holder.itemView.time.text = post.nota
            holder.itemView.soloDescripcion.visibility = View.GONE
        } else if (post.type == "imagen") {
            imagenPost(post, holder)
            holder.itemView.cardReproducir.visibility = View.GONE
        }
        var fecha: Long? = post.fecha
        var mRelativeTime: String? =
            fecha?.let { it1 -> RelativeTime.RelativeTime.getTimeAgo(it1, context) }
        holder.itemView.txtRelaiveTime.text = "$mRelativeTime"
        getUserPost(post.uid, holder)
        if (post.imagen != null) {
            if (post.imagen.isNotEmpty()) {
                Picasso.get().load(post.imagen).into(holder.itemView.imagepostCard)
            } else {
                holder.itemView.imagepostCard.visibility = View.GONE

            }
        }
        mModeradorProvider = ModeradoresProvider()
        mModeradorProvider.getModeradorbyId(post.uid).get().addOnSuccessListener {
            if (it.size() > 0) {
                holder.itemView.soyModerador.visibility = View.VISIBLE
            } else {
                holder.itemView.soyModerador.visibility = View.GONE
            }
        }

        clickar(holder, post)
        getNmeGustabypost(post.idPost, holder)
        getNmeComentariosbypost(post.idPost, holder)
        SiExistelike(post.idPost, mAuth.uid.toString(), holder)


    }

    private fun mensajePersonalizadoLogin() {

        val alertDialog: AlertDialog
        val builder: AlertDialog.Builder =
            AlertDialog.Builder(context!!)
        val v: View =
            LayoutInflater.from(context).inflate(R.layout.cuadro_registrarse_primera_vez, null)
        builder.setView(v)

        builder.setCancelable(true)
        alertDialog = builder.create()
        alertDialog.show()

        v.afirmativo.setOnClickListener {

            val intent = Intent(context, AuthActivity::class.java)
            context!!.startActivity(intent)

        }
        v.negativo.setOnClickListener {
            alertDialog.dismiss()
        }
    }

    private fun clickar(holder: ViewHolder, post: Post) {
        holder.itemView.play.setOnClickListener {
            mFragmentOne.cambiandoCiudadListener(View(context), post, isImagen = false)
        }
        holder.itemView.linearLike.setOnClickListener {
            val users = mAuth.currentUser
            if (users != null) {
                if (banderaClickLike) {
                    banderaClickLike = !banderaClickLike
                    val mlike = Like()
                    mlike.uid = mAuth.uid.toString()
                    mlike.idPost = post.idPost
                    mlike.fecha = Date().time
                    mlike.tipo = "MeGusta"
                    like(mlike, holder)

                }
            } else {
                mensajePersonalizadoLogin()
            }

        }
        holder.itemView.linearYanoEsta.setOnClickListener {
            val users = mAuth.currentUser
            if (users != null) {
                if (banderaClickYaNoEsta) {
                    banderaClickYaNoEsta = !banderaClickYaNoEsta
                    val mlike = Like()
                    mlike.uid = mAuth.uid.toString()
                    mlike.idPost = post.idPost
                    mlike.fecha = Date().time
                    mlike.tipo = "YaNoEsta"
                    like(mlike, holder)
                }
            } else {
                mensajePersonalizadoLogin()
            }

        }
        holder.itemView.linearConfig.setOnClickListener {
            val users = mAuth.currentUser
            if (users != null) {
                abrirMenu(it, post)
            } else {
                mensajePersonalizadoLogin()
            }

        }
        holder.itemView.vermasSinFoto.setOnClickListener {
            if (holder.itemView.vermasSinFoto.text == "Ver menos...") {
                holder.itemView.soloDescripcion.text = post.descripcion.substring(0, 150) + "..."
                holder.itemView.vermasSinFoto.text = "Ver más..."
            } else if (holder.itemView.vermasSinFoto.text == "Ver más...") {
                holder.itemView.soloDescripcion.text = post.descripcion
                holder.itemView.vermasSinFoto.text = "Ver menos..."
            }
        }
        holder.itemView.vermas.setOnClickListener {
            // Toast.makeText(context,"Entra clic",Toast.LENGTH_LONG).show()
            if (holder.itemView.vermas.text == "Ver menos...") {
                holder.itemView.descripcion.text = post.descripcion.substring(0, 150) + "..."
                holder.itemView.vermas.text = "Ver más..."
            } else if (holder.itemView.vermas.text == "Ver más...") {
                holder.itemView.descripcion.text = post.descripcion
                holder.itemView.vermas.text = "Ver menos..."
            }

        }
        holder.itemView.nombrePost.setOnClickListener {
            val uid = post.uid.toString()
            if (!uid.isEmpty()) {
                val intent = Intent(context, PerfilActivityPost::class.java)
                intent.putExtra("idPost", post.idPost)
                intent.putExtra("uid", uid)
                this.context?.startActivity(intent)
            }

        }

        holder.itemView.linearComentario.setOnClickListener {

            var isImagen = false
            if (post.imagen != "") {
                isImagen = true
                mFragmentOne.cambiandoCiudadListener(View(context), post, isImagen)
            } else {
                isImagen = false
                mFragmentOne.cambiandoCiudadListener(View(context), post, isImagen)
            }
            // }

        }

        holder.itemView.framepost.setOnClickListener {
            val text = post.descripcion
            val pattern = Patterns.WEB_URL
            val matcher = pattern.matcher(text)
            while (matcher.find()) {
                val url = matcher.group()
                isUrlPost = true
                textUrl = url
                //

            }
            Log.d("arrar", "$isUrl")
            if (isUrlPost) {
                isUrlPost = false
                //abrir en webView
                val intent = Intent(context!!, VistaWeb::class.java)
                intent.putExtra("url", post.descripcion)
                context!!.startActivity(intent)
            } else {
                var isImagen = false
                if (post.imagen != "") {
                    isImagen = true
                    mFragmentOne.cambiandoCiudadListener(View(context), post, isImagen)
                } else {
                    isImagen = false
                    mFragmentOne.cambiandoCiudadListener(View(context), post, isImagen)
                }
            }
        }
        holder.itemView.photoPerfilPost.setOnClickListener {
            val intent = Intent(context!!, PerfilActivityPost::class.java)
            intent.putExtra("uid", post.uid)
            context!!.startActivity(intent)
        }

    }


    private fun abrirMenu(it: View, post: Post) {
        if (getNombreActivity(context) == "HomeActivity" || getNombreActivity(context) == "PerfilActivityPost" || getNombreActivity(
                context
            ) == "PerfilActivity"
        ) {
            var user: String = ""

            val contextoView = it
            mUserProvider.getUsuario(post.uid).addOnSuccessListener {
                if (it.exists()) {
                    if (it.contains("uid")) {
                        user = it.get("uid").toString()
                        val popup = PopupMenu(context, contextoView)
                        if (user.toString() == mAuth.currentUser!!.uid) {

                            funcionMenu(popup, post, user)

                            user = it.get("uid").toString()
                            Log.d("tarea post ->", " ${user} - ${mAuth.currentUser!!.uid}")
                        } else {
                            funcionMenu(popup, post, user)


                        }


                    }
                }

            }


        }
    }

    fun getSelectedPosition(): Int {
        return selectedPosition
    }

    fun setSelectedPosition(selectedPosition: Int) {
        this.selectedPosition = selectedPosition
    }
    /*  private fun reproducirElBenditoAudio(holder: PostsAdapter.ViewHolder, post: Post) {

          if (idDelBenditoPost == "" || idDelBenditoPost == post.idPost) {
              if ("" == idDelBenditoPost) {
                  idDelBenditoPost = post.idPost
                  var mediap = MediaPlayer()
                  mediap?.setDataSource(post.imagen)
                  mediap?.prepare()
                  mediaPlayer = mediap
                  metodoReproducir(holder, post, banderita, mediaPlayer!!)
              } else {
                  metodoReproducir(holder, post, banderita, mediaPlayer!!)
              }
          } else if (post.idPost != idDelBenditoPost) {

              holder.itemView.seekReproduccion.progress = 0
              holder.itemView.seekReproduccion.max = 0
              mediaPlayer = null
              var mediap2 = MediaPlayer()
              Log.d("banderitaff", "->")
              Toast.makeText(context, "cccc", Toast.LENGTH_LONG).show()
              mediap2?.reset()
              mediap2?.setDataSource(post.imagen)
              mediap2?.prepare()
              banderita = "stop"
              mediaPlayer = mediap2
              metodoReproducir(holder, post, banderita, mediaPlayer!!)
          }
      }*/

    /*fun metodoReproducir(holder: ViewHolder, post: Post, banderita: String, mediap: MediaPlayer) {
        if (banderita == "stop") {
            this.banderita = "enReproduccion"
            mediap?.start()


            holder.itemView.play.background = context!!.getDrawable(R.drawable.btn_pause)
            incializarSeekBar(holder, post, mediap)

        } else if (banderita == "enReproduccion") {
            holder.itemView.play.background = context!!.getDrawable(R.drawable.btn_play)
            this.banderita = "stop"
            mediap?.pause()
            incializarSeekBar(holder, post, mediap)
            Log.d("banderita2", "->${this.banderita}")

        }
        holder.itemView.seekReproduccion.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {

                if (fromUser) {
                    Log.d("xxxxxs3", "${holder.itemView.seekReproduccion.progress} ")
                    mediaPlayer?.seekTo(progress)
                }

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // you can probably leave this empty
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // you can probably leave this empty
            }
        })
    }*/


    /*fun incializarSeekBar(holder: ViewHolder, post: Post, mediap: MediaPlayer) {

if (::globalRunnable.isInitialized){
    handler.removeCallbacks(globalRunnable)
}


        holder.itemView.seekReproduccion.max = mediap!!.duration

        val value = object
            : Runnable {
            override fun run() {
                handler.postDelayed(this, 100)
                holder.itemView.seekReproduccion.progress = mediap!!.currentPosition
                if (post.idPost != idDelBenditoPost) {
                    idDelBenditoPost = post.idPost
                    handler.removeCallbacks(this)
                    mediaPlayer = null
                    mediaPlayer = mediap
                    incializarSeekBar(holder, post, mediaPlayer!!)
                    mediaPlayer?.seekTo(0)
                    return
                }

                if ((mediap!!.currentPosition / 100 == mediap!!.duration / 100)) {
                    holder.itemView.seekReproduccion.progress = 0
                    holder.itemView.play.background = context!!.getDrawable(R.drawable.btn_play)
                    banderita = "stop"
                    handler.removeCallbacks(this)
                }

            }
        }
        globalRunnable=value
        handler.postDelayed(value, 0)
    }*/

    /* fun incializarSeekBar2(holder: ViewHolder, post: Post, mediap: MediaPlayer) {
         holder.itemView.seekReproduccion.max = mediap!!.duration

         Log.d("banderita4.2.1", "---->${post.idPost}-> ${idDelBenditoPost}")
         var handler = Handler()
         handler.postDelayed(object : Runnable {
             override fun run() {
                 Log.d("banderita4.2.3", "---->${post.idPost}-> ${idDelBenditoPost}")
                 try {

                     holder.itemView.seekReproduccion.progress = mediap!!.currentPosition
                     Log.d("banderita4", "${holder.itemView.seekReproduccion.progress}")
                     handler.postDelayed(this, 1000)
                     val tiempo =
                         (mediap!!.duration / 1000) - (mediap!!.currentPosition / 1000).toLong()
                     val time = RelativeTime.RelativeTime.timeAudio(tiempo)
                     holder.itemView.time.text = time
                     if ((mediap!!.currentPosition / 1000 == mediap!!.duration / 1000) || post.idPost != idDelBenditoPost) {
                         mediaPlayer = mediap
                         holder.itemView.seekReproduccion.progress = 0
                         holder.itemView.seekReproduccion.max = 0
                         holder.itemView.play.background =
                             context!!.getDrawable(R.drawable.btn_play)
                         banderita = "stop"
                         handler.removeCallbacks(this)
                         if (mediaPlayer!!.isPlaying) {
                             mediaPlayer!!.stop();
                             mediaPlayer!!.release();
                             mediaPlayer = null;
                             idDelBenditoPost = post.idPost
                             Log.d("banderita4.2", "---->${post.idPost}-> ${idDelBenditoPost}")
                             incializarSeekBar(holder, post, mediap)
                         } else {
                             holder.itemView.play.background =
                                 context!!.getDrawable(R.drawable.btn_pause)
                         }

                     }
                     //}

                 } catch (e: Exception) {
                     holder.itemView.seekReproduccion.progress = 0
                 }
             }
         }, 0)

     }*/

    private fun imagenPost(post: Post, holder: ViewHolder) {
        holder.itemView.configImage.visibility = View.VISIBLE
        holder.itemView.imagepostCard.visibility = View.VISIBLE
        holder.itemView.soloDescripcion.visibility = View.GONE
        holder.itemView.descripcion.visibility = View.VISIBLE
        Picasso.get().load(post.imagen).into(holder.itemView.imagepostCard)
        if (post.descripcion.length > 150) {
            holder.itemView.vermas.text == "Ver más...".toString()
            holder.itemView.vermas.visibility = View.VISIBLE
            holder.itemView.descripcion.text = post.descripcion.substring(0, 150) + "..."
        } else {
            holder.itemView.vermas.visibility = View.GONE
            holder.itemView.descripcion.text = post.descripcion
        }
    }

    fun urlExists(url: String): Boolean {

        val request = Request.Builder()
            .url(url)
            .build()

        try {
            val response = client.newCall(request).execute()
            return response.isSuccessful
        } catch (e: IOException) {
            return false
        }
    }

    fun textoPost(post: Post, holder: ViewHolder) {

        val text = post.descripcion
// Expresión regular para buscar URLs
        val pattern = Patterns.WEB_URL
// Crear un objeto Matcher para buscar URLs en el texto
        val matcher = pattern.matcher(text)
// Recorrer el texto en busca de URLs
        while (matcher.find()) {
            // Obtener la URL encontrada
            val url = matcher.group()
            isUrl = true
            textUrl = url
            //

        }
        Log.d("textooo","${isUrl } ${post.idPost}")
        if (isUrl) {
            hacerAlgoConWebText(textUrl, holder)
            isUrl = false
        } else {
            if (post.descripcion.length > 150) {
                holder.itemView.vermasSinFoto.text == "Ver más..."
                holder.itemView.vermasSinFoto.visibility = View.VISIBLE
                holder.itemView.soloDescripcion.text =
                    post.descripcion.substring(0, 150) + "..."
            } else {
                holder.itemView.vermasSinFoto.visibility = View.GONE
                holder.itemView.soloDescripcion.text = post.descripcion
            }
            // holder.itemView.framepost.background =
            // context!!.resources.getDrawable(R.drawable.back_color_full_black, null)
            holder.itemView.soloDescripcion.visibility = View.VISIBLE
            holder.itemView.descripcion.visibility = View.GONE
            holder.itemView.configImage.visibility = View.VISIBLE
        }


    }

    private fun hacerAlgoConWebText(url: String, holder: ViewHolder) {

        val thread = Thread {
            if (urlExists(url)) {


                textUrl = url
                handler.post {
                    if (mediaPlayer != null) {
                        mediaPlayer!!.reset()
                    }


                }
                val doc = Jsoup.connect(url).get()
                // Extraer el título de la página
                Log.d("docc", "$doc")


                val title = doc.title()
                if (title != null) {
                    handler.post {
                        // Mostrar el título en un TextView, por ejemplo
                        //descripcion.text = title

                        val mitextoU = SpannableString(title)
                        mitextoU.setSpan(UnderlineSpan(), 0, mitextoU.length, 0)

                        holder.itemView.configImage.visibility = View.VISIBLE
                        holder.itemView.imagepostCard.visibility = View.VISIBLE
                        holder.itemView.soloDescripcion.visibility = View.GONE
                        holder.itemView.descripcion.visibility = View.VISIBLE
                        holder.itemView.descripcion.text = mitextoU
                    }
                }


                // Extraer la URL de la imagen de la página
                val imageUrl = doc.select("meta[property=og:image]").attr("content")

                // Descargar la imagen
                if (imageUrl != null && imageUrl.isNotEmpty()) {
                    val bitmap = Picasso.get().load(imageUrl).get()

                    // Guardar la imagen en un archivo temporal
                    val file = File.createTempFile("webview_capture", ".png", context!!.cacheDir)
                    val outputStream = FileOutputStream(file)
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    outputStream.flush()
                    outputStream.close()

                    // Mostrar la imagen en un ImageView, por ejemplo
                    handler.post {

                        holder.itemView.imagepostCard.setImageBitmap(bitmap)
                    }
                }

            } else {

            }

        }
        thread.start()
    }

    private fun funcionMenu(popup: PopupMenu, post: Post, user: String) {
        val inflater: MenuInflater = popup.getMenuInflater()
        mPostProvider = PostProvider()
        if (user.toString() == mAuth.currentUser!!.uid) {
            inflater.inflate(R.menu.menu_my_perfil, popup.menu)

        } else {

            inflater.inflate(R.menu.menupost, popup.menu)
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
                    Log.d("Eliminar ->", " Seleccionado eliminar")
                    mPostProvider.deletePost(post.idPost)
                    if (post.nameFile != "") {
                        mImageProvider.deleteFile(post.nameFile.toString())
                    }

                    return@setOnMenuItemClickListener true
                }
                R.id.itemPerfil -> {
                    getMonedas(user)
                    return@setOnMenuItemClickListener true
                }
                R.id.idChat -> {

                    val intent = Intent(context, ChatActivity::class.java)
                    intent.putExtra("user2", user)
                    intent.putExtra("user1", mAuth.currentUser!!.uid)
                    intent.putExtra("idChat", mAuth.currentUser!!.uid + user)
                    intent.putExtra("estadoPropina", "false")
                    this.context?.startActivity(intent)

                    return@setOnMenuItemClickListener true

                }
                R.id.reportar -> {
                    sumarNreporte(post, mAuth.uid.toString())
                    return@setOnMenuItemClickListener true
                }
                R.id.prueba -> {

                    return@setOnMenuItemClickListener true
                }
                else -> {

                    return@setOnMenuItemClickListener true
                }
            }
        }
        popup.show()

    }

    private fun sumarNreporte(post: Post, uid: String) {
        mLikeProvider.getLikeByPostAndUserAndILoveOrHate(post.idPost, uid, "Reporte").get()
            .addOnSuccessListener {
                val tam = it.size()
                if (tam > 0) {
                    Toast.makeText(
                        context,
                        "Ya tiene un reporte con su usuario",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    actualizarNreportes(post)
                }
            }


    }

    private fun actualizarNreportes(post: Post) {
        mPostProvider.getPostbyId(post.idPost).addOnSuccessListener {
            if (it.exists()) {
                if (it.contains("nreporteNegativo")) {
                    val n = it.getString("nreporteNegativo")!!.toInt()!!
                    val suma = n + 1
                    mPostProvider.upDatePostnReporte(post.idPost, suma.toString())
                        .addOnCompleteListener {

                            if (it.isSuccessful) {
                                if (suma >= 10) {
                                    //mandar a validar nuevamnete a los moderadors
                                    publicacionParaValidar(post)
                                } else {
                                    val mlike = Like()
                                    mlike.uid = mAuth.uid.toString()
                                    mlike.idPost = post.idPost
                                    mlike.fecha = Date().time
                                    mlike.tipo = "Reporte"
                                    mLikeProvider.nuevoLike(mlike)
                                }

                            }
                        }


                }
            }
        }
    }

    private fun publicacionParaValidar(post: Post) {
        var publicacionPorAprobar = PublicacionPorAprobar()
        publicacionPorAprobar.imagen = post.imagen
        publicacionPorAprobar.type = post.type
        publicacionPorAprobar.nota = post.nota
        publicacionPorAprobar.descripcion = post.descripcion
        publicacionPorAprobar.uid = post.uid
        publicacionPorAprobar.fecha = post.fecha
        publicacionPorAprobar.adminArea = post.adminArea
        publicacionPorAprobar.lat = post.lat
        publicacionPorAprobar.lng = post.lng

        mPublicacionPorAprobarProvider = PublicacionPorAprobarProvider()
        mPublicacionPorAprobarProvider.save(publicacionPorAprobar).addOnCompleteListener {
            if (it.isSuccessful) {
                mPostProvider.deletePost(post.idPost)
                mensaje(R.layout.cuadro_eliminar_reporte)
            } else {

            }
        }
    }

    private fun mensaje(cuadro: Int) {
        val alertDialog: androidx.appcompat.app.AlertDialog
        val builder: androidx.appcompat.app.AlertDialog.Builder =
            androidx.appcompat.app.AlertDialog.Builder(context!!)
        val v: View = LayoutInflater.from(context).inflate(cuadro, null)
        builder.setView(v)

        builder.setCancelable(true)
        alertDialog = builder.create()
        alertDialog.show()
        Handler(Looper.myLooper()!!).postDelayed({ alertDialog.dismiss() }, 4500)

    }

    private fun getNmeGustabypost(idPost: String, holder: ViewHolder) {
        mLikeProvider = LikeProvider()
        mLikeProvider.getLikeByPostAndILoveOrHate(idPost, "MeGusta")
            .addSnapshotListener { it, error ->
                val N: Int? = it?.size()
                holder.itemView.Nmegusta.setText("${N}")

            }
        mLikeProvider.getLikeByPostAndILoveOrHate(idPost, "YaNoEsta")
            .addSnapshotListener { it, error ->
                val N: Int? = it?.size()
                holder.itemView.NnYaNoEsta.setText("${N}")
                if (N!! >= 3) {
                    holder.itemView.imgRetirado.visibility = View.VISIBLE
                } else {
                    holder.itemView.imgRetirado.visibility = View.GONE
                }
            }
    }

    private fun getNmeComentariosbypost(idPost: String, holder: ViewHolder) {
        mComentarioProvider = ComentarioProvider()
        mComentarioProvider.getComentariobyPost(idPost).addSnapshotListener { it, error ->

            val N: Int? = it?.size()
            holder.itemView.Ncomentarios.setText("${N}")

        }
    }

    @SuppressLint("ResourceAsColor")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)

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
                    if (like.tipo == "MeGusta") {
                        mLikeProvider.deleteLike(idLike)
                        actualizarMonedas(numberDocuments, like)
                        holder.itemView.imgLike.setImageResource(R.drawable.corazon_like_off)
                        holder.itemView.reaccionLike.visibility = View.GONE
                        banderaClickLike = true
                    }
                    if (like.tipo == "YaNoEsta") {
                        mLikeProvider.deleteLike(idLike)
                        //actualizarMonedas(numberDocuments, like.idPost)
                        holder.itemView.imgYaNoEsta.setImageResource(R.drawable.retirar)
                        //holder.itemView.reaccionLike.visibility = View.GONE
                        banderaClickYaNoEsta = true
                    }

                } else {
                    if (like.tipo == "MeGusta") {
                        mLikeProvider.nuevoLike(like)
                        holder.itemView.imgLike.setImageResource(R.drawable.corazon_like)
                        holder.itemView.reaccionLike.visibility = View.VISIBLE
                        holder.itemView.reaccionLike.playAnimation()
                        actualizarMonedas(numberDocuments, like)
                        banderaClickLike = true
                    }
                    if (like.tipo == "YaNoEsta") {
                        mLikeProvider.nuevoLike(like)
                        holder.itemView.imgYaNoEsta.setImageResource(R.drawable.retirar)
                        // holder.itemView.reaccionLike.visibility = View.VISIBLE
                        // holder.itemView.reaccionLike.playAnimation()
                        //actualizarMonedas(numberDocuments, like.idPost)
                        banderaClickYaNoEsta = true
                    }


                }

            }

    }


    private fun getMonedas(uid: String) {
        val mUserProviders = UserProviders()
        monedasput = ""
        mUserProviders.getNmonedasbyUser(uid).addOnSuccessListener { esto ->
            Log.d("getMonedas de post ant>", " $monedasput")
            if (esto.exists()) {
                if (esto.contains("monedas")) {
                    monedasput = esto.get("monedas").toString()
                    Log.d("getMonedas despues post", " $monedasput")

                    val intent = Intent(context, PerfilActivityPost::class.java)
                    intent.putExtra("uid", uid)
                    intent.putExtra("monedasput", monedasput)
                    Log.d("monedas a enviar ->", " $monedasput")
                    this.context?.startActivity(intent)

                }
            }
            Log.d("getMonedas afuera post", " $monedasput")
        }
        Log.d("getMonedas retorno ", " $monedasput")


    }

    private fun actualizarMonedas(extisteMoneda: Int, like: Like) {
        val mPostProvider = PostProvider()
        val mUserProviders = UserProviders()
        if (like.tipo == "MeGusta") {
            mPostProvider.getUserByPost(like.idPost).addOnSuccessListener { estoPrimero ->
                if (estoPrimero.exists()) {
                    if (estoPrimero.contains("uid")) {
                        val uid = estoPrimero.get("uid")

                        mUserProviders.getNmonedasbyUser(uid.toString())
                            .addOnSuccessListener { esto ->
                                var Nmonedas = 0

                                if (esto.exists()) {
                                    if (esto.contains("monedas")) {
                                        Nmonedas = esto.get("monedas").toString().toInt()
                                        if (extisteMoneda > 0) {
                                            Nmonedas -= 1
                                        } else {
                                            Nmonedas += 1
                                        }

                                        mUserProviders.upDateMonedas(
                                            uid.toString(),
                                            Nmonedas.toString()
                                        )
                                            .addOnCompleteListener {
                                                if (it.isSuccessful) {

                                                } else {

                                                }
                                            }

                                    }

                                }
                            }


                    }
                }

            }

        }
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun SiExistelike(idPost: String, idUser: String, holder: ViewHolder) {
        mLikeProvider = LikeProvider()

        mLikeProvider.getLikeByPostAndUserAndILoveOrHate(idPost, idUser, "MeGusta").get()
            .addOnSuccessListener {
                val numberDocuments: Int = it.size()
                Log.d("megustasss1", "$numberDocuments")
                if (numberDocuments > 0) {

                    holder.itemView.imgLike.setImageResource(R.drawable.corazon_like)

                } else {

                    holder.itemView.imgLike.setImageResource(R.drawable.corazon_like_off)


                }
            }


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

    private fun getNombreActivity(context: Context?): String {
        var nombre = context.toString()
        var hasta: Int = 0
        var desde: Int = 0
        for (i in nombre.indices) {
            if (nombre[i].equals('@')) {
                hasta = i
            }
        }
        for (i in nombre.indices) {
            if (nombre[i].equals('.')) {
                desde = i + 1
            }
        }
        return nombre.substring(desde, hasta)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.cardview_post, parent, false)
        return ViewHolder(view)


    }


    public class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        init {


        }

    }
}


