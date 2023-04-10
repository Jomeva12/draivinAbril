package com.jomeva.driving.activities.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.*
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import com.jomeva.driving.R
import com.jomeva.driving.activities.adapters.MessageAdapter
import com.jomeva.driving.activities.modelos.Chats
import com.jomeva.driving.activities.modelos.FCMBody
import com.jomeva.driving.activities.modelos.FCMResponse
import com.jomeva.driving.activities.modelos.Message
import com.jomeva.driving.activities.providers.*
import com.jomeva.driving.activities.util.RelativeTime
import com.jomeva.driving.activities.util.ViewedMessageHelper
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.fxn.pix.Options
import com.fxn.pix.Pix
import com.fxn.utility.PermUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.gson.Gson
import com.jomeva.driving.activities.util.PreferenciasGenerales
import com.squareup.picasso.Picasso
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions
import kotlinx.android.synthetic.main.activity_chat.*
import retrofit2.Call
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.properties.Delegates


class ChatActivity : AppCompatActivity() {

    companion object {


    }

    private lateinit var mEmojIconActions: EmojIconActions
    private lateinit var mExtraUserId2: String
    private lateinit var mExtraUserId1: String

    private var mIdNotificationChat by Delegates.notNull<Long>()
    private lateinit var mExtraidChat: String
    private lateinit var mAuth: FirebaseAuth
    private var activo: Boolean = true
    private lateinit var mImageSender: String
    private lateinit var mChatsProvider: ChatsProvider
    private lateinit var mUserProvider: UserProviders

    private lateinit var mMessagesProvider: MessagesProvider
    lateinit var mlinearLayoutManager: LinearLayoutManager
    private lateinit var mMinombre: String
    private lateinit var mMessageAdapter: MessageAdapter
    private lateinit var mNotificationProviders: NotificationProvider
    private lateinit var mTokenProvider: TokenProvider

    //fotos
    private lateinit var mImageProvider: ImageProvider
    var mReturnValues: ArrayList<String> = ArrayList()
    private var mOptiones: Options? = null
    private var mImageFile: File? = null

    ///////////////////////////grabador de audio
    private var mRecorder: MediaRecorder? = null
    private var mFileNameRecord: String? = null
    private var durationMs = 0
    private var mediaPlayer: MediaPlayer? = null
    private var banderaReproduccion: String = "stop"

    ////////////////////////
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        //reciclerViewChatprivate.setBackgroundColor(ContextCompat.getColor(this, R.color.fullBlack))
        mEmojIconActions = EmojIconActions(this, vistaChat, txtMensaje, emojiMensage)
        mEmojIconActions.ShowEmojIcon()
        mediaPlayer = MediaPlayer()
        mImageProvider = ImageProvider()
        mAuth = FirebaseAuth.getInstance()
        mUserProvider = UserProviders()
        mChatsProvider = ChatsProvider()
        mMessagesProvider = MessagesProvider()
        mNotificationProviders = NotificationProvider()

        mTokenProvider = TokenProvider()
        mExtraUserId1 = intent.getStringExtra("user1").toString()
        mExtraUserId2 = intent.getStringExtra("user2").toString()
        mExtraidChat = intent.getStringExtra("idChat").toString()

        mlinearLayoutManager = LinearLayoutManager(this)
        mlinearLayoutManager.stackFromEnd = true
        reciclerViewChatprivate.layoutManager = this.mlinearLayoutManager


        mOptiones = Options.init()
            .setRequestCode(100) //Request code for activity results
            .setCount(1) //Number of images to restict selection count
            .setFrontfacing(false) //Front Facing camera on start
            .setPreSelectedUrls(mReturnValues) //Pre selected Image Urls
            .setSpanCount(5) //Span count for gallery min 1 & max 5
            .setMode(Options.Mode.Picture) //Option to select only pictures or videos or both
            .setVideoDurationLimitinSeconds(0) //Duration for video recording
            .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT) //Orientaion
            .setPath("/pix/images")

        txtMensaje.addTextChangedListener {

            if (txtMensaje.text.toString() != "") {
                sendMensaje.visibility = View.VISIBLE
                grabadorMic.visibility = View.GONE
            } else {
                sendMensaje.visibility = View.GONE
                grabadorMic.visibility = View.VISIBLE
            }

        }


        /*grabadorMic.setOnClickListener {


        }*/
        backToHome.setOnClickListener {

            finish()
        }
        sendMensaje.setOnClickListener {
            enviarMensaje()
        }
        icCamara.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                startPix()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    listOf<String>(Manifest.permission.WRITE_EXTERNAL_STORAGE).toTypedArray(),
                    PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS
                )
            }

        }
        deleteAudio.setOnClickListener {
            mediaPlayer?.reset()
            mFileNameRecord = null
            startHTime = 0L
            timeInMilliseconds = 0L
            timeSwapBuff = 0L
            updatedTime = 0L
            cardGrabacion.visibility = View.GONE
            grabadorMic.visibility = View.VISIBLE
            sendMensaje.visibility = View.GONE
            cardInputMessage.visibility = View.VISIBLE
        }
        checkChatExist()
        grabador()

    }

    fun hideTeclado(view: View) {
        var imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

        Log.d("tecladoo", "$view")
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun grabador() {
        grabadorMic.setOnTouchListener { view, motionEvent ->
            if (ContextCompat.checkSelfPermission(
                    this!!,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this!!,
                    android.Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(
                    this,
                    "Por favor concede permisos para acceder al micrófono, ve a configuraciones/aplicaciones/Draivin/permisos",
                    Toast.LENGTH_LONG
                ).show()


                ActivityCompat.requestPermissions(
                    this!!,
                    arrayOf(
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.RECORD_AUDIO
                    ),
                    1000
                )
                Log.d("banderaa2", "Grabó-> $mFileNameRecord")
            } else {
                Log.d("banderaa3", "Grabó-> $mFileNameRecord")

                if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                    // MessageAdapter(null,this).pausarAudios()
                    mediaPlayer?.reset()
                    mFileNameRecord = null
                    startHTime = 0L
                    timeInMilliseconds = 0L
                    timeSwapBuff = 0L
                    updatedTime = 0L
                    grabadorMic.setAnimation("microfono.json")
                    grabadorMic.playAnimation()
                    txtMensaje.clearFocus()
                    cardGrabando.visibility = View.VISIBLE
                    cardGrabacion.visibility = View.GONE
                    cardInputMessage.visibility = View.GONE
                    Log.d("banderaa4", "Grabó-> $mFileNameRecord")
                    grabando()

                } else if (motionEvent.action == MotionEvent.ACTION_UP) {
                    hideTeclado(view)
                    stopRecording()
                    cardGrabando.visibility = View.GONE
                    cardGrabacion.visibility = View.VISIBLE
                    cardInputMessage.visibility = View.GONE
                    grabadorMic.visibility = View.GONE
                    sendMensaje.visibility = View.VISIBLE
                    controlReproducir()
                    Log.d("banderaa", "Grabó-> $mFileNameRecord")

                }
            }
            return@setOnTouchListener true
        }
    }

    private fun grabando() {

        // if (mRecorder == null) {
        var nombre = UUID.randomUUID().toString() + ".3gp"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1) {
            mFileNameRecord = this!!.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
                .toString() + "/$nombre"
        } else {
            mFileNameRecord = Environment.getExternalStorageDirectory().toString() + "/$nombre"
        }



        mRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB)
            setOutputFile(mFileNameRecord)
            Log.d("mFileNameRecord", "->$mFileNameRecord")
            try {
                prepare()
                start()
            } catch (e: IOException) {
                Log.e("LOG_TAG", "$e prepare() failed")
            }
            startHTime = SystemClock.uptimeMillis();
            customHandler.postDelayed(updateTimerThread, 0);
// acá debe cambiar el boton de grabando

        }
        // }

    }

    private val customHandler = Handler()
    private var startHTime = 0L
    var timeInMilliseconds = 0L
    var timeSwapBuff = 0L
    var updatedTime = 0L


    private val updateTimerThread: Runnable = object : Runnable {
        override fun run() {

            timeInMilliseconds = SystemClock.uptimeMillis() - startHTime
            updatedTime = timeSwapBuff + timeInMilliseconds
            var secs = (updatedTime / 1000).toInt()
            val mins = secs / 60
            secs = secs % 60
            if (timeRecording != null) timeRecording!!.text =
                ("" + String.format("%02d", mins) + ":"
                        + String.format("%02d", secs))
            customHandler.postDelayed(this, 0)
        }
    }

    private fun stopRecording() {
        Log.d("tiempo6=", "$mRecorder.")


        try {
            mRecorder?.apply {

                Handler(Looper.getMainLooper()).postDelayed({
                    stop()
                    release()
                    grabadorMic.setAnimation("microfono_off.json")
                }, 250)

            }

            timeSwapBuff += timeInMilliseconds;
            customHandler.removeCallbacks(updateTimerThread);
            mRecorder = null

        } catch (e: IOException) {
            Log.e("mRecorder", "$e")
        }


    }

    fun controlReproducir() {
        btnPlayAudioChat.setOnClickListener {
            Log.d("banderaa", "$banderaReproduccion")
            when (banderaReproduccion) {
                "stop" -> {
                    //reproducir 1 vez
                    btnPlayAudioChat.background = this!!.getDrawable(R.drawable.btn_pause)
                    banderaReproduccion = "play"
                    try {
                        mediaPlayer?.reset()
                        mediaPlayer?.setDataSource(mFileNameRecord)
                        mediaPlayer?.prepare()
                        incializarSeekBar()
                        Log.d("mediaplayerrr1", "ID: ${mediaPlayer?.audioSessionId}")

                    } catch (e: IOException) {
                        Log.e("LOG_TAG", "$e prepare() failed")
                    }
                    mediaPlayer?.start()


                }
                "play" -> {
                    //pausar
                    btnPlayAudioChat.background = this!!.getDrawable(R.drawable.btn_play)
                    //v.btnPlayAudio.background = resources.getDrawable(R.drawable.btn_pause, null)
                    if (mediaPlayer !== null) mediaPlayer?.pause()
                    banderaReproduccion = "pause"
                    Log.d("mediaplayerrr2", "ID: ${mediaPlayer?.audioSessionId}")

                }
                "pause" -> {

                    btnPlayAudioChat.background = this!!.getDrawable(R.drawable.btn_pause)
                    mediaPlayer?.start()
                    incializarSeekBar()
                    Log.d("mediaplayerrr6", "ID: ${mediaPlayer?.audioSessionId}")
                    banderaReproduccion = "play"
                }
            }


        }
        seekReproduccionChat.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                Log.d("mediaplayerrr11", "${progress / 1000} ->${seekReproduccionChat.max / 1000} ")
                if (progress / 100 == seekReproduccionChat.max / 100) {
                    // mediaPlayer?.stop()
                    btnPlayAudioChat.background = getDrawable(R.drawable.btn_play)

                    seekReproduccionChat.progress = 0
                    seekReproduccionChat.max = 0
                    banderaReproduccion = "stop"
                    Log.d("mediaplayerrr8", "stop ")

                }
                if (fromUser) {
                    Log.d("mediaplayerrr10", "$fromUser ")

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

    fun incializarSeekBar() {
        if (mediaPlayer != null) {
            seekReproduccionChat.max = mediaPlayer!!.duration
        }

        val handler = Handler()
        handler.postDelayed(object : Runnable {

            override fun run() {
                try {
                    seekReproduccionChat.progress = mediaPlayer!!.currentPosition
                    handler.postDelayed(this, 100)
                    //if (mediaPlayer!!.currentPosition/1000>0 ) {
                    val tiempo =
                        (mediaPlayer!!.duration / 1000) - (mediaPlayer!!.currentPosition / 1000).toLong()
                    val times = RelativeTime.RelativeTime.timeAudio(tiempo)
                    Log.d("mediaplayerrr15", "${mediaPlayer!!.currentPosition} ")
                    time.text = times
                    if (mediaPlayer!!.currentPosition / 100 == mediaPlayer!!.duration / 100) {
                        seekReproduccionChat.progress = 0
                        seekReproduccionChat.max = 0
                        Log.d("mediaplayerrr16", "${mediaPlayer!!.currentPosition} ")
                        handler.removeCallbacks(this)
                    }
                    // }
                } catch (e: Exception) {
                    seekReproduccionChat.progress = 0
                }
            }
        }, 0)

    }

    private fun getToken(message: Message) {
        getMyInfo()
        var idUser = ""
        if (mAuth.currentUser?.uid == mExtraUserId1) {
            idUser = mExtraUserId2
        } else {
            idUser = mExtraUserId1
        }

        mTokenProvider.getToken(idUser).addOnSuccessListener {
            if (it.exists()) {
                if (it.contains("token")) {
                    val token = it.getString("token")!!
                    getLast3Messages(message, token)
                }
            } else {

            }
        }
    }

    private fun getLast3Messages(message: Message, token: String) {
        if (activo) {
            Log.d("panta", "No enviará")
        } else {
            Log.d("panta", "Enviará")
        }

        mMessagesProvider.getMessagebychatAndSender(mExtraidChat, mAuth.currentUser!!.uid).get()
            .addOnSuccessListener {
                val messagesArray: ArrayList<Message> = ArrayList()
                for (d in it) if (d.exists()) {
                    val message: Message = d.toObject(Message::class.java)
                    messagesArray.add(message)
                }

                if (messagesArray.size == 0) {
                    messagesArray.add(message)
                }
                //messagesArray.reverse()

                val gson = Gson()
                val messages = gson.toJson(messagesArray)


                var map = mutableMapOf("title" to "Nuevo mensaje:")
                map["body"] = message.message!!
                map.put("idNotification", mIdNotificationChat.toString())
                map.put("messages", messages)
                map.put("mMinombre", mMinombre)
                map.put("tipo", "mensaje")
                map.put("user1", mExtraUserId1)
                map.put("user2", mExtraUserId2)
                map.put("idChat", mExtraidChat)
                // map.put("mImageSender", mImageSender)
                Log.d("pantalla3", " -->$mExtraUserId1")
                Log.d("pantalla4", " -->$mExtraUserId2")

                //for para enviar a todos y el tokens debe ir aumentando
                var body = FCMBody(token, "high", "4500s", map)

                mNotificationProviders.sendNotification(body)!!
                    .enqueue(object : retrofit2.Callback<FCMResponse?> {
                        override fun onResponse(
                            call: Call<FCMResponse?>?,
                            response: Response<FCMResponse?>
                        ) {
                            if (response.body() != null) {

                                if (response.body()!!.success == 1) {
                                    Log.d("Enviado...", "${response}")
                                } else {

                                }
                            } else {


                            }
                        }

                        override fun onFailure(call: Call<FCMResponse?>?, t: Throwable?) {}
                    })
            }
    }
//esto lo saqué porque lo necesitba para el listener de para audio mientar se graba


    override fun onStart() {
        super.onStart()

        ViewedMessageHelper().updateOnline(true, this)
        var query: Query = mMessagesProvider.getChatbyId(mExtraidChat)
        var options: FirestoreRecyclerOptions<Message?> =
            FirestoreRecyclerOptions.Builder<Message>().setQuery(
                query,
                Message::class.java
            ).build()
//acá es donde está de por que no me muestra los mensajes del chat
        PreferenciasGenerales.setIdChat(mExtraidChat)

        mMessageAdapter = MessageAdapter(options, this)
        reciclerViewChatprivate.adapter = this.mMessageAdapter
        mMessageAdapter.startListening()

        mMessageAdapter.registerAdapterDataObserver(object : AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {

                super.onItemRangeInserted(positionStart, itemCount)

                getVisto()

                val numberMessage: Int = mMessageAdapter.itemCount
                val lastMessagePosition: Int =
                    mlinearLayoutManager.findLastCompletelyVisibleItemPosition()
                if (lastMessagePosition == -1 || positionStart >= numberMessage - 1 && lastMessagePosition == positionStart - 1) {
                    reciclerViewChatprivate.scrollToPosition(positionStart)
                }
            }
        })

    }


    override fun onPause() {
        super.onPause()
        ViewedMessageHelper().updateOnline(false, this)
        PreferenciasGenerales.setIdChat("")
    }

    override fun onStop() {
        super.onStop()
        if (mMessageAdapter != null) {
            mMessageAdapter.stopListening()
        }


    }

    private fun enviarMensaje() {
        var mensajeInput: String = txtMensaje.text.toString()
        var mensaje = Message()
        val url = ""
        val nota = ""
        if (mensajeInput.isNotEmpty()) {
            guardarMensaje(mensaje, url, "", nota, "")
        } else if (mFileNameRecord != null) {
            cardGrabando.visibility = View.GONE
            cardGrabacion.visibility = View.GONE
            cardInputMessage.visibility = View.VISIBLE
            grabadorMic.visibility = View.VISIBLE
            sendMensaje.visibility = View.GONE
            mediaPlayer!!.reset()
            mediaPlayer!!.setDataSource(mFileNameRecord)
            mediaPlayer!!.prepare()


            val tiempo = RelativeTime.RelativeTime.timeAudio(mediaPlayer!!.duration / 1000.toLong())
            Log.d("mediaaa", "$mediaPlayer -> $tiempo")
            saveAudio(mensaje, url, tiempo)
            mFileNameRecord = null
        }

    }

    private fun guardarMensaje(
        mensaje: Message,
        url: String,
        type: String,
        nota: String,
        nameFile: String
    ) {
        var texto = txtMensaje.text.toString()
        if (texto == "") {
            texto = "Audio: \uD83C\uDF99"

        }
        mensaje.idChat = mExtraidChat
        if (mExtraUserId1 == mAuth.currentUser?.uid) {
            mensaje.idSender = mExtraUserId1
            mensaje.idReceiver = mExtraUserId2
        } else {
            mensaje.idSender = mExtraUserId2
            mensaje.idReceiver = mExtraUserId1
        }
        mensaje.timestamp = Date().time
        mensaje.isViewed = false
        mensaje.idChat = mExtraidChat
        mensaje.url = url
        mensaje.nota = nota
        mensaje.type = type
        mensaje.message = texto
        mensaje.nameFile=nameFile

        txtMensaje.setText("")
        mMessagesProvider.create(mensaje).addOnCompleteListener {
            if (it.isSuccessful) {
                getToken(mensaje)

                mMessageAdapter.notifyDataSetChanged()
            } else {

            }
        }
    }


    private fun saveAudio(mensaje: Message, url: String, nota: String) {
        val type = "audio"
        val nombre = UUID.randomUUID().toString()
        //val thread = Thread {
        mImageProvider.saveAudio(this!!, mFileNameRecord!!, nombre)
            .addOnCompleteListener { it ->
                if (it.isSuccessful) {
val nameFile=mImageProvider.mstorage.name
                    Log.d("borandoFile2","${ mImageProvider.getStorage().name}")
                    mImageProvider.getStorage().downloadUrl.addOnSuccessListener { uriPerfil ->
                        val urlAudio: String = uriPerfil.toString()
                        guardarMensaje(mensaje, urlAudio, type, nota,nameFile)

                    }
                } else {
                    //No se pudo guardar
                }
            }
        //}
        // thread.start()
        //mAlertDialog.dismiss()

    }

    private fun crearChat() {

        Log.d("useri 1", "$mExtraUserId1")
        Log.d("useri 2", "$mExtraUserId2")
        var chat: Chats = Chats()
        chat.idUser1 = mExtraUserId1
        chat.idUser2 = mExtraUserId2
        var aletorio = Random()
        chat.idNotification = aletorio.nextInt(1000000).toLong()
        mIdNotificationChat = chat.idNotification
        chat.isWriting = false
        chat.idChats = chat.idUser1 + chat.idUser2
        chat.timestamp = Date().time
        val list: ArrayList<String> = ArrayList()
        list.add("${chat.idUser1}")
        list.add("${chat.idUser2}")
        chat.ids = list
        mChatsProvider.create(chat)
    }

    private fun checkChatExist() {
        Log.d("mExtraUserId1", "$mExtraUserId1")
        Log.d("mExtraUserId2", "$mExtraUserId2")
        Log.d("mExtraidChat", "$mExtraidChat")
        mChatsProvider.getChatUser1User2(mExtraUserId1, mExtraUserId2).get().addOnSuccessListener {
            val size: Int = it.size()
            Log.d("size donde--->", "$size")
            if (size == 0) {
                crearChat()
                //mExtraidChat=mExtraUserId1+mExtraUserId2
                getDataUser2()
            } else {
                mExtraidChat = it.documents[0].id
                mIdNotificationChat = it.documents[0].getLong("idNotification")!!
                Log.d("id notif", "${it.documents[0].getLong("idNotification")!!}")
                getDataUser2()
                getVisto()
            }
        }
    }

    private fun getVisto() {
        var idSender = ""
        if (mAuth.currentUser?.uid == mExtraUserId1) {
            idSender = mExtraUserId2
        } else {
            idSender = mExtraUserId1
        }
        mMessagesProvider.getMessagebychatAndSender(mExtraidChat, idSender).get()
            .addOnSuccessListener {

                run {
                    for (doc: DocumentSnapshot in it.documents) {
                        mMessagesProvider.upDateVisto(doc.id, true)

                    }
                }
            }
    }

    private fun getDataUser2() {
        val user: String = if (mAuth.currentUser?.uid == mExtraUserId1) {
            mExtraUserId2
        } else {
            mExtraUserId1
        }

        mUserProvider.getUserRealtime(user).addSnapshotListener { it, e ->

            if (it!!.exists()) {
                if (it.contains("nombre")) {
                    nombreUsuario.setText("${it.get("nombre")}")
                }
                if (it.contains("online")) {
                    val online = it.getBoolean("online")
                    if (online == true) {
                        textViewRelativeTime.text = "En línea"
                    } else if (it.contains("lastConnect")) {
                        val lastConection: Long = it.get("lastConnect") as Long
                        textViewRelativeTime.text =
                            RelativeTime.RelativeTime.getTimeAgo(lastConection, this)
                    }
                    nombreUsuario.setText("${it.get("nombre")}")
                }


                if (it.contains("photo")) {
                    var imagenPerfil: String = it.get("photo").toString()
                    if (it.get("photo").toString() == "") {
                        Picasso.get().load(R.drawable.ic_person_azul)
                            .error(R.drawable.ic_person_azul).into(imagenchat)
                    } else {
                        Picasso.get().load(imagenPerfil).error(R.drawable.ic_person_azul)
                            .into(imagenchat)
                    }


                }

            }


        }
    }

    private fun getMyInfo() {
        mUserProvider.getUsuario(mAuth.currentUser!!.uid).addOnSuccessListener {
            if (it.exists())
                if (it.contains("nombre")) {
                    mMinombre = it.getString("nombre").toString()

                }
            if (it.contains("photo")) {
                mImageSender = it.getString("photo").toString()

            }
        }
    }

    //////////fotos
    private fun startPix() {
        Pix.start(this, mOptiones);
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == 100) {
            mReturnValues = data?.getStringArrayListExtra(Pix.IMAGE_RESULTS)!!
            // mImageFile = File(mReturnValues[0])
            mImageFile = File(mReturnValues[0])
            val intent = Intent(this, ConfirmImageSendActivity::class.java)
            intent.putExtra("intencion", "enviaImagen")
            intent.putExtra("data", mReturnValues)
            intent.putExtra("idChat", mExtraidChat)
            intent.putExtra("idReceiver", mExtraUserId2)
            intent.putExtra("mImageFile", mImageFile)
            intent.putExtra("idSender", mAuth.currentUser?.uid)
            startActivity(intent)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Pix.start(this, mOptiones)
            } else {

                Toast.makeText(
                    this,
                    "Por favor concede permisos para acceder a la cámara, ve a configuraciones/aplicaciones/Draivin/permisos",
                    Toast.LENGTH_LONG
                ).show()
            }
            // return

        }
        if (requestCode == 100) {

        }
    }
}