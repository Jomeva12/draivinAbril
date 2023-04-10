package com.jomeva.driving.activities.activities

import android.content.*
import android.content.res.Resources
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
//import android.preference.PreferenceManager
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.*
import android.widget.*
import android.widget.Toast.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jomeva.driving.R
import com.jomeva.driving.activities.adapters.AdapterUtilCiudad
import com.jomeva.driving.activities.adapters.MypageAdapter
import com.jomeva.driving.activities.modelos.*
import com.jomeva.driving.activities.providers.*
import com.jomeva.driving.activities.util.*
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetView
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.cardview_chats.*
import kotlinx.android.synthetic.main.cardview_post.view.*
import kotlinx.android.synthetic.main.cuadro_actualizar_ciudad.*
import kotlinx.android.synthetic.main.cuadro_actualizar_ciudad.view.*
import kotlinx.android.synthetic.main.cuadro_mi_ciudad.*
import kotlinx.android.synthetic.main.cuadro_mi_ciudad.view.*
import kotlinx.android.synthetic.main.cuadro_peticion_moderador.view.*
import kotlinx.android.synthetic.main.cuadro_peticion_moderador.view.cerrarPoster
import kotlinx.android.synthetic.main.cuadro_peticion_moderador.view.ciudad
import kotlinx.android.synthetic.main.cuadro_peticion_moderador.view.txtDescripcion
import kotlinx.android.synthetic.main.cuadro_post.view.*
import kotlinx.android.synthetic.main.cuadro_post_sugerencias.view.*
import kotlinx.android.synthetic.main.cuadro_post_sugerencias.view.photoPerfilPosts
import kotlinx.android.synthetic.main.cuadro_preguntar.view.*
import kotlinx.android.synthetic.main.fragment_four.*
import kotlinx.android.synthetic.main.fragment_moderador2.*
import kotlinx.android.synthetic.main.fragment_one.*
import kotlinx.android.synthetic.main.fragment_two.*
import kotlinx.android.synthetic.main.primer_usuario.view.*
import kotlinx.coroutines.*
import java.util.*


class HomeActivity : AppCompatActivity() {
    lateinit var mPostProvider: PostProvider
    private lateinit var mtokenProvider: TokenProvider
    private lateinit var mAuth: FirebaseAuth
    private lateinit var lista: ListView
    private lateinit var mPicoYPlacaProvider: PicoYPlacaProvider
    private lateinit var mView: View
    private lateinit var mMessagesProvider: MessagesProvider
    private lateinit var mUserProviders: UserProviders
    private lateinit var mFragmentOne: FragmentOne
    private lateinit var ciudadUser: String
    private lateinit var mModeradoresProvider: ModeradoresProvider
    private lateinit var mModeradores: Moderadores
    private lateinit var mExtraCiudad: String
    var tiempoDeUso: Long = 0
    var tiempoDeUsoInicial: Long = 0
    lateinit var mPublicacionPorAprobarProvider: PublicacionPorAprobarProvider
    lateinit var mPeticionModeradorProvider: PeticionModeradorProvider
    private val VERSION_APP = "1.011"
    var bandera = true

    //conexion a internet
    lateinit var mNetworkChangeListener: NetWorkListenerKotlin
    private lateinit var malertDialog: AlertDialog
    lateinit var listaTitulos: ArrayList<UtilCiudad>
    private lateinit var mUtilCiudad: UtilCiudad
    lateinit var mAdapterUtilCiudad: AdapterUtilCiudad
    lateinit var mlinearLayoutManagerUtil: LinearLayoutManager


    //interstitial
    private var interstitial: InterstitialAd? = null

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        mNetworkChangeListener = NetWorkListenerKotlin()
        mModeradoresProvider = ModeradoresProvider()
        mModeradores = Moderadores()
        mMessagesProvider = MessagesProvider()
        mAuth = FirebaseAuth.getInstance()
        mtokenProvider = TokenProvider()
        mPublicacionPorAprobarProvider = PublicacionPorAprobarProvider()
        mPicoYPlacaProvider = PicoYPlacaProvider()
        listaTitulos = ArrayList()
        mPostProvider = PostProvider()
        setSupportActionBar(toolbar)

        val fragmentAdapter = MypageAdapter(supportFragmentManager)
        viewPager.adapter = fragmentAdapter

        frameBotonModerador.setOnClickListener {
            val intent = Intent(this, HomeModerador::class.java)
            startActivity(intent)
        }

        mFragmentOne = FragmentOne()
        lista = ListView(this)
        mView = View(this)

        mPeticionModeradorProvider = PeticionModeradorProvider()

        tabLayout.setupWithViewPager(viewPager)
        mUserProviders = UserProviders()


        getCiudad()
        menuCardHome.setOnClickListener {
            if (PreferenciasGenerales(applicationContext).getpreferenceGuiaMenuHome() == "true") {
                metodoMenu(it)
            } else {
                visitaGuiadaMenu(it)

            }


        }

        val user = mAuth.currentUser
        if (user != null) {
            crearToken(mAuth.currentUser!!.uid)
            if (PreferenciasGenerales(this).getpreferenceCiudadUsuario() == "") {
                //mostrar ciudad
            } else {
                val users = Usuarios()
                users.uid = mAuth.uid.toString()
                users.ciudad = PreferenciasGenerales(this).getpreferenceCiudadUsuario()
                mUserProviders.upDatePerfil(users).addOnCompleteListener {
                    if (it.isSuccessful) {

                    }

                }
            }

            //tituloHome()

        }

    }

    private fun getCiudad() {
        val user = mAuth.currentUser
        if (user != null) {
            mUserProviders.getUsuario(mAuth.uid.toString()).addOnSuccessListener {
                if (it.exists()) {
                    if (it.contains("ciudad")) {
                        val ciudad = it.getString("ciudad")
                        if (ciudad != PreferenciasGenerales(this!!).getpreferenceCiudadUsuario()) {
                            //actualizar como aparece en preferencias
                            val mUser = Usuarios()
                            mUser.ciudad =
                                PreferenciasGenerales(this!!).getpreferenceCiudadUsuario()
                            mUserProviders.upDateCiudad(mUser).addOnCompleteListener {
                                if (it.isSuccessful) {

                                }
                            }

                        }
                    }
                }
            }
        }

    }

    private fun initAds() {
        var adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            this,
            "ca-app-pub-1498245539124562/4388980187",
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    interstitial = interstitialAd
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    interstitial = null
                }
            })
    }

    private fun showAds() {
        interstitial?.show(this)
    }

    private fun initListeners() {
        interstitial?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
            }


            override fun onAdShowedFullScreenContent() {
                interstitial = null
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun metodoMenu(it: View) {
        val popup = PopupMenu(this, it)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.menu, popup.menu)

        /**/
        for (i in 0 until popup.menu.size()) {
            val item = popup.menu.getItem(i)
            val spanString = SpannableString(popup.menu.getItem(i).title.toString())
            spanString.setSpan(
                ForegroundColorSpan(getColor(R.color.white)),
                0,
                spanString.length,
                0
            ) //fix the color to white
            item.title = spanString
        }
        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.itemPerfil -> {
                    val user = mAuth.currentUser
                    if (user != null) {
                        initAds()
                        initListeners()
                        showAds()
                        var intent = Intent(this, PerfilActivity::class.java)
                        startActivity(intent)
                    } else {
                        //quiere ser moderador
                        mensajePersonalizadoLogin()
                    }



                    return@setOnMenuItemClickListener true
                }
                R.id.cambiarCiudad -> {
                    ActualizarCiudad()
                    return@setOnMenuItemClickListener true
                }

                R.id.sugerir -> {
                    val user = mAuth.currentUser
                    if (user != null) {
                        mensajeSugerencias()
                    } else {
                        //quiere ser moderador
                        mensajePersonalizadoLogin()
                    }
                    //abrir un card

                    return@setOnMenuItemClickListener true
                }
                R.id.politica -> {
                    var intent = Intent(this, Politica::class.java)
                    startActivity(intent)
                    return@setOnMenuItemClickListener true
                }
                R.id.login -> {
                    var intent = Intent(this, AuthActivity::class.java)
                    startActivity(intent)
                    return@setOnMenuItemClickListener true
                }
                R.id.configReport -> {

                    initAds()
                    initListeners()
                    showAds()

                    var intent = Intent(this, ConfigReportActivity::class.java)
                    startActivity(intent)
                    return@setOnMenuItemClickListener true
                }
                R.id.laCiudad -> {
                    abrirMiCiudad()
                    //ActualizarCiudad()
                    return@setOnMenuItemClickListener true

                }
                R.id.Compartir -> {

                    val share = Intent(Intent.ACTION_SEND)
                    share.type = "text/plain"
                    share.putExtra(
                        Intent.EXTRA_TEXT,
                        "https://play.google.com/store/apps/details?id=com.jomeva.driving"
                    )
                    share.setPackage("com.whatsapp")
                    startActivity(share)

                    return@setOnMenuItemClickListener true

                }

                R.id.quieroserModerador -> {
                    val user = mAuth.currentUser
                    if (user != null) {

                        mPeticionModeradorProvider.getPeticionesByCityIfExist(mAuth.uid.toString())
                            .get().addOnSuccessListener {
                                val tam = it.size()
                                if (tam > 0) {
                                    val lot = "question.json"
                                    val texto =
                                        "¿Ya tienes una solicitud, deseas eliminarla y aplicar nuevamente? "
                                    mensajePersonalizadoPregunta(lot, texto)
                                } else {
                                    alertDialogSolicitudModerador()
                                }
                            }
                    } else {
                        //quiere ser moderador
                        mensajePersonalizadoLogin()
                    }


                    //mostar mensaje de tiempo y editText de por qué

                    return@setOnMenuItemClickListener true

                }
                else -> {
                    return@setOnMenuItemClickListener true
                }
            }
        }
        popup.show()
    }


    //voy por acá

    private fun ActualizarCiudad() {

        val alertDialog: AlertDialog
        val builder: AlertDialog.Builder =
            AlertDialog.Builder(this!!)
        val v: View = LayoutInflater.from(this).inflate(R.layout.cuadro_actualizar_ciudad, null)
        builder.setView(v)
        builder.setCancelable(false)
        alertDialog = builder.create()
        //val ciudades = Ciudades.getCiudades()
        val adapter = ArrayAdapter<String>(
            this,
            R.layout.list_item_ciudad,
            PreferenciasGenerales.getCiudades()
        )
        v.listaCiudades.adapter = adapter
        alertDialog.show()

        val adRequest: AdRequest = AdRequest.Builder().build()
        val bannerventanaConsulta =
            alertDialog!!.findViewById<AdView>(R.id.cuadro_actualizar_ciudadAds)
        bannerventanaConsulta!!.loadAd(adRequest)

        v.listaCiudades.setOnItemClickListener { parent, view, position, id ->
            //actualizarPerfil(parent.getItemAtPosition(position), alertDialog, mView)
            PreferenciasGenerales(this!!).setpreferenceCiudadUsuario(
                parent.getItemAtPosition(
                    position
                ).toString()
            )
            alertDialog.dismiss()

            val user = mAuth.currentUser
            if (user != null) {
                val user = Usuarios()
                user.uid = mAuth.uid.toString()
                user.ciudad = parent.getItemAtPosition(
                    position
                ).toString()

                mUserProviders.upDateCiudad(user).addOnCompleteListener {
                    mModeradoresProvider.upDateCiudad(user).addOnCompleteListener { it2 ->


                    }
                    if (it.isSuccessful) {
                        alertDialog.dismiss()
                        val intent = Intent(this!!, SplashLoading::class.java)
                        startActivity(intent)
                    }

                }
            } else {
                val intent = Intent(this!!, SplashLoading::class.java)
                startActivity(intent)
            }


        }
        v.cancelar.setOnClickListener {
            alertDialog.dismiss()
        }

    }

    fun visitaGuiadaPrimerReporte() {

        TapTargetView.showFor(
            this,
            TapTarget.forView(
                floatingButton,
                "Crear Reportes",
                "Puedes hacer una pregunta o reportar eventos en ${PreferenciasGenerales(this!!).getpreferenceCiudadUsuario()} como baches, retenes, cámaras entre otras."
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

                    //
                    //abrir ventana para reportar
                }
            }
        )

    }

    private fun mensajePersonalizadoprimerUsuario() {

        val alertDialog: AlertDialog
        val builder: AlertDialog.Builder =
            AlertDialog.Builder(this!!)
        val v: View =
            LayoutInflater.from(this).inflate(R.layout.primer_usuario, null)
        builder.setView(v)
        builder.setCancelable(false)
        alertDialog = builder.create()
        alertDialog.show()
        val texto =
            "Eres el primer usuario en tu ciudad, crea tu primer Post para que la comunidad se entere que iniciaste el proyecto Draivin en ${
                PreferenciasGenerales(this!!).getpreferenceCiudadUsuario()
            }"
        malertDialog = alertDialog
        v.textoMensaje.text = texto
        v.vamos.setOnClickListener {
            alertDialog.dismiss()
            //registrarse
            val user = mAuth.currentUser
            if (user != null) {
                //crea el post
                crearPrimerPost()
            } else {
                //login
                val intent = Intent(this, AuthActivity::class.java)
                startActivity(intent)

            }


        }

    }

    private fun crearPrimerPost() {
        val post = Post()
        mPostProvider = PostProvider()
        post.imagen = ""
        post.type = "texto"
        post.nota = ""
        post.descripcion =
            "Soy el primer usuario en ${PreferenciasGenerales(this!!).getpreferenceCiudadUsuario()} \uD83D\uDE0A"
        post.uid = mAuth.currentUser!!.uid
        var fechalocal = Date().time
        post.fecha = fechalocal
        post.adminArea = PreferenciasGenerales(this!!).getpreferenceCiudadUsuario()
        /*   post.lat = UbicacionesUtil.getLatLng().latitude.toString()
           post.lng = UbicacionesUtil.getLatLng().longitude.toString()*/
        post.nameFile = ""
        post.nreporteNegativo = "0"
        mPostProvider.save(post).addOnCompleteListener {
            if (it.isSuccessful) {
                //tituloHome()
                primerReporte()

            } else {

            }
        }
    }

    private fun mensajePersonalizadoActualizacion(version: String?) {

        val alertDialog: AlertDialog
        val builder: AlertDialog.Builder =
            AlertDialog.Builder(this!!)
        val v: View =
            LayoutInflater.from(this).inflate(R.layout.cuadro_actualizacion, null)
        builder.setView(v)
        val texto =
            "Hay una nueva versión de Draivin ¿Desea actualizar?\nVersión actual: $VERSION_APP\nVersión disponible:$version"
        v.textVie.text = texto
        builder.setCancelable(true)
        alertDialog = builder.create()
        alertDialog.show()
        malertDialog = alertDialog
        v.afirmativo.setOnClickListener {
            malertDialog.dismiss()
            val packageName = "com.jomeva.driving"
            try {
                val intent =
                    Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName"))
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {

                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                )
                startActivity(intent)
            }

        }
        v.negativo.setOnClickListener {
            alertDialog.dismiss()
        }
    }

    private fun mensajePersonalizadoLogin() {

        val alertDialog: AlertDialog
        val builder: AlertDialog.Builder =
            AlertDialog.Builder(this!!)
        val v: View =
            LayoutInflater.from(this).inflate(R.layout.cuadro_registrarse_primera_vez, null)
        builder.setView(v)

        builder.setCancelable(true)
        alertDialog = builder.create()
        alertDialog.show()
        malertDialog = alertDialog
        v.afirmativo.setOnClickListener {
            malertDialog.dismiss()
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)

        }
        v.negativo.setOnClickListener {
            alertDialog.dismiss()
        }
    }

    private fun mensajePersonalizadoPregunta(lot: String, texto: String) {

        val alertDialog: AlertDialog
        val builder: AlertDialog.Builder =
            AlertDialog.Builder(this!!)
        val v: View = LayoutInflater.from(this).inflate(R.layout.cuadro_preguntar, null)
        builder.setView(v)
        v.textVie.text = texto
        v.lottieAnimationViewModoAlerta.setAnimation(lot)
        builder.setCancelable(true)
        alertDialog = builder.create()
        alertDialog.show()
        malertDialog = alertDialog
        if (lot == "happy_map.json") {
            v.afirmativo.text = "Listo!"
            v.negativo.visibility = View.GONE
        }
        v.afirmativo.setOnClickListener {
            if (lot == "happy_map.json") {
                alertDialog.dismiss()

                visitaGuiadaPrimerReporte()

            } else {

                malertDialog.dismiss()
                mensaje()
                mPeticionModeradorProvider.getPeticionesId(mAuth.uid.toString())
                    .addOnSuccessListener {
                        if (it.exists()) {
                            if (it.contains("idPeticion")) {
                                val idPeticion = it.getString("idPeticion").toString()
                                mPeticionModeradorProvider.deletePostByUser(idPeticion)
                                    .addOnCompleteListener { esto ->
                                        if (esto.isSuccessful) {
                                            malertDialog.dismiss()
                                            alertDialogSolicitudModerador()
                                        } else {

                                        }
                                    }
                            }
                        }
                    }

            }
        }
        v.negativo.setOnClickListener {
            alertDialog.dismiss()
        }
    }

    private fun mensajeSugerencias() {
        val lot = "question.json"
        val alertDialog: AlertDialog
        val builder: AlertDialog.Builder =
            AlertDialog.Builder(this!!)
        val v: View = LayoutInflater.from(this).inflate(R.layout.cuadro_post_sugerencias, null)
        builder.setView(v)
        builder.setCancelable(true)
        alertDialog = builder.create()
        alertDialog.show()
        getUsuarioSugerencias(v)
        /* val adRequest: AdRequest = AdRequest.Builder().build()
         val bannerventanaConsulta =
             v!!.findViewById<AdView>(R.id.cuadro_postSugerenciasAds)
         bannerventanaConsulta!!.loadAd(adRequest)*/
        malertDialog = alertDialog
        v.btnpublicarSugerencia.setOnClickListener {
            val mSugerenciasProvider = SugerenciasProvider()
            val mSugerenciasUsuarios = SugerenciasUsuarios()
            mSugerenciasUsuarios.uid = mAuth.uid.toString()
            mSugerenciasUsuarios.fecha = Date().time.toString()
            mSugerenciasUsuarios.descripcion = v.txtDescripcionSugerencia.text.toString()
            mSugerenciasUsuarios.ciudad = PreferenciasGenerales(this!!).getpreferenceCiudadUsuario()

            mSugerenciasProvider.save(mSugerenciasUsuarios).addOnCompleteListener {
                if (it.isSuccessful) {
                    makeText(
                        this,
                        "sugerencia enviada a los desarrolladores de Draivin",
                        LENGTH_LONG
                    ).show()
                }
            }
            malertDialog.dismiss()
            //mensaje()


        }

    }

    private fun mensaje() {
        val cuadro = R.layout.cuadro_eliminar_reporte
        val alertDialog: AlertDialog
        val builder: AlertDialog.Builder =
            AlertDialog.Builder(this!!)
        val v: View = LayoutInflater.from(this).inflate(cuadro, null)
        builder.setView(v)

        builder.setCancelable(true)
        alertDialog = builder.create()
        alertDialog.show()
        malertDialog = alertDialog
        //Handler(Looper.myLooper()!!).postDelayed({ alertDialog.dismiss() }, 4500)

    }

    private fun alertDialogSolicitudModerador() {
        val alertDialog: AlertDialog
        val builder: AlertDialog.Builder =
            AlertDialog.Builder(this)
        // Pasar nulo como vista principal porque va en el diseño del diálogo
        val v: View = LayoutInflater.from(this).inflate(R.layout.cuadro_peticion_moderador, null)

        builder.setView(v)
        getUsuario(v)
        builder.setCancelable(true)
        alertDialog = builder.create()
        alertDialog.show()

        val adRequest: AdRequest = AdRequest.Builder().build()
        val bannerventanaConsulta =
            alertDialog!!.findViewById<AdView>(R.id.cuadro_peticion_moderadorAds)
        bannerventanaConsulta!!.loadAd(adRequest)

        malertDialog = alertDialog
        v.cerrarPoster.setOnClickListener {
            malertDialog.dismiss()
        }
        v.btnsolicitar.setOnClickListener {
            guardarSolicitud(v)
            malertDialog.dismiss()
            mensajePersonalizado()
        }

    }

    private fun guardarSolicitud(v: View) {
        val peticionModerador = PeticionModerador()
        peticionModerador.uid = mAuth.uid.toString()
        peticionModerador.ciudad = v.ciudad.text.toString()
        peticionModerador.mensaje = v.txtDescripcion.text.toString()
        peticionModerador.fecha = Date().time
        peticionModerador.idPeticion = ""
        mPeticionModeradorProvider.save(peticionModerador).addOnCompleteListener {
            if (it.isSuccessful) {
                //mensaje y cerrar
                malertDialog.dismiss()
                makeText(this, "Peticion guardada", LENGTH_SHORT).show()
            } else {
                makeText(this, "negada la Peticion", LENGTH_SHORT).show()
            }
        }

    }

    private fun mensajePersonalizado() {
        val alertDialog: AlertDialog
        val builder: AlertDialog.Builder =
            AlertDialog.Builder(this!!)
        val v: View =
            LayoutInflater.from(this).inflate(R.layout.cuadro_mensaje_check, null)
        builder.setView(v)

        builder.setCancelable(false)
        alertDialog = builder.create()
        alertDialog.show()
        malertDialog = alertDialog

    }

    private fun getUsuario(v: View) {
        Log.d("getUser", "v")
        mUserProviders.getUsuario(mAuth.uid.toString()).addOnSuccessListener {
            if (it.exists()) {
                if (it.contains("nombre")) {
                    v.nombreSolicitante.setText("${it.get("nombre")}")
                    Log.d("getUser", "${it.get("nombre")}")
                }
                if (it.contains("photo")) {
                    var imagenPerfil: String = it.get("photo").toString()
                    if (it.get("photo").toString() == "") {
                        Picasso.get().load(R.drawable.person_24).error(R.drawable.person_24).into(
                            v.photoPerfil
                        )
                    } else {
                        Log.d("getUserfoto", "v")
                        Picasso.get().load(imagenPerfil).error(R.drawable.person_24)
                            .into(v.photoPerfil)
                    }
                }
                if (it.contains("ciudad")) {
                    var ciudad: String = it.get("ciudad").toString()
                    v.ciudad.setText(ciudad)
                }

            }
        }
    }

    private fun getUsuarioSugerencias(v: View) {
        Log.d("getUser", "v")
        mUserProviders.getUsuario(mAuth.uid.toString()).addOnSuccessListener {
            if (it.exists()) {
                if (it.contains("nombre")) {
                    v.nombrePostSugerencia.setText("${it.get("nombre")}")
                    Log.d("getUser", "${it.get("nombre")}")
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


            }
        }
    }

    //1673374632=8:13:19
    //1673356786=8:19:55
    //=6:36 seg

    //Acá debe guardar la ciudad que me traigo de fragment 1
    private fun tituloHome() {
        mUserProviders.getUsuario(mAuth.uid.toString()).addOnSuccessListener { it1 ->
            if (it1.exists()) {
                if (it1.contains("photo")) {
                    val foto = it1.getString("photo")
                    if (foto != "") {
                        Picasso.get().load(foto)
                            .error(R.drawable.ic_person_azul).into(menuCardHome)
                    }
                }

                if (it1.contains("tiempoEnLaApp")) {
                    tiempoDeUso = 0
                    val inicio = it1.getString("tiempoEnLaApp")!!.toLong()
                    PreferenciasGenerales(this!!).setpreferenceTiempoApp(inicio.toString())
                    Log.d("tiempoDeUsotiempoDeUso2", "$inicio")
                    tiempoDeUso = inicio
                    val t = RelativeTime.RelativeTime.timeAUsoApp(inicio)
                    tiempoDeUsoInicial = Date().time
                    Log.d("tiempoDeUsotiempoDeUso3", "$tiempoDeUsoInicial")
                }
                //debo poner un disparador para que elusuario recuerde activar su ciudad
            }
            /* mPublicacionPorAprobarProvider.getPublicacionesByCity(PreferenciasGenerales(this!!).getpreferenceCiudadUsuario()!!)
                 .addSnapshotListener { value, error ->

                     val tam = value!!.size()
                     if (tam > 99) {
                         NPublicacionesporAprobar.text = "99+"
                     } else {
                         if (tam == 0) {
                             viewNpublicaciones.visibility = View.GONE
                             NPublicacionesporAprobar.visibility = View.GONE
                         } else {
                             viewNpublicaciones.visibility = View.VISIBLE
                             NPublicacionesporAprobar.visibility = View.VISIBLE
                             NPublicacionesporAprobar.text = tam.toString()
                         }

                     }

                 }*/
        }
        //////////////////////////////////////////////////////

        if (PreferenciasGenerales(this!!).getpreferenceCiudadUsuario() != "" && PreferenciasGenerales(
                this!!
            ).getpreferenceGuiaSoyModerador() == "false"
        ) {
            mModeradoresProvider.getModeradorbyCity(PreferenciasGenerales(this!!).getpreferenceCiudadUsuario())
                .get().addOnSuccessListener { it2 ->
                    val tam = it2.size()
                    if (tam % 10 == 0 || tam < 4) {

                        mModeradores.uid = mAuth.uid.toString()
                        mModeradores.ciudad =
                            PreferenciasGenerales(this!!).getpreferenceCiudadUsuario()
                        mModeradoresProvider.save(mModeradores).addOnCompleteListener { it3 ->
                            if (it3.isSuccessful) {
                                frameBotonModerador.visibility = View.VISIBLE
                                makeText(this, "Asignado como moderador", LENGTH_SHORT).show()
                                if (PreferenciasGenerales(this).getpreferenceGuiaSoyModerador() == "false") {
                                    visitaGuiadasoyModerador()
                                }
                            }
                        }
                    }
                    Log.d("tamanio", "$tam")


                }
        }


        ///////////////////////////////////////////////
        mModeradoresProvider.getModeradorbyId(mAuth.uid.toString()).get()
            .addOnSuccessListener {
                val tam = it.size()
                if (tam > 0) {
                    frameBotonModerador.visibility = View.VISIBLE
                    if (PreferenciasGenerales(applicationContext).getpreferenceGuiaSoyModerador() == "false") {
                        visitaGuiadasoyModerador()
                    }

                } else {
                    frameBotonModerador.visibility = View.GONE
                }
            }


    }


    fun visitaGuiadasoyModerador() {
        if (bandera) {

            bandera = !bandera
            TapTargetView.showFor(
                this,
                TapTarget.forView(
                    frameBotonModerador,
                    "Eres moderador",
                    "Los moderadores tendrán privilegios como:\nPublicar sin ser aprobados\nPresentar información relevante a los usuarios\nBorrar reportes de otros usuarios \ncontarán con un panel donde podrán controlar actividades."
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

                        PreferenciasGenerales(applicationContext).setpreferenceGuiaSoyModerador("true")
                        val intent = Intent(applicationContext, HomeModerador::class.java)
                        startActivity(intent)
                    }
                }
            )
        }
    }

    fun visitaGuiadaMenu(it: View) {
        TapTargetView.showFor(
            this,
            TapTarget.forView(
                menuCardHome,
                "Menú",
                "Podrás cambiar la ciudad, ver información de la ciudad como: \nPico y placa, \nClínicas, \nambulancias,\nConfiguración de notificación de reportes, entre otras."
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

                @RequiresApi(Build.VERSION_CODES.N)
                override fun onTargetClick(view: TapTargetView) {
                    super.onTargetClick(view)

                    PreferenciasGenerales(applicationContext!!).setpreferenceGuiaMenuHome("true")

                    metodoMenu(it)
                }
            }
        )

    }

    override fun onStart() {
        super.onStart()
        Log.d("atento3", "->")


        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(mNetworkChangeListener, filter)

        val user = mAuth.currentUser
        if (user != null) {
            upDateOnline(true)
            if (mAuth.currentUser!!.uid != null) {
                ViewedMessageHelper().updateOnline(true, this)
                Log.d("tituloHome", "x")


                mPostProvider.getPostByCiudad(PreferenciasGenerales(this!!).getpreferenceCiudadUsuario())
                    .get().addOnSuccessListener { it2 ->
                        val tam = it2.size()
                        if (tam == 0) {
                            crearPrimerPost()
                            tituloHome()
                        } else {
                            primerReporte()
                            tituloHome()
                        }
                        Log.d("primerReporte2", "$tam")
                    }

            }
        } else {
            if (PreferenciasGenerales(this!!).getpreferenceCiudadUsuario() != "") {
                mPostProvider.getPostByCiudad(PreferenciasGenerales(this!!).getpreferenceCiudadUsuario())
                    .get().addOnSuccessListener { it2 ->
                        val tam = it2.size()

                        if (tam == 0) {
                            mensajePersonalizadoprimerUsuario()
                        } else {
                            primerReporte()
                            /*if (PreferenciasGenerales(this!!).getpreferencePrimerReporte() == "false") {
                                viewPager.currentItem = 0
                                *//* val texto="¿Te parece si creamos el primer reporte en ${PreferenciasGenerales(applicationContext).getpreferenceCiudadUsuario()}?"
                                 val lot="happy_map.json"
                                 mensajePersonalizadoPregunta(lot,texto)*//*
                            }*/
                        }

                    }

            }

        }
    }

    fun primerReporte() {
        val pref = PreferenciasGenerales(this!!).getpreferencePrimerReporte().toInt()
        if (pref % 3 == 0 || pref == 0 && pref < 99) {
            viewPager.currentItem = 0
            val texto = "¿Te parece si creamos el primer reporte en ${
                PreferenciasGenerales(applicationContext).getpreferenceCiudadUsuario()
            }?"
            val lot = "happy_map.json"
            mensajePersonalizadoPregunta(lot, texto)
        }
        if (pref < 99) {
            PreferenciasGenerales(this!!).setpreferencePrimerReporte("${pref + 1}")
        }

        Log.d("primerReporte", "$pref")

    }

    override fun onStop() {
        super.onStop()

        unregisterReceiver(mNetworkChangeListener)

    }

    override fun onPause() {
        super.onPause()
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        if (user != null) {
            updateTiempoEnLaApp()
            Log.d("pausandoooo", "x")
            // if (mAuth.currentUser!!.uid != null) {
            ViewedMessageHelper().updateOnline(false, this)
            // }
        }
    }

    override fun onResume() {
        super.onResume()
        val mActualizacionDisponible = ActualizacionDisponible()
        mActualizacionDisponible.version = VERSION_APP
        val mActualizacionProvider = ActualizacionProvider()

        mActualizacionProvider.getVersion().get().addOnSuccessListener {
            Log.d("sizeeeeee", "->${it.size()}")
            if (it.size() > 0) {
                val version = it.documents[0].get("version").toString()
                Log.d("sizeeeeee2", "->${version}")
                if (version == VERSION_APP) {

                } else {
                    //mostrar mensaje de actualizacion
                    mensajePersonalizadoActualizacion(version)

                }
            } else {

                mActualizacionProvider.crearVersion(mActualizacionDisponible)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {

                        }
                    }
            }
        }


        tiempoDeUsoInicial = Date().time
        tiempoDeUso = PreferenciasGenerales(this!!).getpreferenceTiempoApp().toLong()

    }

    private fun updateTiempoEnLaApp() {
        val final = Date().time
        val tiempo = (final - tiempoDeUsoInicial) / 1000
        tiempoDeUso += tiempo
        PreferenciasGenerales(this!!).setpreferenceTiempoApp(tiempoDeUso.toString())
        val t = RelativeTime.RelativeTime.timeAUsoApp(tiempoDeUso)
        val user = Usuarios()
        user.tiempoEnLaApp = tiempoDeUso.toString()
        user.uid = mAuth.uid.toString()
        user.online = false
        mUserProviders.upDatePerfil(user).addOnCompleteListener {
            if (it.isSuccessful) {
            } else {
            }
        }

    }


    private fun upDateOnline(status: Boolean) {
        /*if (mAuth.currentUser!!.uid != null) {
            mUserProviders.updateOnline(mAuth.currentUser!!.uid, status)
        }*/
    }

    fun isOnlineNet(): Boolean {
        try {
            val p = Runtime.getRuntime().exec("ping -c 1 www.google.es")
            val `val` = p.waitFor()
            return `val` == 0
        } catch (e: Exception) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
        return false
    }

    private fun crearToken(idUser: String) {
        mtokenProvider.crearToken(idUser)
    }

    fun abrirMiCiudad() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val bottomsheetBehavior: BottomSheetBehavior<View>
        val bottomSheetView = LayoutInflater.from(this)
            .inflate(
                R.layout.cuadro_mi_ciudad,
                bottom_sheet_mi_ciudad
            )
        bottomSheetDialog.setContentView(bottomSheetView)
//behavior
        bottomsheetBehavior = BottomSheetBehavior.from(bottomSheetView.parent as View)
        // set to behavior
        bottomsheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        val layout =
            bottomSheetDialog.findViewById<CoordinatorLayout>(R.id.bottom_sheet_mi_ciudad)
        assert(layout != null)
        layout!!.minimumHeight = Resources.getSystem().displayMetrics.heightPixels


        bottomSheetDialog.show()

        /*val adRequest: AdRequest = AdRequest.Builder().build()
        val bannerventanaConsulta = layout!!.findViewById<AdView>(R.id.cuadro_mi_ciudadAds)
        bannerventanaConsulta!!.loadAd(adRequest)*/
        titulosUtilCiudad(bottomSheetView)
//////////////////////////////////////////////////////////////////////////
    }

    private fun titulosUtilCiudad(v: View) {
        listaTitulos.removeAll(listaTitulos.toSet())
        listaTitulos.add(UtilCiudad("Pico y placa"))
        listaTitulos.add(UtilCiudad("Clínicas"))
        listaTitulos.add(UtilCiudad("Ambulancias"))
        listaTitulos.add(UtilCiudad("Mecánicos"))
        listaTitulos.add(UtilCiudad("Grúas"))
        listaTitulos.add(UtilCiudad("Tarifas"))
        listaTitulos.add(UtilCiudad("Obras activas"))
        val mRecicler = v.findViewById<RecyclerView>(R.id.reciclerViewUtil)
        mAdapterUtilCiudad = AdapterUtilCiudad(this, listaTitulos)
        mlinearLayoutManagerUtil = LinearLayoutManager(this)
        mRecicler.layoutManager = mlinearLayoutManagerUtil
        v.reciclerViewUtil.adapter = mAdapterUtilCiudad
        mAdapterUtilCiudad.notifyDataSetChanged()
        v.colapsing.title = PreferenciasGenerales(this).getpreferenceCiudadUsuario()
    }


}


