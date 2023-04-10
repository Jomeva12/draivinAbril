package com.jomeva.driving.activities.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.RecyclerView
import com.jomeva.driving.R

import com.jomeva.driving.activities.modelos.Message
import com.jomeva.driving.activities.providers.ImageProvider
import com.jomeva.driving.activities.providers.MessagesProvider
import com.jomeva.driving.activities.providers.UserProviders
import com.jomeva.driving.activities.util.RelativeTime
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.cardview_message.view.*


class MessageAdapter(options: FirestoreRecyclerOptions<Message?>?, var context: Context?) :
    FirestoreRecyclerAdapter<Message, MessageAdapter.ViewHolder>(options!!) {


    private lateinit var mUserProvider: UserProviders
    private lateinit var mAuth: FirebaseAuth
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var globalRunnable: Runnable
    private lateinit var handler: Handler
    private var listaMediaPlayer: ArrayList<Map<String, Any?>> = ArrayList()
    private var mapMediaPlayer: Map<String, Any?> = mapOf()
    var banderita = "stop"
    var idDelBenditoPost = ""
    var mapaMensajes: Map<String, Any?> = mapOf()
    var posiciones = ArrayList<Int>()
    private lateinit var mImageProvider: ImageProvider
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: ViewHolder, position: Int, message: Message) {
        mImageProvider = ImageProvider()
        posiciones.add(position)

        val document: DocumentSnapshot = snapshots.getSnapshot(position)

        val idMensaje = document.id
        mAuth = FirebaseAuth.getInstance()
        mediaPlayer = MediaPlayer()
        handler = Handler(Looper.myLooper()!!)

        if (message.idSender == mAuth.currentUser!!.uid) {
            val params = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
            params.setMargins(100, 0, 0, 0)
            holder.itemView.linearLayoutMessage.layoutParams = params
            holder.itemView.linearLayoutMessage.setPadding(10, 5, 10, 15)
            //holder.itemView.linearLayoutMessage.setPadding(10, 20, 15, 20)
            holder.itemView.linearLayoutMessage.background =
                context!!.resources.getDrawable(R.drawable.bubble_corner_right, null)
            holder.itemView.textViewMessage.setTextColor(Color.BLACK)
            holder.itemView.textViewDate.setTextColor(Color.DKGRAY)
            holder.itemView.textViewDate.setPadding(515, 0, 10, 0)

            if (message.isViewed) {
                holder.itemView.checked.background =
                    context!!.resources.getDrawable(R.drawable.circular_visto, null)

            } else if (!message.isViewed) {
                holder.itemView.checked.background =
                    context!!.resources.getDrawable(R.drawable.border_circular_view, null)

            }


        } else {
            val params = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
            params.setMargins(0, 0, 100, 0)
            holder.itemView.linearLayoutMessage.layoutParams = params
            holder.itemView.linearLayoutMessage.setPadding(45, 5, -10, 15)
            // holder.itemView.linearLayoutMessage.setPadding(40, 20, 10, 20)
            holder.itemView.linearLayoutMessage.background =
                context!!.resources.getDrawable(R.drawable.bubble_corner_left, null)
            holder.itemView.textViewMessage.setTextColor(Color.BLACK)
            holder.itemView.textViewDate.setTextColor(Color.DKGRAY)
            holder.itemView.checked.visibility = View.GONE
            holder.itemView.textViewDate.setPadding(8, 0, 10, 0)

        }

        if (message.type == "imagen") {
            Picasso.get().load(message.url).error(R.drawable.ic_person_azul)
                .into(holder.itemView.imagenMensaje)

            holder.itemView.cardImageMessagge.visibility = View.VISIBLE

            holder.itemView.frameVideo.visibility = View.GONE
        } else if (message.type == "video") {

            holder.itemView.cardImageMessagge.visibility = View.GONE
            holder.itemView.frameVideo.visibility = View.VISIBLE
            Picasso.get().load(message.url).error(R.drawable.ic_person_azul)
                .into(holder.itemView.imagenMensaje)

        } else
            if (message.type == "audio") {

                holder.itemView.cardImageMessagge.visibility = View.GONE
                holder.itemView.frameVideo.visibility = View.GONE
                holder.itemView.textViewMessage.visibility = View.GONE
                holder.itemView.cardReproducir.visibility = View.VISIBLE
                holder.itemView.time.visibility = View.VISIBLE
                holder.itemView.time.text = message.nota
                holder.itemView.time.setTextColor(Color.DKGRAY)
                //  var media = MediaPlayer()
                //media.reset()
                //  media!!.setDataSource(message.url)
                // media!!.prepare()
                /*Log.d("entrandooo ", "${position}")
                mapMediaPlayer = mapOf("media" to media, "position" to position)
                listaMediaPlayer.add(mapMediaPlayer!!)*/

                //  mapaMensajes = mapOf("mensajeAudio" to message.url, "posicion" to position)
                // cargarAudios(message.url,media)

            } else {
                holder.itemView.cardImageMessagge.visibility = View.GONE
                holder.itemView.frameVideo.visibility = View.GONE
                holder.itemView.textViewMessage.visibility = View.GONE
                holder.itemView.cardReproducir.visibility = View.GONE
                holder.itemView.textViewMessage.visibility = View.VISIBLE
                holder.itemView.time.visibility = View.GONE

            }




        holder.itemView.textViewMessage.text = "${message.message}"


        val fecha = message.timestamp
        val time = RelativeTime.RelativeTime.timeFormatAMPM(fecha, context)
        holder.itemView.textViewDate.text = "$time"
/*
        holder.itemView.linearLayoutMessage.setOnClickListener {

        }*/



        holder.itemView.linearLayoutMessage.setOnLongClickListener {
                      val popup = PopupMenu(context, it)
            borrarMensaje(popup, message)
            return@setOnLongClickListener false
        }
        holder.itemView.imagenMensaje.setOnLongClickListener {
            Log.d("tratando desde imagen", "eliminar mensaje")
            val popup = PopupMenu(context, it)

            borrarMensaje(popup, message)
            return@setOnLongClickListener false
        }
        holder.itemView.textViewMessage.setOnLongClickListener {

            val popup = PopupMenu(context, it)

            borrarMensaje(popup, message)
            return@setOnLongClickListener false
        }

        holder.itemView.playAudioMensaje.setOnClickListener {

                reproducirElBenditoAudio(holder, message)






        }

    }


    private fun reproducirElBenditoAudio(holder: ViewHolder, message: Message) {

        if (idDelBenditoPost == "" || idDelBenditoPost == message.id) {
            if ("" == idDelBenditoPost) {


                idDelBenditoPost = message.id.toString()
                val mediap = MediaPlayer()
                mediap.setDataSource(message.url)
                mediap?.prepare()
                mediaPlayer = mediap
                metodoReproducir(
                    holder, message, banderita, mediaPlayer!!
                )
            } else {

                metodoReproducir(
                    holder,
                    message,
                    banderita,
                    mediaPlayer!!
                )
            }
        } else if (message.id.toString() != idDelBenditoPost) {


            var media = MediaPlayer()
            media.setDataSource(message.url)
            holder.itemView.seekReproduccionMensaje.progress = 0
            holder.itemView.seekReproduccionMensaje.max = 0

            mediaPlayer = media
            media?.prepare()
            banderita = "stop"

            metodoReproducir(
                holder,
                message,
                banderita,
                media!!
            )
        }
    }

    fun metodoReproducir(
        holder: ViewHolder,
        message: Message,
        banderita: String,
        mediap: MediaPlayer
    ) {
        if (banderita == "stop") {

            this.banderita = "enReproduccion"


            mediap?.start()

            Log.d("entrandooo2", "-----")

            holder.itemView.playAudioMensaje.background =
                context!!.getDrawable(R.drawable.btn_pause)
            incializarSeekBar(holder, message, mediap)

        } else if (banderita == "enReproduccion") {
            // holder.itemView.playAudioMensaje.background = context!!.getDrawable(R.drawable.btn_play)
            this.banderita = "stop"
            mediap?.pause()
            incializarSeekBar(holder, message, mediap)
            Log.d("banderita2", "->${this.banderita}")

        }

        holder.itemView.seekReproduccionMensaje.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {

                if (fromUser) {
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
    }

    fun incializarSeekBar(
        holder: ViewHolder,
        message: Message,
        mediap: MediaPlayer
    ) {

        if (::globalRunnable.isInitialized) {
            handler.removeCallbacks(globalRunnable)
        }



        holder.itemView.seekReproduccionMensaje.max = mediap!!.duration
        Log.d("tamaño", "${holder.itemView.seekReproduccionMensaje.max} ")
        val value = object
            : Runnable {
            override fun run() {
                handler.postDelayed(this, 100)
                holder.itemView.seekReproduccionMensaje.progress = mediap!!.currentPosition
                Log.d("tamaño2", "${holder.itemView.seekReproduccionMensaje.progress} ")

                val tiempo =
                    (mediaPlayer!!.duration / 1000) - (mediaPlayer!!.currentPosition / 1000).toLong()
                val time = RelativeTime.RelativeTime.timeAudio(tiempo)
                holder.itemView.time.text = time
                if (message.id.toString() != idDelBenditoPost) {
                    idDelBenditoPost = message.id.toString()
                    handler.removeCallbacks(this)
                    mediaPlayer = null
                    mediaPlayer = mediap
                    incializarSeekBar(holder, message, mediaPlayer!!)

                    mediaPlayer?.seekTo(0)
                    return
                }

                if ((mediap!!.currentPosition / 100 == mediap!!.duration / 100)) {
                    holder.itemView.seekReproduccionMensaje.progress = 0
                    holder.itemView.playAudioMensaje.background =
                        context!!.getDrawable(R.drawable.btn_play)
                    banderita = "stop"
                    handler.removeCallbacks(this)
                }

            }
        }
        globalRunnable = value
        handler.postDelayed(value, 0)
    }

    private fun borrarMensaje(popup: PopupMenu, message: Message) {
        val inflater: MenuInflater = popup.menuInflater
        val mMessagesProvider = MessagesProvider()

        inflater.inflate(R.menu.menu_mensaje, popup.menu)
        for (i in 0 until popup.menu.size()) {
            val item = popup.menu.getItem(i)
            val spanString = SpannableString(popup.menu.getItem(i).title.toString())
            spanString.setSpan(
                ForegroundColorSpan(getColor(context!!, R.color.white)),
                0,
                spanString.length,
                0
            ) //fix the color to white
            item.title = spanString
        }


        popup.setOnMenuItemClickListener {
            when (it!!.itemId) {
                R.id.eliminarMensaje -> {
                    mMessagesProvider.deleteMessage(message.id.toString())
                    if (message.nameFile!=""){
                        mImageProvider.deleteFile(message.nameFile)
                    }

                    return@setOnMenuItemClickListener true
                }
                else -> {

                    return@setOnMenuItemClickListener true
                }
            }
        }
        popup.show()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.cardview_message, parent, false)

        return ViewHolder(view)
    }


    public class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        init {
            var textViewMessage: TextView = view.findViewById(R.id.textViewMessage)
            var textViewDate: TextView = view.findViewById(R.id.textViewDate)
            // var imageCheck: ImageView = view.findViewById(R.id.imageCheck)

        }

    }



}