package com.jomeva.driving.activities.activities


//import kotlinx.coroutines.Runnable

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.*
import android.preference.PreferenceManager
import android.provider.Settings
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import android.util.Patterns
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jomeva.driving.R
import com.jomeva.driving.activities.adapters.ComentarioAdapter
import com.jomeva.driving.activities.adapters.PostsAdapter
import com.jomeva.driving.activities.modelos.*
import com.jomeva.driving.activities.providers.*
import com.jomeva.driving.activities.util.*
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.fxn.pix.Options
import com.fxn.pix.Pix
import com.fxn.utility.PermUtil
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.picasso.Picasso
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions
import kotlinx.android.synthetic.main.cuadro_comentarios_eventos_transparentes.*
import kotlinx.android.synthetic.main.cuadro_comentarios_eventos_transparentes.view.*
import kotlinx.android.synthetic.main.cuadro_mensaje_check.view.*
import kotlinx.android.synthetic.main.cuadro_post.view.*
import kotlinx.android.synthetic.main.cuadro_preguntar.view.*
import kotlinx.android.synthetic.main.fragment_one.*
import org.jsoup.Jsoup
import retrofit2.Call
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


class FragmentOne : Fragment, MyListener, OnFragmentAddedListener {
    constructor(context: Context) : this() {
        myContext = context
    }

    constructor()

    private lateinit var viewModel: MyViewModel

    //////////////////////////////////comentarios bottomSheets
    lateinit var mlinearLayoutManagerComentario: LinearLayoutManager
    private lateinit var mComentarioAdapter: ComentarioAdapter
    private lateinit var mComentarioProvider: ComentarioProvider
    lateinit var mReciclerViewComentario: RecyclerView
    private lateinit var mComentarios: Comentarios
    private lateinit var mTokenProvider: TokenProvider
    private lateinit var mNotificationProviders: NotificationProvider
    private lateinit var mPublicacionPorAprobarProvider: PublicacionPorAprobarProvider

    /////////////////////////////////
    private lateinit var mFragmentTwo: FragmentTwo

    ///////////////////////////grabador de audio
    private var mRecorder: MediaRecorder? = null
    private var mFileNameRecord: String? = null
    private var durationMs = 0
    private var mediaPlayer: MediaPlayer? = null
    private var banderaReproduccion: String = "stop"

    ////////////////////////
    //post
    private var mFile: File? = null
    private var mPhotoFile: File? = null

    private var ciudadExiste: Boolean = false
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mprovider: PostProvider
    private lateinit var mUserProviders: UserProviders
    private lateinit var mBuilderSelector: AlertDialog.Builder
    private lateinit var mAbsolutePhotoPath: String
    private lateinit var mPhotoPath: String
    private var cambioDeCiudad: Boolean = false
    private lateinit var vista: View
    private lateinit var malertDialog: AlertDialog
    private lateinit var mOptions: Array<CharSequence>
    private var PHOTO_REQUEST_CODE = 3
    private var GALLERY_REQUEST_CODE = 1

    var mMyListener: MyListener? = null

    ////
    private lateinit var ciudadUser: String
    lateinit var mReciclerView: RecyclerView
    var mLastPosition: Int = 0
    lateinit var mlinearLayoutManager: LinearLayoutManager
    lateinit var mPostProvider: PostProvider
    private var mPostAdapter: PostsAdapter? = null
    private var myVariable: Boolean = false
    private var myContext: Context? = null
    private var mOptiones: Options? = null
    private lateinit var mImageProvider: ImageProvider
    var mReturnValues: ArrayList<String> = ArrayList()
    private var mImageFile: File? = null
    lateinit var mView: View
    private lateinit var mAlertDialog: AlertDialog


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mPostProvider = PostProvider()
        mComentarioProvider = ComentarioProvider()
        mImageProvider = ImageProvider()
        mediaPlayer = MediaPlayer()
        mprovider = PostProvider()
        mUserProviders = UserProviders()
        mPublicacionPorAprobarProvider = PublicacionPorAprobarProvider()

        mBuilderSelector = AlertDialog.Builder(requireContext())
        mPhotoPath = ""
        mFile = null
        mAuth = FirebaseAuth.getInstance()

        viewModel = ViewModelProvider(this).get(MyViewModel::class.java)
        viewModel.setContexto(context!!)
       // adapterPost()
        Log.d("listenerrrr9", "dasdasd")
        mFragmentTwo = FragmentTwo()
        mOptiones = Options.init()
            .setRequestCode(100) //Request code for activity results
            .setCount(1) //Number of images to restict selection count
            .setFrontfacing(false) //Front Facing camera on start
            .setPreSelectedUrls(mReturnValues) //Pre selected Image Urls
            .setSpanCount(4) //Span count for gallery min 1 & max 5
            .setMode(Options.Mode.Picture) //Option to select only pictures or videos or both
            .setVideoDurationLimitinSeconds(0) //Duration for video recording
            .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT) //Orientaion
            .setPath("/pix/images")


        swipeRefreshLayout.isRefreshing = true

        swipeRefreshLayout.setOnRefreshListener {
            adapterPost()
            Toast.makeText(requireContext(), "Actualizando", Toast.LENGTH_LONG).show()
        }


        abrirPoster.setOnClickListener {

            //verifica permisos
            val users = mAuth.currentUser
            if (users != null) {
                alertDialogPost()
            } else {
                mensajePersonalizadoLogin(context!!)
            }


            // config.visibility=View.GONE

        }

        // sendNotificationEventosGrupo()
    }


    //para mostrar tiempo de grabación en el textView

    private val customHandler = Handler()
    private var startHTime = 0L
    var timeInMilliseconds = 0L
    var timeSwapBuff = 0L
    var updatedTime = 0L

    //tiempo para audios
    private val updateTimerThread: Runnable = object : Runnable {
        override fun run() {

            timeInMilliseconds = SystemClock.uptimeMillis() - startHTime
            updatedTime = timeSwapBuff + timeInMilliseconds
            var secs = (updatedTime / 1000).toInt()
            val mins = secs / 60
            secs = secs % 60
            if (mView.timeRecording != null) mView.timeRecording!!.text =
                ("" + String.format("%02d", mins) + ":"
                        + String.format("%02d", secs))
            customHandler.postDelayed(this, 0)
        }
    }

    fun visitaGuiadaPoster() {
        TapTargetView.showFor(
            activity,
            TapTarget.forView(
                abrirPoster,
                "Crear publicaciones",
                "Puedes compartir información útil para los usuarios por medio de descripción, imégenes o audios en tu ciudad."
            )
                .outerCircleAlpha(0.96f)
                .targetCircleColor(R.color.rojiso)
                .titleTextSize(23)
                .titleTextColor(R.color.white)
                .descriptionTextSize(18)
                .descriptionTextColor(R.color.white)
                .textColor(R.color.white)
                .textTypeface(Typeface.SANS_SERIF)
                .dimColor(R.color.azulGuiada)
                .outerCircleColorInt(resources.getColor(R.color.azulGuiada))
                .drawShadow(true)
                .cancelable(false)
                .tintTarget(true)
                .transparentTarget(true)
                .targetRadius(60),
            object : TapTargetView.Listener() {

                override fun onTargetClick(view: TapTargetView) {
                    super.onTargetClick(view)

                    PreferenciasGenerales(context!!).setpreferenceGuiaPoster("true")
                    alertDialogPost()
                }
            }
        )

    }

    private fun guardarAudio() {
        if (mFileNameRecord != null) {
            val name = UUID.randomUUID().toString()
            mImageProvider.saveAudio(context!!, mFileNameRecord!!, name).addOnCompleteListener {
                if (it.isSuccessful) {
                    val url = mImageProvider.getStorage().downloadUrl.toString()
                    Toast.makeText(requireContext(), "Guardado en $url", Toast.LENGTH_LONG).show()

                } else {
                    Toast.makeText(requireContext(), "error en guardar", Toast.LENGTH_LONG).show()

                }
            }
        }
    }

    private fun grabando(view: View) {

        // if (mRecorder == null) {
        var nombre = UUID.randomUUID().toString() + ".3gp"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1) {
            mFileNameRecord = activity!!.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
                .toString() + "/$nombre"
        } else {
            mFileNameRecord = Environment.getExternalStorageDirectory().toString() + "/$nombre"
        }


        Log.d("mFileNameRecord0", "->$mFileNameRecord")
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


    fun controlReproducir(v: View) {
        v.btnPlayAudio.setOnClickListener {
            when (banderaReproduccion) {
                "stop" -> {
                    //reproducir 1 vez
                    v.btnPlayAudio.background = context!!.getDrawable(R.drawable.btn_pause)
                    banderaReproduccion = "play"
                    try {
                        mediaPlayer?.reset()
                        mediaPlayer?.setDataSource(mFileNameRecord)
                        mediaPlayer?.prepare()
                        incializarSeekBar(v)
                        Log.d("mediaplayerrr1", "ID: ${mediaPlayer?.audioSessionId}")

                    } catch (e: IOException) {
                        Log.e("LOG_TAG", "$e prepare() failed")
                    }
                    mediaPlayer?.start()


                }
                "play" -> {
                    //pausar
                    v.btnPlayAudio.background = context!!.getDrawable(R.drawable.btn_play)
                    //v.btnPlayAudio.background = resources.getDrawable(R.drawable.btn_pause, null)
                    if (mediaPlayer !== null) mediaPlayer?.pause()
                    banderaReproduccion = "pause"
                    Log.d("mediaplayerrr2", "ID: ${mediaPlayer?.audioSessionId}")

                }
                "pause" -> {

                    v.btnPlayAudio.background = context!!.getDrawable(R.drawable.btn_pause)
                    mediaPlayer?.start()
                    incializarSeekBar(v)
                    Log.d("mediaplayerrr6", "ID: ${mediaPlayer?.audioSessionId}")
                    banderaReproduccion = "play"
                }
            }


        }
        v.seekReproduccion.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                Log.d("mediaplayerrr11", "${progress / 1000} ->${v.seekReproduccion.max / 1000} ")
                if (progress / 1000 == v.seekReproduccion.max / 1000) {
                    // mediaPlayer?.stop()
                    v.btnPlayAudio.background = context!!.getDrawable(R.drawable.btn_play)
                    v.seekReproduccion.progress = 0
                    v.seekReproduccion.max = 0
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

    fun incializarSeekBar(v: View) {
        if (mediaPlayer != null) {
            v.seekReproduccion.max = mediaPlayer!!.duration
        }

        val handler = Handler()
        handler.postDelayed(object : Runnable {

            override fun run() {
                try {
                    v.seekReproduccion.progress = mediaPlayer!!.currentPosition
                    handler.postDelayed(this, 100)
                    //if (mediaPlayer!!.currentPosition/1000>0 ) {
                    val tiempo =
                        (mediaPlayer!!.duration / 1000) - (mediaPlayer!!.currentPosition / 1000).toLong()
                    val time = RelativeTime.RelativeTime.timeAudio(tiempo)
                    Log.d("mediaplayerrr15", "${mediaPlayer!!.currentPosition} ")
                    v.time.text = time
                    if (mediaPlayer!!.currentPosition / 100 == mediaPlayer!!.duration / 100) {
                        v.seekReproduccion.progress = 0
                        v.seekReproduccion.max = 0
                        Log.d("mediaplayerrr16", "${mediaPlayer!!.currentPosition} ")
                        handler.removeCallbacks(this)
                    }
                    // }
                } catch (e: Exception) {
                    v.seekReproduccion.progress = 0
                }
            }
        }, 0)

    }

    private fun stopRecording(view: View) {
        Log.d("tiempo6=", "$mRecorder.")


        try {
            mRecorder?.apply {

                Handler(Looper.getMainLooper()).postDelayed({
                    stop()
                    release()
                    view.grabadorMic.setAnimation("microfono_off.json")
                    Toast.makeText(requireContext(), "Grabacion finalizada", Toast.LENGTH_LONG)
                        .show()
                    //   view.linearReproducir.visibility = View.VISIBLE
                }, 250)

            }

            timeSwapBuff += timeInMilliseconds;
            customHandler.removeCallbacks(updateTimerThread);
            mRecorder = null

        } catch (e: IOException) {
            Log.e("mRecorder", "$e")
        }


    }


    private fun listenerRecycler(): RecyclerView.OnScrollListener {
        val scrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val visibleItemCount = recyclerView.childCount
                val totalItemCount = recyclerView.layoutManager!!.itemCount
                val firstVisibleItemPosition = (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

                // Si el último elemento visible es el mismo que el último elemento en el adaptador,
                // entonces el usuario ha llegado al final de la lista y se deben cargar más datos.
                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                    // Cargar más datos aquí
                }
            }
        }
        return scrollListener
    }


    override fun onDestroy() {
        super.onDestroy()
        /*if (loading.isVisible){
            loading.visibility = View.GONE
        }
      */
        val preference: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        preference.edit {
            this.putInt("lastPosition", mLastPosition)
            Log.d("lastPosition1", "$mLastPosition")
            this.apply()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        Log.d(
            "Queryy3",
            "iniciando"
        )
//vamos a implementar IA

        Log.d("listenerrrr10", "dasdasd")
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_one, container, false)
        mReciclerView = mView.findViewById(R.id.reciclerViewEventos)

        myContext = context!!.applicationContext
        vista = View(context)
        mlinearLayoutManager = LinearLayoutManager(context)
        mReciclerView.layoutManager = this.mlinearLayoutManager

        return mView
    }

    fun mostrarBottomSheets() {
        if (myVariable) {
            Log.d("myListener", "**************")
        }
    }

    override fun onStart() {
        super.onStart()
        adapterPost()

    }


    override fun onResume() {
        super.onResume()
        mostrarBottomSheets()
        Log.d("REstaurando2", "ssss")
        //adapterPost()

        Log.d("posicionnn2","$posicion")
    }
    var posicion=0
    override fun onPause() {
        super.onPause()

       /* posicion=mlinearLayoutManager.findLastVisibleItemPosition()*/
       // Log.d("posicionnn","$posicion")

    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("enPausaa2", "destruyendo")

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

    private lateinit var globalRunnable: java.lang.Runnable

    fun adapterPost() {


        val ciudad = PreferenciasGenerales(context!!).getpreferenceCiudadUsuario()
        mPostProvider = PostProvider()
        var query: Query = mPostProvider.getPostByCiudad(ciudad!!)
        var options: FirestoreRecyclerOptions<Post?> =
            FirestoreRecyclerOptions.Builder<Post>().setQuery(
                query,
                Post::class.java
            ).build()
        mPostAdapter = PostsAdapter(options, context)
        Log.d("removidoo", "$isAdded")
        if (isAdded) {
            if (mPostAdapter != null) {
                reciclerViewEventos.adapter = this.mPostAdapter
                //reciclerViewEventos.addOnScrollListener(listenerRecycler())
                mPostAdapter?.notifyDataSetChanged()
                mPostAdapter?.startListening()
                // loading.visibility = View.GONE

            }
        }
        if (isAdded) {
            swipeRefreshLayout.isRefreshing = false
        }


    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onStop() {
        super.onStop()

        val preference: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        preference.edit {
            this.putInt("lastPosition", mLastPosition)
            Log.d("lastPosition1", "$mLastPosition")
            this.apply()
        }


        var x = mlinearLayoutManager.findLastVisibleItemPosition()
        Log.d("buscados", "$x")
        mPostAdapter?.stopListening()
    }


    private fun guardarDescripcion(
        url: String,
        v: View,
        type: String,
        nameFile: String,
        texto: String
    ) {
        var tipo = ""

        if (type == "") {
            tipo = "texto"
        } else {
            tipo = type
        }
        // if (PreferenciasGenerales(context!!).getpreferenceGuiaSoyModerador() == "true") {
        val post = Post()
        mPostProvider = PostProvider()
        post.imagen = url
        post.type = tipo
        post.nota = v.time.text.toString()
        post.descripcion = texto
        post.uid = mAuth.currentUser!!.uid
        var fechalocal = Date().time
        post.fecha = fechalocal
        post.adminArea =PreferenciasGenerales(context!!).getpreferenceCiudadUsuario()
        /*post.lat = UbicacionesUtil.getLatLng().latitude.toString()
        post.lng = UbicacionesUtil.getLatLng().longitude.toString()*/
        post.nameFile = nameFile
        post.nreporteNegativo = "0"
        mPostProvider.save(post).addOnCompleteListener {
            if (it.isSuccessful) {
                sendNotificationEventosGrupo(v.txtDescripcion.text.toString())
                Handler(Looper.myLooper()!!).postDelayed({
                    mAlertDialog.dismiss()
                }, 3000)
            } else {

            }
        }


    }

    private fun getUsuario(v: View) {
        Log.d("getUser", "v")
        mUserProviders.getUsuario(mAuth.uid.toString()).addOnSuccessListener {
            if (it.exists()) {
                if (it.contains("nombre")) {
                    v.nombrePost.setText("${it.get("nombre")}")

                }
                if (it.contains("photo")) {
                    var imagenPerfil: String = it.get("photo").toString()
                    if (it.get("photo").toString() == "") {
                        Picasso.get().load(R.drawable.person_24).error(R.drawable.person_24).into(
                            v.photoPerfilPosts
                        )
                    } else {
                        Log.d("getUserfoto", "v")
                        Picasso.get().load(imagenPerfil).error(R.drawable.person_24)
                            .into(v.photoPerfilPosts)
                    }
                }

                v.ciudad.setText(PreferenciasGenerales(context!!).getpreferenceCiudadUsuario())


            }
        }
    }

    private fun startPix() {
        Pix.start(this, mOptiones);
    }

    private fun alertDialogPost() {
        if (PreferenciasGenerales(context!!).getpreferenceGuiaPoster() == "true") {


            val alertDialog: AlertDialog
            val builder: AlertDialog.Builder =
                AlertDialog.Builder(requireContext())
            // Pasar nulo como vista principal porque va en el diseño del diálogo
            val v: View = LayoutInflater.from(context).inflate(R.layout.cuadro_post, null)

            builder.setView(v)
            getUsuario(v)
            builder.setCancelable(true)
            alertDialog = builder.create()
            alertDialog.show()
            val adRequest: AdRequest = AdRequest.Builder().build()
            val bannerventanaConsulta = alertDialog!!.findViewById<AdView>(R.id.cuadro_postAds)
            bannerventanaConsulta!!.loadAd(adRequest)


            mView = v
            v.lottieAnimationCamera.setOnClickListener {

                startPix()
                /*v.lottieAnimationCamera.visibility = View.GONE
                vista = v
                selecionarOpciones(GALLERY_REQUEST_CODE)*/
            }
            v.verImagen.setOnClickListener {
                mView = v
                startPix()
            }
            v.eliminarAudio.setOnClickListener {
                mediaPlayer?.reset()
                mFileNameRecord = null
                v.lottieAnimationCamera.visibility = View.VISIBLE
                v.cardReproducir.visibility = View.GONE
                v.eliminarAudio.visibility = View.GONE
                v.cardtimeRecording.visibility = View.GONE
                //v.timeRecording.text="00:00"
                startHTime = 0L
                timeInMilliseconds = 0L
                timeSwapBuff = 0L
                updatedTime = 0L
                v.txtDescripcion.visibility = View.VISIBLE
                //v.linearReproducir.visibility = View.GONE
            }


            v.grabadorMic.setOnTouchListener { view, motionEvent ->
                //  v.linearReproducir.visibility = View.GONE
                v.txtDescripcion.visibility = View.GONE
                val permission = android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                val permission2 = android.Manifest.permission.RECORD_AUDIO


                val permissionCheck = ContextCompat.checkSelfPermission(context!!, permission)
                val permissionCheck2 = ContextCompat.checkSelfPermission(context!!, permission2)

                if (permissionCheck != PackageManager.PERMISSION_GRANTED && permissionCheck2 != PackageManager.PERMISSION_GRANTED) {
                    // Los permisos de uso del micrófono han sido rechazados
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            activity!!,
                            permission
                        ) && ActivityCompat.shouldShowRequestPermissionRationale(
                            activity!!,
                            permission2
                        )
                    ) {
                        // Mostrar una explicación al usuario sobre por qué necesitas el permiso
                        Toast.makeText(
                            context,
                            "Habilíte permisos de Para usar micrófono.",
                            Toast.LENGTH_LONG
                        )
                            .show()
                        // Direccionar al usuario a las configuraciones para habilitar los permisos
                        val intent = Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", "com.jomeva.driving", null)
                        )
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)

                    } else {
                        ActivityCompat.requestPermissions(
                            requireActivity()!!,
                            arrayOf(
                                permission,
                                permission2
                            ),
                            1000
                        )
                    }
                } else {
                    grabar(v, motionEvent)
                    // Tienes los permisos necesarios para usar el micrófono
                }


                /*if (ContextCompat.checkSelfPermission(
                        context!!,
                        permission
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        context!!,
                        permission2
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    Log.d("microoo2","permisos otorgados")
                    //queda pendiente el bloque de abajo cuuando los permisos fueron negados
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(activity!!, permission) || !ActivityCompat.shouldShowRequestPermissionRationale(activity!!, permission2)) {
                        Log.d("microoo3","permisos otorgados")
                        Toast.makeText(context, "Habilíte permisos de Para usar micrófono.", Toast.LENGTH_LONG)
                            .show()
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package", context!!.packageName, null)
                        intent.data = uri
                        startActivityForResult(intent, 200)
                    } else {
                        ActivityCompat.requestPermissions(
                            activity!!,
                            arrayOf(
                                permission,
                                permission2
                            ),
                            200
                        )
                    }

      *//*
              Toast.makeText(
                  context!!,
                  "Por favor concede permisos para acceder al micrófono, ve a configuraciones/aplicaciones/Draivin/permisos",
                  Toast.LENGTH_LONG
              ).show()

              ActivityCompat.requestPermissions(
                  requireActivity()!!,
                  arrayOf(
                      permission,
                      permission2
                  ),
                  1000
              )*//*
          } else {
              Log.d("microoo","permisos otorgados")
//si los permisos fueron otorgados
              grabar(v,motionEvent)
          }*/
                return@setOnTouchListener true
            }

            v.seekReproduccion.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {

                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {
                    // you can probably leave this empty
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                    // you can probably leave this empty
                }
            })

            v.txtDescripcion.addTextChangedListener {
                val text = v.txtDescripcion.text.toString()
// Expresión regular para buscar URLs
                val pattern = Patterns.WEB_URL
// Crear un objeto Matcher para buscar URLs en el texto
                val matcher = pattern.matcher(text)
// Recorrer el texto en busca de URLs
                while (matcher.find()) {
                    // Obtener la URL encontrada


                    val url = matcher.group()
                    hacerAlgoConWebText(url, v)
                    // Hacer algo con la URL encontrada
                    Log.d("URLss", url)
                }
            }
            v.btnpublicar.setOnClickListener {
                mAlertDialog = alertDialog
                if (mImageFile == null && mFileNameRecord == null) {
                    if (v.txtDescripcion.text.toString().isEmpty()) {
                        Toast.makeText(
                            requireContext(),
                            " Está vacío el campo de texto",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    } else {
                        if (isUrl) {
                            alertDialog.dismiss()
                            mensajePersonalizado()
                            guardarDescripcion("", v, "", "", textUrl)
                            isUrl=!isUrl
                        } else {
                            alertDialog.dismiss()
                            mensajePersonalizado()
                            guardarDescripcion("", v, "", "", v.txtDescripcion.text.toString())
                        }


                    }
                } else if (mImageFile != null) {
                    Toast.makeText(requireContext(), " guardar imagen", Toast.LENGTH_SHORT).show()
                    alertDialog.dismiss()
                    mensajePersonalizado()
                    saveImage(v)


                } else if (mFileNameRecord != null) {
                    Toast.makeText(requireContext(), " guardar solo audio", Toast.LENGTH_SHORT)
                        .show()
                    alertDialog.dismiss()
                    mensajePersonalizado()
                    saveAudio(v)
                    //
                }
            }

            v!!.frameVideoPost.setOnClickListener {
                if (!v!!.videoViewPost.isPlaying) {
                    v!!.btnPlayPost.visibility = View.GONE
                    v!!.imageVideoPost.visibility = View.GONE
                    v!!.videoViewPost.start()
                } else {
                    v!!.btnPlayPost.visibility = View.VISIBLE
                    v!!.imageVideoPost.visibility = View.VISIBLE
                    v!!.videoViewPost.pause()
                }
            }


            v.cerrarPoster.setOnClickListener {
                alertDialog.dismiss()
            }
        } else {
            visitaGuiadaPoster()
        }
    }

    var isUrl = false
    var textUrl = ""
    private fun hacerAlgoConWebText(url: String, v: View) {
        v.txtDescripcion.visibility = View.VISIBLE
        v.verImagen.visibility = View.VISIBLE
        val thread = Thread {
            if (urlExists(url)) {
                // eliminar imagen y audios
                activity!!.runOnUiThread {

                    mImageFile = null
                    mediaPlayer!!.reset()
                    v.linearImagenAudio.visibility = View.GONE
                }
                isUrl = true
                textUrl = url

                val doc = Jsoup.connect(url).get()
                // Extraer el título de la página
                Log.d("docc", "$doc")


                val title = doc.title()
                if (title != null) {
                    activity!!.runOnUiThread {
                        // Mostrar el título en un TextView, por ejemplo
                        //descripcion.text = title

                        val mitextoU = SpannableString(title)
                        mitextoU.setSpan(UnderlineSpan(), 0, mitextoU.length, 0)

                        v.txtDescripcion.setText(mitextoU)

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
                    activity!!.runOnUiThread {
                        v.verImagen.setImageBitmap(bitmap)
                    }
                }

            }

        }
        thread.start()
    }

    val client = OkHttpClient()
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

    fun grabar(v: View, motionEvent: MotionEvent) {
        try {


            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                mediaPlayer!!.reset()
                mFileNameRecord = null
                startHTime = 0L
                timeInMilliseconds = 0L
                timeSwapBuff = 0L
                updatedTime = 0L

                v.cardtimeRecording.visibility = View.VISIBLE
                v.cardReproducir.visibility = View.GONE
                v.grabadorMic.setAnimation("microfono.json")
                v.grabadorMic.playAnimation()
                grabando(v)

            } else if (motionEvent.action == MotionEvent.ACTION_UP) {
                stopRecording(v)
                v.cardReproducir.visibility = View.VISIBLE
                v.eliminarAudio.visibility = View.VISIBLE
                v.cardtimeRecording.visibility = View.GONE
                v.time.text = v.timeRecording.text
                controlReproducir(v)

                v.lottieAnimationCamera.visibility = View.GONE
            }
        } catch (e: Exception) {
            Toast.makeText(context, "No se han aceptado los permisos", Toast.LENGTH_LONG).show()
            val intent = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", "com.jomeva.driving", null)
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)

        }
    }


    private fun saveAudio(v: View) {
        val type = "audio"
        val nombre = UUID.randomUUID().toString()
        val thread = Thread {
            mImageProvider.saveAudio(context!!, mFileNameRecord!!, nombre)
                .addOnCompleteListener { it ->
                    if (it.isSuccessful) {
                        mFileNameRecord = null
                        val nameFile = mImageProvider.getStorage().name
                        mImageProvider.getStorage().downloadUrl.addOnSuccessListener { uriPerfil ->
                            val urlPost: String = uriPerfil.toString()
                            //crearPost()
                            guardarDescripcion(urlPost, v, type, nameFile,"")
                            Log.d("url-", "->$urlPost")
                        }
                    } else {
                        //No se pudo guardar
                    }
                }
        }
        thread.start()

        //alertDialog.dismiss()

    }

    fun verVideo() {
        //if (ExtensionFile.isVideoFile(mImageFile))
    }

    private fun mensajePersonalizado() {
        val alertDialog: AlertDialog
        val builder: AlertDialog.Builder =
            AlertDialog.Builder(requireContext()!!)
        val v: View =
            LayoutInflater.from(requireContext()).inflate(R.layout.cuadro_mensaje_check, null)
        builder.setView(v)
        val adRequest: AdRequest = AdRequest.Builder().build()
        val bannerventanaConsulta = v!!.findViewById<AdView>(R.id.cuadro_mensaje_checkAds)
        bannerventanaConsulta!!.loadAd(adRequest)

        builder.setCancelable(false)
        alertDialog = builder.create()
        alertDialog.show()

        malertDialog = alertDialog
        v.cerrarPosterCheck.setOnClickListener {
            alertDialog.dismiss()
        }


    }

    private fun saveImage(v: View) {
        var nombre = ""
        var type = ""
        if (ExtensionFile.isImageFile(mImageFile?.absolutePath)) {
            type = "imagen"
            nombre = "${Date().time}.jpg"
            val thread = Thread {
                mImageProvider.save(requireContext(), mImageFile!!, nombre)
                    .addOnCompleteListener { it ->
                        if (it.isSuccessful) {
                            mImageFile = null
                            val nameFile = mImageProvider.getStorage().name
                            mImageProvider.getStorage().downloadUrl.addOnSuccessListener { uriPerfil ->
                                val urlPost: String = uriPerfil.toString()
                                //crearPost()
                                guardarDescripcion(urlPost, v, type, nameFile,v.txtDescripcion.text.toString())

                            }
                        } else {
                            //No se pudo guardar
                        }
                    }
            }
            thread.start()
            // mAlertDialog.dismiss()
        } else if (ExtensionFile.isVideoFile(mImageFile?.absolutePath)) {

        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == 100) {
            mReturnValues = data?.getStringArrayListExtra(Pix.IMAGE_RESULTS)!!
            mImageFile = File(mReturnValues[0])
            mView.verImagen.visibility = View.VISIBLE
            mView.verImagen.scaleType = ImageView.ScaleType.FIT_CENTER

            if (ExtensionFile.isVideoFile(mImageFile?.absolutePath)) {
                mView.verImagen.visibility = View.GONE
                mView.frameVideoPost.visibility = View.VISIBLE
                val uri = Uri.fromFile(File(mImageFile?.absolutePath))
                mView.videoViewPost.setVideoURI(uri)

                var mediaController = MediaController(context)
                mView.videoViewPost.setMediaController(mediaController)
                mediaController.setAnchorView(mView.videoViewPost)

                Log.d("mostrandoVideo", "${uri}")

            } else if (ExtensionFile.isImageFile(mImageFile?.absolutePath)) {
                mView.verImagen.visibility = View.VISIBLE
                mView.frameVideoPost.visibility = View.GONE


                mView.verImagen.setImageBitmap(BitmapFactory.decodeFile(mImageFile?.absolutePath))

            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Pix.start(this, mOptiones)
            } else {

                val intent = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", "com.jomeva.driving", null)
                )
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)



                Toast.makeText(
                    requireContext(),
                    "Por favor concede permisos para acceder a la cámara",
                    Toast.LENGTH_LONG
                ).show()
            }

        }

    }

    override fun cambiandoCiudadListener(view: View, post: Post, isImagen: Boolean) {
        mPostProvider = PostProvider()

        val bottomSheetDialog = BottomSheetDialog(view.context)
        val bottomsheetBehavior: BottomSheetBehavior<View>
        val bottomSheetView = LayoutInflater.from(view.context)
            .inflate(
                R.layout.cuadro_comentarios_eventos_transparentes,
                bottom_sheet_comentario_transparente
            )
        bottomSheetDialog.setContentView(bottomSheetView)
//behavior
        bottomsheetBehavior = BottomSheetBehavior.from(bottomSheetView.parent as View)
        // set to behavior
        bottomsheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        val layout =
            bottomSheetDialog.findViewById<CoordinatorLayout>(R.id.bottom_sheet_comentario_transparente)
        assert(layout != null)
        layout!!.minimumHeight = Resources.getSystem().displayMetrics.heightPixels


        bottomSheetDialog.show()
        val adRequest: AdRequest = AdRequest.Builder().build()
        val bannerventanaConsulta =
            layout!!.findViewById<AdView>(R.id.cuadro_comentario_eventos_transparenteAds)
        bannerventanaConsulta!!.loadAd(adRequest)
//////////////////////////////////////////////////////////////////////////
        val mEmojIconActions =
            EmojIconActions(
                view.context,
                bottomSheetView,
                bottomSheetView.comentarioBottomSheet,
                bottomSheetView.imagenEmoji
            )
        mEmojIconActions.ShowEmojIcon()
/////////////////////////////////////////////////////////////////////
        mPostProvider.getPostbyId(post.idPost).addOnSuccessListener {
            if (it.contains("imagen") && it.contains("type")) {
                val imagen = it.getString("imagen").toString()
                val tipo = it.getString("type").toString()

                if (imagen != "") {
                    if (tipo == "audio") {

                        layout.imagepostCardBottomSheets.visibility = View.GONE
                        layout.cardReproducirCuadro.visibility = View.VISIBLE
                        reproducirAudioPost(layout, post, bottomSheetDialog)

                    } else if (tipo == "imagen") {
                        Log.d("aaaaaaa2", "$tipo")
                        Picasso.get().load(imagen).into(layout.imagepostCardBottomSheets)
                        layout.imagepostCardBottomSheets.visibility = View.VISIBLE
                    }

                } else {

                    val params = CoordinatorLayout.LayoutParams(
                        CoordinatorLayout.LayoutParams.MATCH_PARENT,
                        CoordinatorLayout.LayoutParams.WRAP_CONTENT
                    )
                    layout.frameLa.layoutParams = params
                    layout.descripcionPost.textSize = 23f

                    Handler(Looper.getMainLooper()).postDelayed({
                        val alturaLinear = layout.frameLa.height + 20

                        val paramsLinear = CoordinatorLayout.LayoutParams(
                            CoordinatorLayout.LayoutParams.MATCH_PARENT,
                            CoordinatorLayout.LayoutParams.WRAP_CONTENT
                        )
                        layout.linearRecycler.layoutParams = paramsLinear

                        layout.linearRecycler.setPadding(10, alturaLinear, 50, 95)
                    }, 200)
                }
            }
            if (it.contains("descripcion")) {
                val descripcion = it.getString("descripcion").toString()
                layout.descripcionPost.text = descripcion
            }
        }

        mReciclerViewComentario = layout.findViewById(R.id.reciclerViewComentario)
        mlinearLayoutManagerComentario = LinearLayoutManager(context)
        mReciclerViewComentario.layoutManager = this.mlinearLayoutManagerComentario
        adapterComentario(mReciclerViewComentario, post.idPost, view.context)
        ////////////////////////////////////////////////////////////////////////////////////

        layout.publicarComentario.setOnClickListener {
            val auth = FirebaseAuth.getInstance()
            val users = auth.currentUser
            if (users != null) {
                val comment = layout.comentarioBottomSheet.text.toString()
                crearComentario(comment, post)
                layout.comentarioBottomSheet.setText("")
            } else {
                mensajePersonalizadoLogin(view.context)
            }

        }
        layout.comentarioBottomSheet.addTextChangedListener {

            if (layout.comentarioBottomSheet.text.toString() != "") {
                layout.publicarComentario.visibility = View.VISIBLE
            } else {
                layout.publicarComentario.visibility = View.INVISIBLE

            }
        }


    }

    private fun mensajePersonalizadoLogin(context: Context) {

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

    fun reproducirAudioPost(v: View, post: Post, bottomSheetDialog: BottomSheetDialog) {
        //
        val mediap = MediaPlayer()

        if (banderaReproduccion == "stop") {
            v.playCuadro.background = v.context!!.getDrawable(R.drawable.btn_pause)
            banderaReproduccion = "play"
            try {

                mediap?.reset()
                mediap?.setDataSource(post.imagen)
                mediap?.prepare()
                mediaPlayer = mediap
                incializarSeekBarBottom(v, mediap, bottomSheetDialog)
                Log.d("mediaplayerrr1", "ID: ${mediap?.audioSessionId}")
            } catch (e: IOException) {
                Log.e("LOG_TAG", "$e prepare() failed")
            }
            mediap?.start()
        }
        v.playCuadro.setOnClickListener {
            when (banderaReproduccion) {
                "stop" -> {
                    //reproducir 1 vez
                    v.playCuadro.background = v.context!!.getDrawable(R.drawable.btn_pause)
                    banderaReproduccion = "play"
                    try {

                        mediap?.reset()
                        mediap?.setDataSource(post.imagen)
                        mediap?.prepare()
                        mediaPlayer = mediap
                        incializarSeekBarBottom(v, mediap, bottomSheetDialog)
                        Log.d("mediaplayerrr1", "ID: ${mediap?.audioSessionId}")
                    } catch (e: IOException) {
                        Log.e("LOG_TAG", "$e prepare() failed")
                    }
                    mediap?.start()


                }
                "play" -> {
                    //pausar
                    v.playCuadro.background = v.context!!.getDrawable(R.drawable.btn_play)
                    //v.btnPlayAudio.background = resources.getDrawable(R.drawable.btn_pause, null)
                    if (mediap !== null) {
                        mediap?.pause()
                        mediaPlayer = mediap
                    }
                    banderaReproduccion = "pause"
                    Log.d("mediaplayerrr2", "ID: ${mediap?.audioSessionId}")

                }
                "pause" -> {

                    v.playCuadro.background = v.context!!.getDrawable(R.drawable.btn_pause)
                    mediap?.start()
                    mediaPlayer = mediap
                    incializarSeekBarBottom(v, mediap, bottomSheetDialog)
                    Log.d("mediaplayerrr6", "ID: ${mediap?.audioSessionId}")
                    banderaReproduccion = "play"
                }
            }


        }
        v.seekReproduccionCuadro.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                Log.d(
                    "mediaplayerrr11",
                    "${progress / 1000} ->${v.seekReproduccionCuadro.max / 1000} "
                )
                if (progress / 1000 == v.seekReproduccionCuadro.max / 1000) {
                    // mediaPlayer?.stop()
                    v.playCuadro.background = v.context!!.getDrawable(R.drawable.btn_play)
                    v.seekReproduccionCuadro.progress = 0
                    v.seekReproduccionCuadro.max = 0
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

    fun incializarSeekBarBottom(
        v: View,
        mediap: MediaPlayer,
        bottomSheetDialog: BottomSheetDialog
    ) {
        if (mediaPlayer != null) {
            v.seekReproduccionCuadro.max = mediaPlayer!!.duration
        }

        val handler = Handler()
        handler.postDelayed(object : Runnable {

            override fun run() {
                try {
                    if (!bottomSheetDialog.isShowing) {
                        Log.d("Ocultando", "-------------")
                        v.seekReproduccionCuadro.progress = 0
                        v.seekReproduccionCuadro.max = 0
                        mediaPlayer!!.reset()
                        mediaPlayer = null
                        Log.d("mediaplayerrr16", "${mediaPlayer!!.currentPosition} ")
                        handler.removeCallbacks(this)
                    }
                    v.seekReproduccionCuadro.progress = mediaPlayer!!.currentPosition
                    handler.postDelayed(this, 100)
                    //if (mediaPlayer!!.currentPosition/1000>0 ) {
                    val tiempo =
                        (mediaPlayer!!.duration / 1000) - (mediaPlayer!!.currentPosition / 1000).toLong()
                    val time = RelativeTime.RelativeTime.timeAudio(tiempo)
                    Log.d("mediaplayerrr15", "${mediaPlayer!!.currentPosition} ")
                    v.timeCuadro.text = time
                    if (mediaPlayer!!.currentPosition / 100 == mediaPlayer!!.duration / 100) {
                        v.seekReproduccionCuadro.progress = 0
                        v.seekReproduccionCuadro.max = 0
                        Log.d("mediaplayerrr16", "${mediaPlayer!!.currentPosition} ")
                        handler.removeCallbacks(this)
                    }
                    // }
                } catch (e: Exception) {
                    v.seekReproduccionCuadro.progress = 0
                }
            }
        }, 0)

    }


    override fun consultarPropinas(view: View, propinas: Propinas) {
        TODO("Not yet implemented")
    }

    override fun CrearPrimerReporte(view: View) {
        TODO("Not yet implemented")
    }




    private fun crearComentario(comment: String, post: Post) {
        println("Clic en publicar")
        mAuth = FirebaseAuth.getInstance()
        mComentarios = Comentarios()
        var fechalocal = Date().time
        mComentarios.comentario = comment
        mComentarios.uid = mAuth.currentUser!!.uid
        mComentarios.idPost = post.idPost
        mComentarios.uidPost = post.uid
        mComentarios.fecha = fechalocal
        mComentarioProvider.crearComentario(mComentarios).addOnCompleteListener {
            if (it.isSuccessful) {
                sendNotification(comment, post)
                Log.d("cargarComent ", "->subido")
            } else {
                Log.d("cargarComent ", "->negado")
            }
        }
    }

    private fun adapterComentario(
        mReciclerViewComentario: RecyclerView,
        idPost: String,
        context: Context
    ) {
        mComentarioProvider = ComentarioProvider()

        var query: Query = mComentarioProvider.getComentariobyPost(idPost)

        var options: FirestoreRecyclerOptions<Comentarios?> =
            FirestoreRecyclerOptions.Builder<Comentarios>().setQuery(
                query,
                Comentarios::class.java
            ).build()

        mComentarioAdapter = ComentarioAdapter(options, context)
        mReciclerViewComentario.adapter = this.mComentarioAdapter
        mComentarioAdapter.startListening()
    }

    private fun sendNotificationEventosGrupo(texto: String) {
        if (PreferenciasGenerales(context!!).getpreferenceCiudadUsuario() != "") {
            var lista = arrayListOf<String>()
            mTokenProvider = TokenProvider()
            mUserProviders.getUserByCiudad(PreferenciasGenerales(context!!).getpreferenceCiudadUsuario())
                .get().addOnSuccessListener {

                for (i in it) {
                    if (i.get("uid").toString() != mAuth.uid) {
                        mTokenProvider.getToken(i.get("uid").toString()).addOnSuccessListener {
                            mNotificationProviders = NotificationProvider()
                            if (it.exists()) {
                                if (it.contains("token")) {
                                    val token = it.get("token").toString()
                                    //lista.add(token)

                                    enviarNotificacionesByCiudad(token, texto)

                                }
                            } else {

                            }
                        }
                    }


                    // lista.add(i.get("uid").toString())
                    // Log.d("usuariosss","${i.get("uid")}")
                }


            }
        }


    }

    fun enviarNotificacionesByCiudad(token: String, texto: String) {

        var map = mutableMapOf("title" to "Evento reportado en tu ciudad")
        map.put("body", "$texto")
        map.put("tipo", "Evento")
        map.put("user1", mAuth.currentUser!!.uid)

        var body = FCMBody(token, "high", "4500s", map)

        mNotificationProviders.sendNotification(body)!!
            // .enqueue(object :retrofit2.Callback<FCMresponse?>
            .enqueue(object : retrofit2.Callback<FCMResponse?> {
                override fun onResponse(
                    call: Call<FCMResponse?>?,
                    response: Response<FCMResponse?>
                ) {
                    if (response.body() != null) {

                        if (response.body()!!.success == 1) {

                        } else {

                        }
                    } else {


                    }
                }

                override fun onFailure(call: Call<FCMResponse?>?, t: Throwable?) {}
            })


    }

    private fun sendNotification(comment: String, post: Post) {
        mTokenProvider = TokenProvider()
        if (post.uid == null) {
            Log.d("usuario->", "${post.uid}")
            return

        }

        mTokenProvider.getToken(post.uid).addOnSuccessListener {
            mNotificationProviders = NotificationProvider()
            if (it.exists()) {

                if (it.contains("token")) {

                    val token = it.get("token").toString()

                    var map = mutableMapOf("title" to "Nuevo comentario Post")
                    map.put("body", comment)
                    map.put("tipo", "comentarioPost")
                    map.put("user1", mAuth.currentUser!!.uid)
                    map.put("user2", post.uid)
                    map.put("idPost", post.idPost)
                    Log.d("mapassss", "${post.idPost}")
                    Log.d("yddhdsbbs->", "$token ${map["tipo"]}")
                    var body = FCMBody(token, "high", "4500s", map)

                    mNotificationProviders.sendNotification(body)!!
                        // .enqueue(object :retrofit2.Callback<FCMresponse?>
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
            } else {

            }
        }
    }

    override fun onFragmentAdded() {

        Log.d("addressList8", "s ")
    }


}