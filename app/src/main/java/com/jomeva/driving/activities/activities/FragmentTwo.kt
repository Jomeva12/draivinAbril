package com.jomeva.driving.activities.activities

//import com.example.driving.R

/*import kotlinx.android.synthetic.main.activity_perfil.*
import kotlinx.android.synthetic.main.activity_perfiledit.**/

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import kotlinx.coroutines.*
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.*
import android.hardware.*
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.net.Uri
import android.os.*
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.jomeva.driving.R
import com.jomeva.driving.activities.adapters.AdapterReport
import com.jomeva.driving.activities.modelos.*
import com.jomeva.driving.activities.providers.*
import com.jomeva.driving.activities.util.*
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.GeoQueryEventListener
import com.fxn.pix.Options
import com.fxn.pix.Pix
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnCameraIdleListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseError
import com.google.firebase.firestore.DocumentSnapshot
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.cardview_report.*
import kotlinx.android.synthetic.main.cuadro_actualizar_ciudad.view.*
import kotlinx.android.synthetic.main.cuadro_comentario_propinas.view.*
import kotlinx.android.synthetic.main.cuadro_datos_reporte.*
import kotlinx.android.synthetic.main.cuadro_datos_reporte.view.*
import kotlinx.android.synthetic.main.cuadro_mapa_consulta_opciones.*
import kotlinx.android.synthetic.main.cuadro_mapa_consulta_opciones.view.*

import kotlinx.android.synthetic.main.cuadro_opciones_mapa.view.*

import kotlinx.android.synthetic.main.cuadro_preguntar.view.*
import kotlinx.android.synthetic.main.cuadro_propina_mapa.*
import kotlinx.android.synthetic.main.cuadro_propina_mapa.view.*
import kotlinx.android.synthetic.main.cuadro_propina_mapa.view.btnpublicar
import kotlinx.android.synthetic.main.cuadro_propina_mapa.view.linearLikePropina
import kotlinx.android.synthetic.main.cuadro_propina_mapa.view.nombrePost
import kotlinx.android.synthetic.main.cuadro_propina_mapa.view.photoPerfilPosts
import kotlinx.android.synthetic.main.cuadro_propina_mapa.view.txtDescripcion
import kotlinx.android.synthetic.main.cuadro_reporte_mapa.*
import kotlinx.android.synthetic.main.cuadro_reporte_mapa.view.*
import kotlinx.android.synthetic.main.fragment_two.*
import kotlinx.android.synthetic.main.fragment_two.view.*

import kotlinx.android.synthetic.main.layout_opciones_mapa.*
import kotlinx.android.synthetic.main.layout_opciones_mapa.view.*
import kotlinx.android.synthetic.main.layout_opciones_mapa.view.optPeligrosa
import retrofit2.Call
import retrofit2.Response
import java.io.File
import java.util.*
import kotlin.math.atan
import kotlin.math.sqrt
import kotlin.properties.Delegates


class FragmentTwo : Fragment(), OnMapReadyCallback, LocationListener, MyListener {
    //post imagen
    private var mFile: File? = null
    private var mPhotoFile: File? = null
    private lateinit var mImageProvider: ImageProvider
    private lateinit var mPhotoPath: String
    private lateinit var mOptions: Array<CharSequence>
    private var PHOTO_REQUEST_CODE = 3
    private var GALLERY_REQUEST_CODE = 1
    private lateinit var mBuilderSelector: AlertDialog.Builder
    private lateinit var vista: View
    private lateinit var mAbsolutePhotoPath: String
    private lateinit var mTokenProvider: TokenProvider
    private lateinit var mNotificationProviders: NotificationProvider
    private lateinit var mAuth: FirebaseAuth
    private val mUsersMarkers: MutableList<Marker> = ArrayList()
    private val mReporteMarkers: MutableList<Marker> = ArrayList()
    private val mUIDuser: MutableList<String> = ArrayList()
    private var mIsFirstTime = true
    private lateinit var mRaiting: Raiting
    private lateinit var mRaitingProvider: RaitingProvider
    private lateinit var mMap: GoogleMap
    private lateinit var mLocationRequest: LocationRequest
    private lateinit var mFusedLocation: FusedLocationProviderClient
    private val LOCATION_REQUEST_CODE = 1
    private val SETTINGS_REQUEST_CODE = 2
    private var mMarker: Marker? = null
    private lateinit var mCurrentLatLng: LatLng
    private lateinit var mReporteLatLng: LatLng
    private lateinit var mGeofireProvider: GeofireProvider
    private lateinit var mGeofireProviderReportes: GeofireProviderReportes

    private lateinit var mUserProviders: UserProviders
    private lateinit var mPostProviders: PostProvider
    private lateinit var mAutoCompletar: AutocompleteSupportFragment
    private lateinit var mPlaces: PlacesClient

    private lateinit var mReporteMapaProvider: ReporteMapaProvider
    private lateinit var mUser: Usuarios
    private lateinit var mReporteEnMapa: ReporteEnMapa
    private lateinit var mCategoria: String
    private var mCameraListener: OnCameraIdleListener? = null
    private var mCameraListenerReporte: OnCameraIdleListener? = null
    private var banderaMoverCameraUnaVez: Boolean = true
    private var mCiudadReporte: String = ""
    private var mpaisReporte: String = ""
    private var mRatingBar: Float = 0f
    public var milongitud: Double? = null

    public var mListaCiudades: List<Address> = arrayListOf()
    private lateinit var mHomeActivity: HomeActivity
    private lateinit var mPropinasProvider: PropinasProvider
    var mEstiloMapa = ""
    private lateinit var mprovider: PostProvider
    private var zoom by Delegates.notNull<Float>()
    private lateinit var mView: View
    private var isFragmenActiveFirstTime: Boolean = true
    private lateinit var myLatLng: LatLng
    private lateinit var mFragmentOne: FragmentOne

    // magnetic
    private var listaMapa: MutableList<Map<String, GeoLocation>> =
        arrayListOf()
    private var listaMapaIdReport: MutableList<String> =
        arrayListOf()


    // reportes en pantalla temporal
    private lateinit var mAdapterReport: AdapterReport
    private var mListaReportesRecycler: MutableList<ReportesRecycler> =
        arrayListOf()
    private var BanderaNotificacionReporte: Boolean = true
    private var tipoDeReporteOConsulta = ""
    private lateinit var mContextFragment: Context
    private var lastPositionLAT: Double = 0.0
    private var lastPositionLON: Double = 0.0
    private var miAngulo: Double = 0.0
    private var lastIdReport: String = ""
    private lateinit var mPreferencias: Preferencias
    public lateinit var mReportesRecycler: ReportesRecycler
    private lateinit var ciudadUser: String
    private lateinit var mModeradoresProvider: ModeradoresProvider
    private lateinit var mModeradores: Moderadores

    //interfaz
//pix
    private var mImageFile: File? = null
    private var mOptiones: Options? = null
    var mReturnValues: ArrayList<String> = ArrayList()
    private lateinit var mPublicacionPorAprobarProvider: PublicacionPorAprobarProvider

    //sonido
    private var soundPool: SoundPool? = null
    private var sonidoRepro: Int = 0
    var contadorActualizarMapa = 0

    //crear un hilo diferente porque acá es donde se explota
    var mLocationCallback: LocationCallback = object : LocationCallback() {

        override fun onLocationResult(locationResult: LocationResult) {
            Log.d("primeraVezFragment2grados", "${mMap.cameraPosition.bearing + 90f}")
            for (location in locationResult.locations) {
                if (context != null) {
                    mCurrentLatLng = LatLng(location.latitude, location.longitude)
                    location.altitude
                    if (mMarker != null) {
                        mMarker?.remove()
                    }

                    myLatLng = mCurrentLatLng


                    if (contadorActualizarMapa == 0) {
                        mMarker = mMap.addMarker(
                            MarkerOptions().position(
                                LatLng(location.latitude, location.longitude)
                            )
                                .title("Mi posicion")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marcador1))
                        )

                        /*  UbicacionesUtil.setLatLng(mMarker!!.position)*/
                        /*mMap.setOnCameraMoveListener {
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                                CameraPosition.builder(mMap.cameraPosition)
                                    .bearing(mMap.cameraPosition.bearing + 90f)
                                    .zoom(18f)
                                    .tilt(50.0f)
                                    .build()))
                        }*/

                        mMap.animateCamera(
                            CameraUpdateFactory.newCameraPosition(
                                CameraPosition.Builder()
                                    .target(LatLng(location.latitude, location.longitude))
                                    .zoom(18f)
                                    .tilt(50.0f)
                                    .bearing(0f)
                                    .build()
                            )
                        )
                        contadorActualizarMapa++
                        getReportesActive()
                        getLocationforUsers()
                    } else {
                        contadorActualizarMapa++
                        mMarker = mMap.addMarker(
                            MarkerOptions().position(
                                LatLng(location.latitude, location.longitude)
                            )
                                .title("Mi posicion")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marcador1))
                        )
                        if (contadorActualizarMapa == 10) {
                            contadorActualizarMapa = 0
                        }

                        getReportesActive()
                        // getLocationforUsers()
                    }

                    getMyDegrees(mCurrentLatLng)
                    //mostrarReportesTmporales()
                    // updateLocation()
                    //mostrar si son premium
                    //getUserActive()
                }

            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        MapView.onCreate(savedInstanceState)
        MapView.onSaveInstanceState(savedInstanceState)
        MapView.onResume()
        MapView.getMapAsync(this)
        mFragmentOne = FragmentOne()
        mPreferencias = Preferencias(context!!)
        BanderaNotificacionReporte = mPreferencias.getpreferencepreferencetodasAlertas()
        mPublicacionPorAprobarProvider = PublicacionPorAprobarProvider()
        if (BanderaNotificacionReporte) {
            floatingButtonNotificaciones.setImageResource(R.drawable.campana_on)
        } else {
            floatingButtonNotificaciones.setImageResource(R.drawable.campana_off)
        }

        var getPreferences: SharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(context)

        ciudadUser = getPreferences.getString("ciudadUser", "General").toString()

        zoom = 18f
        inicialize()
        floatingButtonMylocation.setOnClickListener {
            posicionMarcador()
        }
        floatingButtonNotificaciones.setOnClickListener {
            if (BanderaNotificacionReporte) {
                floatingButtonNotificaciones.setImageResource(R.drawable.campana_off)
                BanderaNotificacionReporte = false
                mPreferencias.setpreferenceTodasAlertas(BanderaNotificacionReporte)
            } else {
                BanderaNotificacionReporte = true
                mPreferencias.setpreferenceTodasAlertas(BanderaNotificacionReporte)
                floatingButtonNotificaciones.setImageResource(R.drawable.campana_on)

            }

        }
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

        mLocationCallback

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mContextFragment = context!!.applicationContext
        Log.d("contextoo1", "$mContextFragment")
        val mView = inflater.inflate(R.layout.fragment_two, container, false)
        val permitir = mView.findViewById<FrameLayout>(R.id.PedirPermisoFrame)
        permitir.setOnClickListener {
            val permission = Manifest.permission.ACCESS_FINE_LOCATION
            if (ContextCompat.checkSelfPermission(
                    context!!,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(activity!!, permission)) {
                    Toast.makeText(context, "Habilíte permisos de ubicación.", Toast.LENGTH_LONG)
                        .show()
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", context!!.packageName, null)
                    intent.data = uri
                    startActivityForResult(intent, SETTINGS_REQUEST_CODE)
                } else {
                    ActivityCompat.requestPermissions(
                        activity!!,
                        arrayOf(permission),
                        SETTINGS_REQUEST_CODE
                    )
                }
            } else {
                // El permiso ya ha sido otorgado anteriormente
            }



            if (!gpsActived()) {
                Log.d("alertaaa2", "aaa")
                showAlertDialogNOGPS()


            } else {
                Log.d("alertaaa3", "aaa")
                iniciarLocalizacion("d")
            }
        }


        return mView
    }



    private fun startPix() {
        Pix.start(this, mOptiones);
    }

    private fun posicionMarcador() {
        if (ActivityCompat.checkSelfPermission(
                context!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context!!,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }
        if (gpsActived()) {

            mFusedLocation.lastLocation.addOnSuccessListener {

                mMap.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                            it.latitude,
                            it.longitude
                        ), 18f
                    )
                )
                if (mMarker != null) {
                    mMarker?.remove()
                }
                mMarker = mMap.addMarker(
                    MarkerOptions().position(
                        LatLng(it.latitude, it.longitude)
                    )
                        .title("Mi posicion")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marcador1))
                )
                getLocationforUsers()
                getReportesActive()

            }
        }

    }


    fun compareToMe(
        points: MutableList<Map<String, GeoLocation>>,
        currentLatLng: LatLng
    ) {
        var myLat = currentLatLng.latitude
        var myLon = currentLatLng.longitude

        var theta = 0.0

        var distancia = 0.0
        var k = ""
        var aMap = 0.0
        var bMap = 0.0
        for ((key, valor) in points[0]) {
            k = key
            aMap = valor.latitude
            bMap = valor.longitude
            var a = (aMap - myLat) * (aMap - myLat)
            var b = (bMap - myLon) * (bMap - myLon)
            val div = (aMap - myLat) / (bMap - myLon)
            theta = Math.toDegrees(atan(div))
            distancia = sqrt(a + b) * 100000
        }
        if ((miAngulo > theta - 12) && (miAngulo < theta + 12) && lastIdReport != k) {


            //listaMapaIdReport.removeAt(0)
            mReporteMapaProvider = ReporteMapaProvider()


            mReporteMapaProvider.getReporte(k).addOnSuccessListener {
                if (it.exists()) {
                    if (it.contains("categoria")) {
                        val categoria = it.getString("categoria")
                        if (distancia <= distanciaPreferencia(categoria).toDouble()) {
                            mReportesRecycler = ReportesRecycler()
                            if (listaMapaIdReport.contains(k)) {

                            } else {
                                listaMapaIdReport.add(k)
                                mReportesRecycler.idReporte = k!!
                                mReportesRecycler.categoria = categoria!!
                                mReportesRecycler.distancia = distancia.toInt()!!

                                //la lista solo será de tres
                                if (mListaReportesRecycler.size == 0) {
                                    Log.i("looooo1", "${mListaReportesRecycler.size}  ")
                                    mListaReportesRecycler.add(0, mReportesRecycler)

                                } else if (mListaReportesRecycler.size == 1) {
                                    /* aux=mListaReportesRecycler[0]
                                     mListaReportesRecycler[0]=mReportesRecycler
                                     mListaReportesRecycler.add(1,aux)*/
                                    Log.i("looooo3", "${mListaReportesRecycler.size}  ")
                                    mListaReportesRecycler.add(0, mReportesRecycler)
                                    // mListaReportesRecycler.add(1, mReportesRecycler)

                                } else if (mListaReportesRecycler.size == 2) {
                                    Log.i("looooo4", "${mListaReportesRecycler.size}  ")


                                    mListaReportesRecycler.add(0, mReportesRecycler)

                                } else if (mListaReportesRecycler.size == 3) {
                                    Log.i("looooo2", "${mListaReportesRecycler.size}  ")
                                    mListaReportesRecycler.removeAt(2)
                                    mListaReportesRecycler.add(0, mReportesRecycler)


                                }

                                lastIdReport = k
                                Log.i("looooo", "${mListaReportesRecycler.size}  ")

                                //acá es para sopnido corto
                                cardReporte.visibility = View.VISIBLE
                                // if (cardReporte.isVisible) {
                                //animación y tarjeta verde
                                categoriaTVFragmenttwo.text = categoria.toString()
                                distanciaTVFragmenttwo.text = distancia.toInt().toString()
                                var animation: Animation =
                                    AnimationUtils.loadAnimation(context, R.anim.fade_in)
                                floatingButton.visibility = View.GONE

                                cardReporte.animation = animation
                                animation =
                                    AnimationUtils.loadAnimation(context, R.anim.fade_out)
                                cardReporte.animation = animation
                                //}


                                Handler(Looper.getMainLooper()).postDelayed(
                                    {
                                        cardReporte.visibility = View.GONE
                                        floatingButton.visibility = View.VISIBLE
                                        //llenando recycler
                                        idReporteFragment.layoutManager =
                                            LinearLayoutManager(context)
                                        mAdapterReport =
                                            AdapterReport(context!!, mListaReportesRecycler)
                                        idReporteFragment.adapter = mAdapterReport
                                    }, 4000
                                )
                                sonidovibrar(aMap, bMap)
                            }
                        }
                    }
                }
            }


        }

    }

    private fun sonidovibrar(aMap: Double, bMap: Double) {
        var vibrator =
            context!!.applicationContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(2000)
        soundPool?.play(
            sonidoRepro, 1f, 1f, 0, 0, 1f
        );
        mMap.animateCamera(
            CameraUpdateFactory.newCameraPosition(
                CameraPosition.Builder()
                    .target(LatLng(aMap, bMap))
                    .zoom(20f)
                    .tilt(50.0f)
                    .bearing(0f)
                    .build()
            )
        )


        Handler(Looper.getMainLooper()).postDelayed(
            {
//acá es donde va a borrar, revisa la posicion qe borrará
                if (mListaReportesRecycler.size > 0) {

                    mListaReportesRecycler.removeAt(mListaReportesRecycler.size - 1)

                }


                idReporteFragment.layoutManager =
                    LinearLayoutManager(context)
                mAdapterReport =
                    AdapterReport(context!!, mListaReportesRecycler)
                idReporteFragment.adapter = mAdapterReport

            }, 40 * 1000
        )

        Handler(Looper.getMainLooper()).postDelayed(
            {
                linearPuntoReporte.visibility = View.VISIBLE

            }, 1000
        )


        Handler(Looper.getMainLooper()).postDelayed(
            {
                linearPuntoReporte.visibility = View.GONE
                mMap.animateCamera(
                    CameraUpdateFactory.newCameraPosition(
                        CameraPosition.Builder()
                            .target(
                                LatLng(
                                    mCurrentLatLng.latitude,
                                    mCurrentLatLng.longitude
                                )
                            )
                            .zoom(19f)
                            .tilt(50.0f)
                            .bearing(0f)
                            .build()
                    )
                )
            }, 4000
        )
    }

    private fun mostrarrecycler() {

    }

    private fun distanciaPreferencia(categoria: String?): String {
        return when (categoria) {
            "Bache" -> {
                mPreferencias.getpreferenceBache()

            }
            "Especial" -> {
                mPreferencias.getpreferenceEspeciales()
            }
            "Vía cerrada" -> {
                mPreferencias.getpreferenceViaCerrada()
            }
            "Accidente-trancón" -> {
                mPreferencias.getpreferenceTrancon()
            }
            "Zona Peligrosa" -> {
                mPreferencias.getpreferencePeligrosa()
            }
            "Cámara" -> {
                mPreferencias.getpreferenceCamara()
            }
            "Retén" -> {
                mPreferencias.getpreferenceReten()

            }
            else -> {
                "Sin info"

            }
        }

    }

    fun getMyDegrees(mCurrentLatLng: LatLng) {
        val newPositionLAT = mCurrentLatLng.latitude
        val newPositionLON = mCurrentLatLng.longitude
        var theta = 0.0
        val x = lastPositionLON - newPositionLON
        val y = lastPositionLAT - newPositionLAT
        var div = if (x != 0.0) {
            y / x
        } else {
            0.0
        }
        theta = (Math.toDegrees(atan(div)))
        if (newPositionLAT != lastPositionLAT && newPositionLON != lastPositionLON) {
            lastPositionLAT = newPositionLAT
            lastPositionLON = newPositionLON
        }

        val grad = theta - 90
        Log.d("thetaa", "$grad")
        miAngulo = theta

    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun inicialize() {


        mAuth = FirebaseAuth.getInstance()
        mGeofireProvider = GeofireProvider()
        mGeofireProviderReportes = GeofireProviderReportes()
        mUserProviders = UserProviders()
        mImageProvider = ImageProvider()
        mBuilderSelector = AlertDialog.Builder(context!!)
        mPhotoPath = String()
        mPropinasProvider = PropinasProvider()
        mPostProviders = PostProvider()
        mReporteMapaProvider = ReporteMapaProvider()
        mReporteEnMapa = ReporteEnMapa()
        mUser = Usuarios()
        mRaiting = Raiting()
        mRaitingProvider = RaitingProvider()
        mFusedLocation = LocationServices.getFusedLocationProviderClient(context!!)
        if (ActivityCompat.checkSelfPermission(
                context!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context!!,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }


        val title = mBuilderSelector.setTitle("Selecciona una opción")
        mOptions = arrayOf<CharSequence>("Galería", "Cámara")

        mHomeActivity = HomeActivity()
        mprovider = PostProvider()

        mView = View(context)

        //sonido
        if (Build.VERSION.SDK_INT
            >= Build.VERSION_CODES.LOLLIPOP
        ) {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(
                    AudioAttributes.USAGE_ASSISTANCE_SONIFICATION
                )
                .setContentType(
                    AudioAttributes.CONTENT_TYPE_SONIFICATION
                )
                .build()
            soundPool = SoundPool.Builder()
                .setMaxStreams(6)
                .setAudioAttributes(
                    audioAttributes
                )
                .build()
        } else {
            soundPool = SoundPool(
                3,
                AudioManager.STREAM_MUSIC,
                0
            )
        }
        sonidoRepro = soundPool!!.load(context, R.raw.campana1, 1)

        floatingButton.setOnClickListener {
            val users = mAuth.currentUser
            if (users != null) {
                alertDialogOpcionesMapa()
            } else {
                mensajePersonalizadoLogin(context!!)
            }

        }


        btnSeleccionar.setOnClickListener {
            //debe abrir en el move
            linearCargando.visibility = View.VISIBLE
            onCameraMove()
            linearCargando.visibility = View.GONE
        }
        btnCancelar.setOnClickListener {
            framePosicionMarker.visibility = View.GONE
            btnSeleccionar.visibility = View.GONE
            btnCancelar.visibility = View.GONE
        }
        mModeradoresProvider = ModeradoresProvider()
        mModeradores = Moderadores()
    }

    private fun mensajePersonalizadoLogin(context: Context) {

        val alertDialog: androidx.appcompat.app.AlertDialog
        val builder: androidx.appcompat.app.AlertDialog.Builder =
            androidx.appcompat.app.AlertDialog.Builder(context!!)
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

    private fun mostrarDondePreguntar() {
        framePosicionMarker.visibility = View.VISIBLE
        btnSeleccionar.visibility = View.VISIBLE
        btnCancelar.visibility = View.VISIBLE

        if (PreferenciasGenerales(context!!).getpreferenceUbicacionReporte() == "true") {

        } else {
            val titulo = "Reportar en el mapa"
            val descripcion =
                "Deslízate por el mapa y encuentra donde vas a generar el reporte.\nSé lo mas preciso posible."
            visitaGuiadaConsultaMapa(titulo, descripcion)
        }

    }


    private fun abrirOpciones() {

        val bottomSheetDialog = BottomSheetDialog(context!!, R.style.BottomSheetDialogTheme)
        val bottomSheetView = LayoutInflater.from(context)
            .inflate(R.layout.layout_opciones_mapa, hoja)

        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()


        val adRequest: AdRequest = AdRequest.Builder().build()
        val bannerventanaConsulta =
            bottomSheetView!!.findViewById<AdView>(R.id.layout_opciones_mapa)
        bannerventanaConsulta.loadAd(adRequest)


        bottomSheetView.optBache.setOnClickListener {
            mCategoria = "Bache"
            bottomSheetDialog.hide()
            validarSiConoceComoReportar()
        }
        bottomSheetView.optEspecial.setOnClickListener {
            mCategoria = "Especial"
            bottomSheetDialog.hide()
            validarSiConoceComoReportar()
        }
        bottomSheetView.optCerrada.setOnClickListener {
            mCategoria = "Vía cerrada"
            bottomSheetDialog.hide()
            validarSiConoceComoReportar()
        }
        bottomSheetView.optAccidente.setOnClickListener {
            mCategoria = "Accidente-trancón"
            bottomSheetDialog.hide()
            validarSiConoceComoReportar()
        }
        bottomSheetView.optPeligrosa.setOnClickListener {
            mCategoria = "Zona Peligrosa"
            bottomSheetDialog.hide()
            validarSiConoceComoReportar()
        }
        bottomSheetView.optCamara.setOnClickListener {
            mCategoria = "Cámara"
            bottomSheetDialog.hide()
            validarSiConoceComoReportar()
        }
        bottomSheetView.optReten.setOnClickListener {
            mCategoria = "Retén"
            bottomSheetDialog.hide()
            validarSiConoceComoReportar()
        }
        bottomSheetView.PedirInfo.setOnClickListener {
            val texto = "¿Desea crear una consulta?"
            val lot = "question.json"
            val tema = "consulta"
            bottomSheetDialog.hide()
            mensajePersonalizado(tema, texto, lot)

        }


    }

    fun validarSiConoceComoReportar() {
        framePosicionMarker.visibility = View.VISIBLE
        btnSeleccionar.visibility = View.VISIBLE
        btnCancelar.visibility = View.VISIBLE
        if (PreferenciasGenerales(context!!).getpreferenceUbicacionReporte() == "true") {

        } else {
            val titulo = "Reportar en el mapa"
            val descripcion =
                "Deslízate por el mapa y encuentra donde vas a generar el reporte.\nSé lo mas preciso posible."
            visitaGuiadaConsultaMapa(titulo, descripcion)
        }
    }

    public fun alertDialogOpcionesMapa() {
        val alertDialog: androidx.appcompat.app.AlertDialog
        val builder: androidx.appcompat.app.AlertDialog.Builder =
            androidx.appcompat.app.AlertDialog.Builder(requireContext())
        // Pasar nulo como vista principal porque va en el diseño del diálogo
        val v: View =
            LayoutInflater.from(context).inflate(R.layout.cuadro_mapa_consulta_opciones, null)

        builder.setView(v)

        builder.setCancelable(true)
        alertDialog = builder.create()
        alertDialog.show()
        v.setting.setOnClickListener {
            alertDialog.dismiss()
            alertDialogSetting()

        }
        val adRequest: AdRequest = AdRequest.Builder().build()
        val bannerventanaConsulta = v!!.findViewById<AdView>(R.id.adsMapaConsulta)
        bannerventanaConsulta.loadAd(adRequest)

        v.btnpreguntar.setOnClickListener {
            tipoDeReporteOConsulta = "consulta"
            val texto = "¿Desea crear una consulta?"
            val lot = "question.json"
            val tema = "consulta"
            alertDialog.dismiss()
            mensajePersonalizado(tema, texto, lot)

        }
        v.abrirMenu.setOnClickListener {
            tipoDeReporteOConsulta = "Reporte"
            alertDialog.dismiss()
            abrirOpciones()
            btnSeleccionar.visibility = View.GONE
            btnCancelar.visibility = View.GONE
        }

    }

    @SuppressLint("LogNotTimber")
    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    private fun getUsuario(key: String, location: GeoLocation) {
        Log.d("fue llamado", "$key")
        var nombre = ""
        var marker: Marker

        mUserProviders.getUsuario(key).addOnSuccessListener { it2 ->

            if (it2.exists()) {
                if (it2.contains("nombre")) {
                    nombre = it2.get("nombre").toString()

                    Log.d("creado10", "${mUsersMarkers.toString()}")
                    for (marker in mUsersMarkers) {
                        Log.d("creado5", "${marker.tag}")
                        if (marker.tag != null) {
                            Log.d("creado11", "${mUsersMarkers.toString()}")

                        }
                    }

                    val driverLatLng = LatLng(location.latitude, location.longitude)
                    Log.d("creado9", "key: $key uid:${mAuth.uid.toString()} ")
                    if (key != mAuth.uid.toString()) {

                        Log.d("creado7", "$nombre")
                        Log.d("creado8", "$nombre")
                        marker = mMap.addMarker(
                            MarkerOptions().position(driverLatLng).title("$nombre")
                                .anchor(0.5f, 0.5f)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car))
                        )
                        marker.tag = key
                        mUsersMarkers.add(marker)
                        mUIDuser.add(key)
                    }
                }
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.N)
    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    private fun getReportes(key: String, location: GeoLocation) {
        mReporteMapaProvider.getReporte(key).addOnSuccessListener {
            if (it.exists()) {
                if (it.contains("ciudad")) {
                    val ciudad = it.getString("ciudad")
                    if (PreferenciasGenerales(context!!).getpreferenceCiudadUsuario() == ciudad) {
                        if (it.contains("categoria")) {
                            var fecha: Long = Date().time
                            val secondsCurrent =
                                RelativeTime.RelativeTime.getSecondsCurrent(
                                    fecha,
                                    context
                                )

                            //if(isFragmenActiveFirstTime){

                            // if(secondsCurrent.toInt() in 30..33||secondsCurrent.toInt() in 55..58){
                            /* var lista: MutableList<Map<String, GeoLocation>> =
                                 arrayListOf()
                             var map = mutableMapOf(key to location)

                            // lista.clear()
                             lista.add(map)
                         Log.d("map2","${lista}")*/
                            //Log.d("getReportesActive2","${MyPositionVsReports.compareToMe( lista, mCurrentLatLng )}")
                            // }

                            var imagen =
                                BitmapDescriptorFactory.fromResource(R.drawable.cate_nada)
                            var categoria = it.getString("categoria")
                            when (categoria) {
                                "Bache" -> {
                                    imagen =
                                        BitmapDescriptorFactory.fromResource(
                                            R.drawable.cate_bache
                                        )
                                    categoria = "Bache"
                                }
                                "Especial" -> {
                                    imagen =
                                        BitmapDescriptorFactory.fromResource(
                                            R.drawable.cate_especial
                                        )
                                    categoria = "Especial"
                                }
                                "Vía cerrada" -> {
                                    imagen =
                                        BitmapDescriptorFactory.fromResource(
                                            R.drawable.cate_cerrada
                                        )
                                    categoria = "Vía cerrada"
                                }
                                "Accidente-trancón" -> {
                                    imagen =
                                        BitmapDescriptorFactory.fromResource(
                                            R.drawable.cate_accidente
                                        )
                                    categoria = "Accidente-trancón"
                                }
                                "Zona Peligrosa" -> {
                                    imagen =
                                        BitmapDescriptorFactory.fromResource(
                                            R.drawable.cate_peligro
                                        )
                                    categoria = "Zona Peligrosa"
                                }
                                "Cámara" -> {
                                    imagen =
                                        BitmapDescriptorFactory.fromResource(
                                            R.drawable.cate_camara
                                        )
                                    categoria = "Cámara"
                                }
                                "Retén" -> {
                                    /*imagen =
                                        BitmapDescriptorFactory.fromResource(
                                            R.drawable.cate_reten
                                        )*/
                                    imagen =
                                        BitmapDescriptorFactory.fromResource(
                                            R.drawable.car_police
                                        )
                                    categoria = "Retén"
                                }
                                else -> {
                                    categoria = "Reporte"
                                    imagen =
                                        BitmapDescriptorFactory.fromResource(
                                            R.drawable.cate_nada
                                        )
                                }
                            }
                            Log.d("categoria", "$categoria")
                            var reporte = LatLng(
                                location.latitude,
                                location.longitude
                            )

                            for (marker in mReporteMarkers) {
                                if (marker.tag != null) {
                                    /*  if (marker.tag.equals(key)) {
                                          Log.d("retorno", "$key")
                                          return@addOnSuccessListener
                                      } else {

                                      }*/

                                } else {
                                    Log.d("retorno", "null")
                                }
                            }
                            var marker: Marker = mMap.addMarker(
                                MarkerOptions().position(reporte)
                                    .title("$categoria")
                                    // .title("$key")
                                    .anchor(0.5f, 0.5f)
                                    //.flat(true)  // Para cambiar la orientación del marcador, configura la propiedad flat del marcador en true.
                                    .icon(imagen)
                            )
                            marker.tag = key
                            Log.d("key->", key)

                            mReporteMarkers.add(marker)

                            Log.d("agregando", "$key")


                        }

                    }


                }


            }
        }
        // }


    }


    //}


    private fun getReportesActive() {
        if (isAdded && isVisible) {
            if (linearCargando.visibility == View.VISIBLE) {
                linearCargando.visibility = View.GONE
            }


        }
//ponerlo alreves para que sea buscando primero usuario despues key

        if (!::mCurrentLatLng.isInitialized) {
            mCurrentLatLng = LatLng(
                PreferenciasGenerales(context!!).getpreferenceLat().toDouble(),
                PreferenciasGenerales(context!!).getpreferenceLon().toDouble()!!
            )
        }
        //mReporteMarkers.clear()
        mGeofireProviderReportes.getActiveReports(mCurrentLatLng)!!
            .addGeoQueryEventListener(object : GeoQueryEventListener {
                @RequiresApi(Build.VERSION_CODES.N)
                override fun onKeyEntered(key: String, location: GeoLocation) {
                    // AÑADIREMOS LOS MARCADORES DE LOS CONDUCTORES QUE SE CONECTEN EN LA APLICACION
                    //borrar primero
                    //

                    Log.d("ReporteEliminando2", "${mReporteMarkers.size}")
                    getReportes(key, location)

                    if (BanderaNotificacionReporte) {
                        var map = mutableMapOf(key to location)
                        listaMapa.add(map)
                        compareToMe(listaMapa, mCurrentLatLng)
                        listaMapa.removeAt(0)
                    }

                }

                override fun onKeyExited(key: String) {
                    Log.d("mReporteMarkers3", "$mReporteMarkers")
                    for (marker in mReporteMarkers) {
                        if (marker.tag != null) {
                            if (marker.tag.equals(key)) {
                                Log.d("borrando", "$key")
                                marker.remove()
                                mReporteMarkers.remove(marker)

                                return
                            }
                        }
                    }
                }

                override fun onKeyMoved(key: String, location: GeoLocation) {
                    // ACTUALIZAR LA POSICION DE CADA CONDUCTOR
                    Log.d("mReporteMarkers2", "$mReporteMarkers")

                    for (marker in mReporteMarkers) {
                        if (marker.tag != null) {
                            if (marker.tag.equals(key)) {
                                marker.position = LatLng(location.latitude, location.longitude)
                            }
                        }
                    }
                }

                override fun onGeoQueryReady() {

                }

                override fun onGeoQueryError(error: DatabaseError) {}
            })
    }

    private fun getUserActive() {

        val mPreferencia = Preferencias(requireContext())
        var distanciaUsuarios: Double = mPreferencia.getpreferenceUsuarios().toDouble() / 1000


        Log.d("distanciaUsuarios", "->${distanciaUsuarios}")
//Toast.makeText(context,"-> $distanciaUsuarios",Toast.LENGTH_LONG).show()
        mGeofireProvider.getActiveDrivers(mCurrentLatLng, distanciaUsuarios)!!
            .addGeoQueryEventListener(object : GeoQueryEventListener {
                override fun onKeyEntered(key: String, location: GeoLocation) {
                    getUsuario(key, location)
                    Log.d("creado4", "$key")
                    Log.d("getUserActive2", "$key")
                }

                override fun onKeyExited(key: String) {
                    for (marker in mUsersMarkers) {
                        if (marker.tag != null) {
                            if (marker.tag.equals(key)) {
                                marker.remove()
                                mUsersMarkers.remove(marker)
                                Log.d("creado6", "$key")
                                return
                            }
                        }
                    }
                }

                override fun onKeyMoved(key: String, location: GeoLocation) {
                    // ACTUALIZAR LA POSICION DE CADA CONDUCTOR
                    for (marker in mUsersMarkers) {
                        if (marker.tag != null) {
                            if (marker.tag.equals(key)) {
                                marker.position = LatLng(location.latitude, location.longitude)
                            }
                        }
                    }
                }

                override fun onGeoQueryReady() {}
                override fun onGeoQueryError(error: DatabaseError) {}
            })
    }

    private fun existeSesion(): Boolean {
        var existe: Boolean = false
        if (mAuth.currentUser != null) {
            existe = true
        }
        return existe
    }

    private fun desconectar() {
        if (mFusedLocation != null) {
            mFusedLocation.removeLocationUpdates(mLocationCallback)
            if (existeSesion()) {
                mGeofireProvider.removeLocation(mAuth.uid)
            }

        }
    }

    override fun onStart() {
        super.onStart()
        /*   val handler = Handler(Looper.myLooper()!!)
           handler.postDelayed(object : Runnable {
               override fun run() {
                   Log.d("desconectandoxx", "$isFragmenActiveFirstTime")
                   isFragmenActiveFirstTime = !isFragmenActiveFirstTime
                   UbicacionesUtil.setLatLng(mMap.cameraPosition.target)

                   //mFragmentOne.getDistanceViewPost(LatLng(milatitud!!,milongitud!!))
                  *//* if (::mCurrentLatLng.isInitialized) {

                    getReportesActive()
                    getLocationforUsers()
                }*//*
                handler.postDelayed(this, 10 * 1000)
            }
        }, 0)*/
    }

    override fun onResume() {
        super.onResume()


        // isFragmenActiveFirstTime = true
        // mLocationCallback


        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            mIsFirstTime = true
            if (ContextCompat.checkSelfPermission(
                    context!!,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                return
            } else {
                Log.d("primeraVezFragment4", "$isFragmenActiveFirstTime")
                mLocationRequest = LocationRequest()
                mFusedLocation.requestLocationUpdates(
                    mLocationRequest,
                    mLocationCallback,
                    Looper.myLooper()
                )
            }
        } else {
            Log.d("primeraVezFragment5", "$isFragmenActiveFirstTime")
            mLocationRequest = LocationRequest()
            mFusedLocation.requestLocationUpdates(
                mLocationRequest,
                mLocationCallback,
                Looper.myLooper()
         )
        } */

    }

    override fun onPause() {
        super.onPause()

        /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
              if (ContextCompat.checkSelfPermission(
                      context!!,
                      Manifest.permission.ACCESS_FINE_LOCATION
                  ) != PackageManager.PERMISSION_GRANTED
              ) {

                  return
              } else {
                  mFusedLocation.removeLocationUpdates(mLocationCallback)
              }
          } else {
              mFusedLocation.removeLocationUpdates(mLocationCallback)
          }*/
    }

    //  @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        Log.d("cambioz", "------")
        mMap = googleMap
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        mMap.setOnCameraIdleListener(mCameraListener)
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = false

        mLocationRequest = LocationRequest()
        mLocationRequest.interval = 1000
        mLocationRequest.fastestInterval = 1000
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.smallestDisplacement = 5f
        mMap.setOnCameraMoveListener {

            this.zoom = mMap.cameraPosition.zoom
            Log.d("cambiozzoom", "$zoom")
        }
        iniciarLocalizacion("5")
        if (ActivityCompat.checkSelfPermission(
                context!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context!!,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {


        }
        PreferenciasGenerales(context!!).setpreferenceLat("${mMap.cameraPosition.target.latitude}")
        PreferenciasGenerales(context!!).setpreferenceLon("${mMap.cameraPosition.target.longitude}")
        Log.d("yjyjyjy", "${PreferenciasGenerales(context!!).getpreferenceLat()}")


        estiloMapa(PreferenciasGenerales(context!!).getpreferenceMapa())
        posicionMarcador()
        mMap.setOnMarkerClickListener {
            //clickOnMarkerJm("${it.tag}")
            Log.d("yjyjyjydf", "${it.tag.toString()} $it")
            if (it.tag != null) {
                esReporte(it.tag.toString())
            }

            return@setOnMarkerClickListener false
        }


    }

    private var currentBearing = 0f
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        /* outState.putDouble("lat", milatitud!!)
         outState.putDouble("lon", milongitud!!)
         outState.putBoolean("notificacionReporte", BanderaNotificacionReporte)*/
    }

    public fun iniciarLocalizacion(x: String) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    context!!,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {

                linearPedirPermiso.visibility = View.GONE
                botonesFlotantes.visibility = View.VISIBLE
                linearCargando.visibility = View.VISIBLE
                if (gpsActived()) {

                    mMap.isMyLocationEnabled = false
                    mFusedLocation.requestLocationUpdates(
                        mLocationRequest,
                        mLocationCallback,
                        Looper.myLooper()
                    )
                    //mostrar lastUbication
                    if (mFusedLocation.lastLocation.isSuccessful) {

                        mFusedLocation.lastLocation.addOnSuccessListener {

                            if (it.latitude != null) {
                                mMap.moveCamera(
                                    CameraUpdateFactory.newLatLngZoom(
                                        LatLng(
                                            it.latitude,
                                            it.longitude
                                        ), 18f
                                    )
                                )
                                //guardar en static
                                /* UbicacionesUtil.setLatLng(mMarker!!.position)*/

                                // mFragmentOne.getDistanceViewPost(LatLng(it.latitude,it.longitude))
                            } else {

                            }


                        }
                    } else {
                        Log.d("milatitud3", "No hay posicion")
                    }


                } else {
                    showAlertDialogNOGPS();
                }
            } else {

                checkLocationPermissions();
            }
        } else {
            if (gpsActived()) {
                mFusedLocation.requestLocationUpdates(
                    mLocationRequest,
                    mLocationCallback,
                    Looper.myLooper()
                )
                mMap.isMyLocationEnabled = false
            } else {
                showAlertDialogNOGPS();
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_REQUEST_CODE) {

            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                if (ContextCompat.checkSelfPermission(
                        this.context!!,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    if (gpsActived()) {

                        mFusedLocation.requestLocationUpdates(
                            mLocationRequest,
                            mLocationCallback,
                            Looper.myLooper()
                        )

                    } else {
                        showAlertDialogNOGPS()
                    }
                } else {
                    checkLocationPermissions()
                }
            } else {

                checkLocationPermissions()
            }
        }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("verificando per", "$requestCode")
        if (requestCode == SETTINGS_REQUEST_CODE && gpsActived()) {
            if (gpsActived()) {
                if (ActivityCompat.checkSelfPermission(
                        this.context!!,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this.context!!,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }

                mFusedLocation.requestLocationUpdates(
                    mLocationRequest,
                    mLocationCallback,
                    Looper.myLooper()
                )

                mMap.isMyLocationEnabled = false
            } else {
                showAlertDialogNOGPS()
            }

        } else if (requestCode == SETTINGS_REQUEST_CODE && !gpsActived()) {
            showAlertDialogNOGPS()
        }

        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == 100) {
            mReturnValues = data?.getStringArrayListExtra(Pix.IMAGE_RESULTS)!!
            mImageFile = File(mReturnValues[0])
            mView.verImagen.visibility = View.VISIBLE
            mView.verImagen.scaleType = ImageView.ScaleType.FIT_CENTER
            mView.verImagen.scaleType = ImageView.ScaleType.FIT_CENTER
            if (ExtensionFile.isVideoFile(mImageFile?.absolutePath)) {
                /* mView.verImagen.visibility = View.GONE
                 mView.frameVideoPost.visibility = View.VISIBLE
                 val uri = Uri.fromFile(File(mImageFile?.absolutePath))
                 mView.videoViewPost.setVideoURI(uri)

                 var mediaController = MediaController(context)
                 mView.videoViewPost.setMediaController(mediaController)
                 mediaController.setAnchorView(mView.videoViewPost)*/

                //Log.d("mostrandoVideo", "${uri}")

            } else if (ExtensionFile.isImageFile(mImageFile?.absolutePath)) {
                mView.fotoReportePost.visibility = View.VISIBLE
                //mView.frameVideoPost.visibility = View.GONE


                mView.verImagen.setImageBitmap(BitmapFactory.decodeFile(mImageFile?.absolutePath))

            }
        }


/* El result para las imagenes*/
        /* if (requestCode == PHOTO_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
             Picasso.get().load(mPhotoPath).error(R.drawable.ic_cam)
                 .into(vista.fotoReportePost)
         }*/

/*Selecionar desde la galería*/
        /*if (requestCode == GALLERY_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            try {
                data?.data?.let { data ->
                    mFile = FileUtil.from(context!!, data)
                    vista.fotoReportePost.scaleType = ImageView.ScaleType.CENTER_CROP
                    vista.fotoReportePost.setImageBitmap(BitmapFactory.decodeFile(mFile!!.absolutePath))

                }

            } catch (e: Exception) {
                Log.d("error", "se  produjo un error")
                Toast.makeText(context, "Se produjo un error", Toast.LENGTH_LONG).show()
            }
        }*/


    }

    private fun validarPermiso(): Boolean {
        var permiso = true
        if (ContextCompat.checkSelfPermission(
                this.context!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permiso = false
        }
        return permiso
    }

    public fun checkLocationPermissions() {
        val permiso = validarPermiso()
        Log.d("mostrando2", "dsada")
        if (!permiso) {
            Log.d("viendo4", "dsada")
            linearPedirPermiso.visibility = View.VISIBLE
            botonesFlotantes.visibility = View.GONE
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this.activity!!,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                linearCargando.visibility = View.GONE
                mensajePersonalizadoGPS("permiso")

            } else {
                mensajePersonalizadoGPS("permiso")
                /* ActivityCompat.requestPermissions(
                     this.activity!!,
                     arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                     LOCATION_REQUEST_CODE
                 )*/
            }

        } else {
            // Handler().postDelayed({
            iniciarLocalizacion("d")
            Log.d("viendo3", "dsada")

            // },2000)
        }
    }

    public fun gpsActived(): Boolean {
        var isActive = false
        val locationManager =
            context!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            isActive = true
        }
        Log.d("isActive->", "$isActive")
        return isActive
    }

    private fun showAlertDialogNOGPS() {
        linearPedirPermiso.visibility = View.VISIBLE
        botonesFlotantes.visibility = View.GONE
        linearCargando.visibility = View.GONE
        mensajePersonalizadoGPS("gps")
        /* val alerta = AlertDialog.Builder(this.context)
             .setTitle("Activar Ubicación")
             .setMessage("Driving necesita activación del GPS para ofrecerte una mejor experiencia")
             .setIcon(R.mipmap.launcher)
             .setPositiveButton("Ok") { dialog: DialogInterface?, which: Int ->

                 startActivity(Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                 dialog!!.dismiss()
                 ActivityCompat.requestPermissions(
                     this.activity!!,
                     arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                     LOCATION_REQUEST_CODE
                 )


             }
         alerta.show()*/
        Log.d("showAlertDialogNOGPS", "entrando a vacío")


    }
///////////////////////////////////////////////////Permisos arriba///////////////////////////////////////


    private fun esReporte(key: String) {
        val bottomSheetDialog = BottomSheetDialog(context!!, R.style.BottomSheetDialogTheme)
        val bottomSheetView = LayoutInflater.from(context)
            .inflate(R.layout.cuadro_datos_reporte, bottom_Sheet_reporte)
/* val intent = Intent(context, ChatActivity::class.java)
         intent.putExtra("user2", uid)
         intent.putExtra("user1", mAuth.currentUser.uid)
         intent.putExtra("idChat", mAuth.currentUser.uid + uid)
         intent.putExtra("estadoPropina", "false")
         this.context?.startActivity(intent)*/

        mReporteMapaProvider.getReporte(key).addOnSuccessListener { it3 ->
            if (it3.exists()) {
                if (it3.contains("uid")) {
                    val otroUser = it3.getString("uid").toString()
                    mUserProviders.getUsuario(otroUser)
                        .addOnSuccessListener {

                            if (it.contains("nombre")) {
                                bottomSheetView.nombre.text = it.getString("nombre")
                            }

                            if (it.contains("photo")) {
                                var imagenPerfil: String = it.get("photo").toString()
                                if (it.get("photo").toString() == "") {
                                } else {
                                    Picasso.get().load(imagenPerfil)
                                        .error(R.drawable.ic_person_azul)
                                        .into(bottomSheetView.foto)
                                }
                            }
                        }
                    getDAtosReportes(otroUser, key, it3, bottomSheetDialog, bottomSheetView)
                }
            }
        }
    }

    private fun getDAtosReportes(
        otroUser: String,
        key: String,
        it3: DocumentSnapshot,
        bottomSheetDialog: BottomSheetDialog,
        bottomSheetView: View
    ) {
        Log.d("MYUID 1", "${mAuth.uid}")
        Log.d("MYUID 2", "$otroUser")
        if (otroUser == mAuth.uid) {
            Log.d("MYUID 3", "$otroUser")
            bottomSheetView.linearCalificar.visibility = View.GONE
            bottomSheetView.linearYaCalificaste.visibility = View.GONE
            bottomSheetView.linearDeleteReport.visibility = View.VISIBLE
            //referenciasGenerales(context!!).getpreferenceGuiaSoyModerador()=="true"
        } else {
            Log.d("MYUID 4", "$otroUser")
            mRaitingProvider.getRaitingByReporteAndUser(key, mAuth.uid.toString()).get()
                .addOnSuccessListener {
                    if (it.size() > 0) {
                        bottomSheetView.linearCalificar.visibility = View.GONE
                        bottomSheetView.linearYaCalificaste.visibility = View.VISIBLE
                        //ya califiaste
                    }
                }
        }
        if (PreferenciasGenerales(context!!).getpreferenceGuiaSoyModerador() == "true") {

            bottomSheetView.linearCalificar.visibility = View.VISIBLE
            bottomSheetView.linearYaCalificaste.visibility = View.VISIBLE
            bottomSheetView.linearDeleteReport.visibility = View.VISIBLE
            mRaitingProvider.getRaitingByReporteAndUser(key, mAuth.uid.toString()).get()
                .addOnSuccessListener {
                    if (it.size() > 0) {
                        bottomSheetView.linearCalificar.visibility = View.GONE
                        bottomSheetView.linearYaCalificaste.visibility = View.VISIBLE
                        //ya califiaste
                    } else {
                        bottomSheetView.linearCalificar.visibility = View.VISIBLE
                        bottomSheetView.linearYaCalificaste.visibility = View.GONE
                    }
                }

        }

        mReporteMapaProvider.getReporte(key).addOnSuccessListener {
            if (it.contains("raitingTotal")) {
                if (it.contains("npersonasRating")) {
                    val raitingTotal: Float = it.getString("raitingTotal")!!.toFloat()
                    val npersonasRating: Int = it.getString("npersonasRating")!!.toInt()
                    if (npersonasRating >= 10) {
                        val porcentaje: Int = ((raitingTotal / (npersonasRating * 5)) * 100).toInt()

                        if (porcentaje < 10) {
                            borrarReporte(key)
                            bottomSheetView.linearCalificar.visibility = View.GONE
                            bottomSheetView.linearYaCalificaste.visibility = View.VISIBLE
                            bottomSheetView.calificaste.setTextColor(
                                ContextCompat.getColor(
                                    context!!,
                                    R.color.red
                                )
                            )

                            bottomSheetView.calificaste.text = "ReporteEliminado"
                        }

                        if (porcentaje > 10 && porcentaje <= 30) {
                            bottomSheetView.rating.text = "$porcentaje % No confiable"
                            bottomSheetView.rating.setTextColor(
                                ContextCompat.getColor(
                                    context!!,
                                    R.color.red
                                )
                            )
                        }
                        if (porcentaje in 31..59) {
                            bottomSheetView.rating.text = "$porcentaje % Poco confiable"
                            bottomSheetView.rating.setTextColor(
                                ContextCompat.getColor(
                                    context!!,
                                    R.color.salmon
                                )
                            )
                        }
                        if (porcentaje in 60..100) {
                            bottomSheetView.rating.text = "$porcentaje %confiable"

                            bottomSheetView.rating.setTextColor(
                                ContextCompat.getColor(
                                    context!!,
                                    R.color.verde
                                )
                            );
                        }
                    } else {
                        /////////////////////////////////////////

                        //bottomSheetView.rating.text = "$porcentaje % No confiable"
                        ///////////////////////////////////////////////////////////


                        bottomSheetView.rating.text = "Reporte en prueba $npersonasRating/10"
                    }
                    Log.d("raitingTotal1", "$raitingTotal")
                    Log.d("raitingTotal2", "$npersonasRating")
                    if (it.contains("categoria")) {
                        bottomSheetView.categoria.text = it.getString("categoria")
                    }
                    if (it.contains("fecha")) {
                        val fecha: Long = it.get("fecha") as Long
                        val mRelativeTime: String? =
                            RelativeTime.RelativeTime.getTimeAgo(fecha, context!!)
                        bottomSheetView.relativeTime.text = mRelativeTime
                    }
                    if (it.contains("descripcion")) {
                        bottomSheetView.descripcionReporte.text = it.getString("descripcion")
                    }
                    if (it.contains("foto")) {
                        var imagen: String = it.get("foto").toString()
                        if (it.get("foto").toString() == "") {
/*
            Picasso.get().load(R.drawable.ic_person_azul)
               .error(R.drawable.ic_person_azul).into(bottomSheetView.fotoReporte)
*/
                        } else {
                            Picasso.get().load(imagen)
                                .error(R.drawable.ic_person_azul)
                                .into(bottomSheetView.fotoReporte)
                        }
                    }
                }
            }
        }

        var raitingBar = bottomSheetView.ratingBarCalification.rating
        Log.d("raitingTotal3", "$raitingBar")
        bottomSheetView.ratingBarCalification.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            raitingBar = rating
            Log.d("raitingTotal4", "$raitingBar")
        }
        bottomSheetView.btnCalificar.setOnClickListener {
            //guardar raiting
            Log.d("raitingTotal5", "$raitingBar")
            bottomSheetDialog.dismiss()
            val check = R.layout.cuadro_mensaje_check
            mensajeSuccess(check)
            saveRaiting(bottomSheetView, raitingBar, key)
        }
        bottomSheetView.foto.setOnClickListener {
            if (key.isNotEmpty()) {
                Log.d("key...", "$otroUser")
                val intent = Intent(context, PerfilActivityPost::class.java)
                intent.putExtra("uid", otroUser)
                this.context?.startActivity(intent)
            }
        }
        bottomSheetView.linearDeleteReport.setOnClickListener {

            borrarReporte(key)
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()
        val adRequest: AdRequest = AdRequest.Builder().build()
        val bannerventanaConsulta =
            bottomSheetDialog.findViewById<AdView>(R.id.cuadro_datos_reporteAds)
        bannerventanaConsulta!!.loadAd(adRequest)
    }

    private fun borrarReporte(key: String) {
        val borrar = R.layout.cuadro_eliminar_reporte

        mReporteMapaProvider.deleteReporte(key).addOnCompleteListener {
            if (it.isSuccessful) {
                mensajeSuccess(borrar)
                mGeofireProviderReportes.removeReporte(key)
                for (marker in mReporteMarkers) {
                    if (marker.tag != null) {
                        if (marker.tag.equals(key)) {
                            Log.d("borrando", "$key")
                            marker.remove()
                            mReporteMarkers.remove(marker)
                            return@addOnCompleteListener
                        }
                    }
                }

                //getReportesActive()

                Log.d("Reporte", "Eliminado")
            }
        }

    }

    private fun mensajeSuccess(cuadro: Int) {
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
/*v.cerrarcuadroLikes.setOnClickListener {
    alertDialog.dismiss()
}*/
    }

    fun visitaGuiadaConsultaMapa(titulo: String, descripcion: String) {
        Log.d("tatatat", "ssdasdasd")
        TapTargetView.showFor(
            activity,
            TapTarget.forView(
                imagenCentroMapa,
                titulo,
                descripcion
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
                    PreferenciasGenerales(context!!).setpreferenceUbicacionReporte("true")

                }
            }
        )

    }

    private fun mensajePersonalizado(tema: String, texto: String, lot: String) {

        val alertDialog: androidx.appcompat.app.AlertDialog
        val builder: androidx.appcompat.app.AlertDialog.Builder =
            androidx.appcompat.app.AlertDialog.Builder(context!!)
        val v: View = LayoutInflater.from(context).inflate(R.layout.cuadro_preguntar, null)
        builder.setView(v)
        v.textVie.text = texto
        v.lottieAnimationViewModoAlerta.setAnimation(lot)
        builder.setCancelable(true)
        alertDialog = builder.create()
        alertDialog.show()
if (tema=="reporte"){
    v.negativo.visibility=View.GONE
    v.afirmativo.text="Listo!"
}
        v.afirmativo.setOnClickListener {


            if (tema == "consulta") {
                alertDialog.dismiss()
                mostrarDondePreguntar()
            }


        }
        v.negativo.setOnClickListener {
            alertDialog.dismiss()
        }
    }

    private fun saveRaiting(bottomSheetView: View, raitingBar: Float, key: String) {
//antes debo averiguar sii existe .get() para ocultar si ya califiqué
        mReporteMapaProvider.getReporte(key).addOnSuccessListener { it1 ->
            if (it1.contains("raitingTotal")) {
                if (it1.contains("npersonasRating")) {
                    val raitingTotal: Float = it1.getString("raitingTotal")!!.toFloat()
                    val npersonasRating: Int = it1.getString("npersonasRating")!!.toInt()
                    val nuevoRaiting = raitingTotal + raitingBar
                    val nuevaPersona = npersonasRating + 1
                    Log.d("raitingTotal6", "$nuevoRaiting")
                    Log.d("raitingTotal7", "$nuevaPersona")
                    mReporteEnMapa.nPersonasRating = nuevaPersona.toString()
                    mReporteEnMapa.raitingTotal = nuevoRaiting.toString()
                    mReporteEnMapa.idReporte = key
                    mReporteMapaProvider.upDateReporte(mReporteEnMapa)
                        .addOnCompleteListener { it2 ->
                            if (it2.isSuccessful) {
                                //actualizado guarda el raiting
                                Log.d("raitingTotal8", "actualizó en reporte")
                                mRaiting.idReporte = key
                                mRaiting.uid = mAuth.uid.toString()
                                mRaiting.raiting = raitingBar.toString()
                                mRaitingProvider.nuevoRaiting(mRaiting).addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        Log.d("raitingTotal9", "guardó en raiting")
                                        //guardado ocultar
                                        Log.d("guardado", "1")
                                        //bottomSheetView.linearCalificar.visibility=View.GONE
                                    } else {
                                        //error al guardar
                                    }
                                }

                            } else {
                                //no se actualizó
                            }
                        }

                }
            }
        }
    }


    private fun alertDialog() {

        val alertDialog: androidx.appcompat.app.AlertDialog
        val builder: androidx.appcompat.app.AlertDialog.Builder =
            androidx.appcompat.app.AlertDialog.Builder(
                context!!
            )
        val v: View = LayoutInflater.from(context).inflate(R.layout.cuadro_reporte_mapa, null)
        builder.setView(v)
        builder.setCancelable(false)
        alertDialog = builder.create()
        alertDialog.show()

        v.cerrar.setOnClickListener {
            alertDialog.dismiss()
            framePosicionMarker.visibility = View.GONE
        }
        v.btnDetallado.setOnClickListener {
            v.reporteDetallado.visibility = View.VISIBLE
            v.botonesreportes.visibility = View.GONE
            v.checkbox_post.isChecked = true
        }

        v.btnCrearReporte.setOnClickListener {
            alertDialog.dismiss()
            clickPost(v, alertDialog)
            framePosicionMarker.visibility = View.GONE
            PreferenciasGenerales(context!!).setpreferencePrimerReporte("${100}")
        }
//si no esta creada la pos va sa lir null onCameraMove()
        v.reporteRapido.setOnClickListener {
            alertDialog.dismiss()
            guardarDescripcion("", v, "", "")
            PreferenciasGenerales(context!!).setpreferencePrimerReporte("${100}")
            framePosicionMarker.visibility = View.GONE
        }

        v.fotoReportePost.setOnClickListener {
            mView = v
            startPix()

        }
    }

    private fun alertDialogPropinas() {
        val alertDialog: androidx.appcompat.app.AlertDialog
        val builder: androidx.appcompat.app.AlertDialog.Builder =
            androidx.appcompat.app.AlertDialog.Builder(context!!)
        val v: View = LayoutInflater.from(context).inflate(R.layout.cuadro_propina_mapa, null)

        builder.setView(v)
        getUsuario(v)
        builder.setCancelable(false)

        alertDialog = builder.create()
        alertDialog.show()

        val adRequest: AdRequest = AdRequest.Builder().build()
        val bannerventanaConsulta = alertDialog!!.findViewById<AdView>(R.id.cuadro_propina_mapa)
        bannerventanaConsulta!!.loadAd(adRequest)

        v.linearLikePropina.setOnClickListener {
            v.NmegustaPropina.text = "${alertDialog.NmegustaPropina.text.toString().toInt() + 1}"

        }
        v.cerrarCuadro.setOnClickListener {
            alertDialog.dismiss()
            framePosicionMarker.visibility = View.GONE
            btnSeleccionar.visibility = View.GONE
            btnCancelar.visibility = View.GONE
        }
        v.btnpublicar.setOnClickListener {

            if (v.txtDescripcion.text.toString() == "") {
                Toast.makeText(context, "No hay descripcion", Toast.LENGTH_LONG).show()
            } else {

                alertDialog.dismiss()
                guardarPropina(v)
                tipoDeReporteOConsulta = ""
                //mensajeSuccess()
            }
            framePosicionMarker.visibility = View.GONE
        }


/*  malertDialog = alertDialog
  v.cerrarPoster.setOnClickListener {
      alertDialog.dismiss()
  }*/
    }

    private fun guardarPropina(v: View) {
        val lat = mReporteLatLng.latitude.toString()
        val lon = mReporteLatLng.longitude.toString()
        var mPropinas = Propinas()
        mPropinas.descripcion = v.txtDescripcion.text.toString()
        mPropinas.uid = mAuth.uid.toString()
        mPropinas.monedas = v.NmegustaPropina.text.toString()
        mPropinas.idAporte = ""
        mPropinas.ciudad = PreferenciasGenerales(context!!).getpreferenceCiudadUsuario()
        mPropinas.fecha = Date().time
        mPropinas.Lat = lat
        mPropinas.Lon = lon
        mPropinasProvider.save(mPropinas).addOnCompleteListener {
            if (it.isSuccessful) {

                Toast.makeText(context, "Publicación exitosa", Toast.LENGTH_SHORT).show()
                sendNotificationEventosGrupo(v.txtDescripcion.text.toString())
            } else {
                Toast.makeText(
                    context,
                    "No Se pudo publicar, verifique su conexión",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    private fun getUsuario(v: View) {
        Log.d("getUser", "v")
        mUserProviders.getUsuario(mAuth.uid.toString()).addOnSuccessListener {
            if (it.exists()) {
                if (it.contains("nombre")) {
                    v.nombrePost.setText("${it.get("nombre")}")
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

    @RequiresApi(Build.VERSION_CODES.M)
    private fun alertDialogSetting() {
        val alertDialog: androidx.appcompat.app.AlertDialog
        val builder: androidx.appcompat.app.AlertDialog.Builder =
            androidx.appcompat.app.AlertDialog.Builder(
                context!!
            )
        val v: View = LayoutInflater.from(context).inflate(R.layout.cuadro_opciones_mapa, null)
        builder.setView(v)

        builder.setCancelable(true)
        alertDialog = builder.create()
        alertDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

        alertDialog.show()
        val adRequest: AdRequest = AdRequest.Builder().build()
        v.adsOpcionesMapa.loadAd(adRequest)



        v.imagenmodoviaje.setOnClickListener {
            Toast.makeText(
                context!!,
                "Pronto presentaremos esta y otras opciones",
                Toast.LENGTH_LONG
            ).show()
        }
        v.imagenmodoruta.setOnClickListener {
            Toast.makeText(
                context!!,
                "Pronto presentaremos esta y otras opciones",
                Toast.LENGTH_LONG
            ).show()
        }
        v.optPeligrosa.setOnClickListener {
            Toast.makeText(
                context!!,
                "Pronto presentaremos esta y otras opciones",
                Toast.LENGTH_LONG
            ).show()
        }
        v.estiloMapaAuto.setOnClickListener {
            Toast.makeText(
                context!!,
                "Pronto presentaremos esta y otras opciones",
                Toast.LENGTH_LONG
            ).show()
        }

        v.estiloMapaDia.setOnClickListener {
            estiloMapa("dia")
            v.mapadia.borderWidth = 8f
            v.mapadia.borderColor = requireContext()!!.getColor(R.color.colorPrimary)
            v.mapaNoche.borderWidth = 2f
            v.mapaNoche.borderColor = context!!.getColor(R.color.grisClaro)
            v.mapaAuto.borderWidth = 2f
            v.mapaAuto.borderColor = context!!.getColor(R.color.grisClaro)
        }
        v.estiloMapaNoche.setOnClickListener {
            estiloMapa("noche")
            v.mapaNoche.borderWidth = 8f
            v.mapaNoche.borderColor = context!!.getColor(R.color.colorPrimary)
            v.mapadia.borderWidth = 2f
            v.mapadia.borderColor = context!!.getColor(R.color.grisClaro)
            v.mapaAuto.borderWidth = 2f
            v.mapaAuto.borderColor = context!!.getColor(R.color.grisClaro)
        }
        v.estiloMapaAuto.setOnClickListener {
            getHoraActual()
            v.mapaAuto.borderWidth = 8f
            v.mapaAuto.borderColor = context!!.getColor(R.color.colorPrimary)
            v.mapadia.borderWidth = 2f
            v.mapadia.borderColor = context!!.getColor(R.color.grisClaro)
            v.mapaNoche.borderWidth = 2f
            v.mapaNoche.borderColor = context!!.getColor(R.color.grisClaro)
        }

    }

    private fun estiloMapa(estilo: String) {


        var estilomapa = R.raw.map_style_oscuro

        if (PreferenciasGenerales(context!!).getpreferenceMapa() == "dia") {
            estilomapa = R.raw.map_style_dia
        } else if (estilo == "noche") {
            estilomapa = R.raw.map_style_oscuro
        }
        PreferenciasGenerales(context!!).setpreferenceMapa(estilo)


        try {
            val success: Boolean = mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    context, estilomapa
                )
            );


        } catch (e: Resources.NotFoundException) {
            Log.e("Can't find style.  ", "$e");
        }
    }

    private fun getHoraActual() {
        var estilo = ""
        var fecha: Long = Date().time

        val time = RelativeTime.RelativeTime.timeFormat24H(fecha, context)
        if (time != null) {
            if (time > "06:00 a. m." && time < "18:00 p. m.") {
                estilo = "dia"
                Log.d("fechaAhora 1", "$time")
            } else if (time > "18:00 p. m." && time < "06:00 a. m.") {
                estilo = "noche"
                Log.d("fechaAhora 2", "$time")
            }
            estiloMapa(estilo)
        }

    }

    private fun selecionarOpciones(GALLERY_REQUEST_CODE: Int) {
        mBuilderSelector.setItems(mOptions) { dialog, i ->
            if (i == 0) {
                openGalery(GALLERY_REQUEST_CODE)
            } else {
                if (i == 1) {
                    tomarFoto()
                }
            }
        }
        mBuilderSelector.show()
    }

    private fun openGalery(GALLERY_REQUEST_CODE: Int) {
        val galeria = Intent(Intent.ACTION_GET_CONTENT)

        galeria.type = "image/*"
        startActivityForResult(galeria, GALLERY_REQUEST_CODE)

    }

    private fun tomarFoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(context!!.packageManager) != null) {
            var photoFile: File? = null
            try {
                photoFile = createPhotoFile()

                if (photoFile != null) {
                    mPhotoFile = photoFile
                    var photoUri: Uri = FileProvider.getUriForFile(
                        context!!,
                        "com.example.driving.activities",
                        photoFile
                    )
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                    startActivityForResult(intent, PHOTO_REQUEST_CODE)

                }
            } catch (e: java.lang.Exception) {
                Toast.makeText(context, "Hubo un error ${e.message}", Toast.LENGTH_SHORT).show()
                println("errror del archivo ${e.message}")
            }


        }
    }

    private fun createPhotoFile(): File? {
        var storageDir = context!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        var photoFile: File = File.createTempFile("${Date()}_photo", ".jpg", storageDir)
        mPhotoPath = "file:${photoFile.absolutePath}"
        mAbsolutePhotoPath = photoFile.absolutePath
        mPhotoFile = photoFile
        return photoFile
    }

    private fun onCameraMove() {

        try {
            val geocoder = Geocoder(context!!)
            var mOriginLatLng = mMap.cameraPosition.target
            val addressList: List<Address> =
                geocoder.getFromLocation(
                    mOriginLatLng.latitude,
                    mOriginLatLng.longitude,
                    1
                ) as List<Address>

            mReporteLatLng = LatLng(mOriginLatLng.latitude, mOriginLatLng.longitude)
            //if (mReporteEnMapa != null) {
            Log.d("tipoDeReporteOConsulta2", "$tipoDeReporteOConsulta")

            if (tipoDeReporteOConsulta == "Reporte") {
                alertDialog()
            } else
                if (tipoDeReporteOConsulta == "consulta") {
                    alertDialogPropinas()
                }
            // } else {
            //Toast.makeText(context, "espere, cargando coordenadas", Toast.LENGTH_LONG).show()
            // }

        } catch (e: Exception) {
            Log.d("->: ", "Mensaje error: " + e.message)
        }

    }

    private fun getLocationforUsers() {
        Log.d("ciudadciudad11", "ciudad")
        try {
            val geocoder = Geocoder(context!!)
            var mOriginLatLng = mMarker!!.position
            val addressList: List<Address> =
                geocoder.getFromLocation(
                    mOriginLatLng.latitude,
                    mOriginLatLng.longitude,
                    1
                ) as List<Address>
            if (PreferenciasGenerales(context!!).getpreferenceCiudadUsuario() == "") {
                ActualizarCiudad(addressList)
                Log.d("vaciart", "llenando")
                // PreferenciasGenerales.setCiudades(addressList)
            } else {
                PreferenciasGenerales.setCiudades(addressList)

            }


        } catch (e: Exception) {
            Log.d("No ha cargado ", "Mensaje error2: " + e.message)
        }
    }

    private fun ActualizarCiudad(ciudad: List<Address>) {
        var mListaCiudades: MutableList<String>? = arrayListOf()


        Log.d("ListaNoVacia4", "${ciudad}")

        if (!ciudad[0].locality.isNullOrBlank()) {
            mListaCiudades?.add(ciudad[0].locality)

        } else if (!ciudad[0].subAdminArea.isNullOrBlank()) {
            mListaCiudades?.add(ciudad[0].subAdminArea)
        } else if (!ciudad[0].adminArea.isNullOrBlank()) {
            mListaCiudades?.add(ciudad[0].adminArea)

        }

        val alertDialog: androidx.appcompat.app.AlertDialog
        val builder: androidx.appcompat.app.AlertDialog.Builder =
            androidx.appcompat.app.AlertDialog.Builder(context!!)
        val v: View = LayoutInflater.from(context).inflate(R.layout.cuadro_actualizar_ciudad, null)
        builder.setView(v)
        builder.setCancelable(false)
        alertDialog = builder.create()
        //val ciudades = Ciudades.getCiudades()
        val adapter = ArrayAdapter<String>(
            context!!,
            R.layout.list_item_ciudad,
            mListaCiudades!!
        )
        v.listaCiudades.adapter = adapter
        alertDialog.show()

        v.listaCiudades.setOnItemClickListener { parent, view, position, id ->
            //actualizarPerfil(parent.getItemAtPosition(position), alertDialog, mView)
            PreferenciasGenerales(context!!).setpreferenceCiudadUsuario(
                parent.getItemAtPosition(
                    position
                ).toString()
            )

            val users = mAuth.currentUser
            if (users != null) {
                val user = Usuarios()
                user.uid = mAuth.uid.toString()
                user.ciudad = parent.getItemAtPosition(
                    position
                ).toString()
                mUserProviders.upDatePerfil(user).addOnCompleteListener {
                    if (it.isSuccessful) {

                    }
                }
            }

            alertDialog.dismiss()
            val intent = Intent(context!!, SplashLoading::class.java)
            startActivity(intent)
            //
        }
        v.cancelar.visibility = View.GONE

    }


    private fun clickPost(v: View, alertDialog: androidx.appcompat.app.AlertDialog) {
        if (mImageFile != null) {
            Toast.makeText(requireContext(), " guardar imagen", Toast.LENGTH_SHORT).show()
            alertDialog.dismiss()
            //  mensajePersonalizado()
            saveImage(v)


        }
        /*  Log.d("Clic", " $")
          if (mFile != null) {
              print("imagen ")
              saveImage(mFile!!, v)
          } else if (mPhotoFile != null) {
              print("foto ")
              saveImage(mPhotoFile!!, v)
          } else {
              Log.d("foto nula", "$mFile $mPhotoFile")
              guardarDescripcion("", v)
          }*/

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
                                guardarDescripcion(urlPost, v, type, nameFile)

                            }
                        } else {
                            //No se pudo guardar
                        }
                    }
            }
            thread.start()
            // mAlertDialog.dismiss()
        }


        /* val nombre = "${Date().time}.jpg"
         mImageProvider.save(v.context!!, tipodefoto, nombre).addOnCompleteListener { it0 ->
             if (it0.isSuccessful) {
                 mImageProvider.getStorage().downloadUrl.addOnSuccessListener { uri ->
                     var url = uri.toString()
                     guardarDescripcion(url, v)
                 }
             } else {
                 Toast.makeText(context, "hubo error", Toast.LENGTH_SHORT).show()
             }
         }*/
    }

    private fun crearPostIfCheckbox(url: String, descripcion: String, nameFile: String) {
        val mPostProvider = PostProvider()

        if (PreferenciasGenerales(context!!).getpreferenceGuiaSoyModerador() == "true") {

            val post = Post()

            post.imagen = url
            post.type = "imagen"
            post.nota = ""
            post.descripcion = descripcion
            post.uid = mAuth.currentUser!!.uid
            var fechalocal = Date().time
            post.fecha = fechalocal
            post.adminArea = PreferenciasGenerales(context!!).getpreferenceCiudadUsuario()
            /* post.lat = UbicacionesUtil.getLatLng().latitude.toString()
             post.lng = UbicacionesUtil.getLatLng().longitude.toString()*/
            post.nameFile = nameFile
            post.nreporteNegativo = "0"
            mPostProvider.save(post).addOnCompleteListener {
                if (it.isSuccessful) {

                } else {

                }
            }


        } else {
            var publicacionPorAprobar = PublicacionPorAprobar()
            publicacionPorAprobar.imagen = url
            publicacionPorAprobar.type = descripcion
            publicacionPorAprobar.nota = ""
            publicacionPorAprobar.descripcion = descripcion
            publicacionPorAprobar.uid = mAuth.currentUser!!.uid
            var fechalocal = Date().time
            publicacionPorAprobar.fecha = fechalocal
            publicacionPorAprobar.adminArea =
                PreferenciasGenerales(context!!).getpreferenceCiudadUsuario()
            /*publicacionPorAprobar.lat = UbicacionesUtil.getLatLng().latitude.toString()
            publicacionPorAprobar.lng = UbicacionesUtil.getLatLng().longitude.toString()
         */
            publicacionPorAprobar.nameFile = nameFile
            mPublicacionPorAprobarProvider.save(publicacionPorAprobar).addOnCompleteListener {
                if (it.isSuccessful) {
                    Handler(Looper.myLooper()!!).postDelayed({


                    }, 4000)
                } else {

                }
            }
        }
    }

    private fun guardarDescripcion(url: String, v: View, type: String, nameFile: String) {

        mReporteEnMapa.categoria = mCategoria
        mReporteEnMapa.uid = mAuth.uid.toString()
        mReporteEnMapa.lat = mReporteLatLng.latitude.toString()
        mReporteEnMapa.lng = mReporteLatLng.longitude.toString()
        mReporteEnMapa.foto = url
        mReporteEnMapa.descripcion = v.descripcion.text.toString()
        mReporteEnMapa.nPersonasRating = "0"
        mReporteEnMapa.raitingTotal = "0"
        mReporteEnMapa.ciudad = PreferenciasGenerales(context!!).getpreferenceCiudadUsuario()
        mReporteEnMapa.fecha = Date().time
        mReporteEnMapa.confiabilidad = ""

        mReporteMapaProvider.save(mReporteEnMapa).addOnCompleteListener {
            if (it.isSuccessful) {
                mGeofireProviderReportes.saveReporte(mReporteEnMapa.idReporte, mReporteLatLng)
                banderaMoverCameraUnaVez = true
                // mFile = null
                //  mPhotoFile = null
                if (v.checkbox_post.isChecked && (v.descripcion.text.toString() != "" || url != "")) {

                    crearPostIfCheckbox(url, v.descripcion.text.toString(), nameFile)
                }

            } else {
                Toast.makeText(context, "No se guardó", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun mensajePersonalizadoGPS(tema: String) {
        var texto = ""
        var lot = ""

        val alertDialog: androidx.appcompat.app.AlertDialog
        val builder: androidx.appcompat.app.AlertDialog.Builder =
            androidx.appcompat.app.AlertDialog.Builder(context!!)
        val v: View = LayoutInflater.from(context!!).inflate(R.layout.cuadro_preguntar, null)

        builder.setView(v)
        v.textVie.text = texto

        builder.setCancelable(false)
        alertDialog = builder.create()
        alertDialog.show()
        v.negativo.visibility = View.GONE

        if (tema == "permiso") {
            lot = "mundo.json"
            texto = "Draivin necesita permisos para ofrecerte una mejor experiencia"
            v.afirmativo.text = "Habilitar"
        } else {
            lot = "gps.json"
            texto = "Draivin necesita acceso al GPS para mostrarte reportes y eventos de tu ciudad"
            v.afirmativo.text = "Conectar"
        }
        v.textVie.text = texto
        v.lottieAnimationViewModoAlerta.setAnimation(lot)
        v.afirmativo.setOnClickListener {
            if (tema == "permiso") {
                /* ActivityCompat.requestPermissions(
                     this.activity!!,
                     arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                     LOCATION_REQUEST_CODE
                 )*/

                val permission = Manifest.permission.ACCESS_FINE_LOCATION
                if (ContextCompat.checkSelfPermission(
                        context!!,
                        permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        activity!!,
                        arrayOf(permission),
                        SETTINGS_REQUEST_CODE
                    )
                } else {
                    // El permiso ya ha sido otorgado anteriormente
                }


            } else {
                startActivity(Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                alertDialog!!.dismiss()
                ActivityCompat.requestPermissions(
                    this.activity!!,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_REQUEST_CODE
                )
            }







            alertDialog.dismiss()


        }
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

        var map = mutableMapOf("title" to "Alguien ofrece propina por ayuda")
        map.put("body", "$texto")
        map.put("tipo", "Propina")
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


    override fun onDestroy() {
        super.onDestroy()

    }

    override fun onDestroyView() {
        super.onDestroyView()
//acá es donde se borra el fragment


    }


    override fun onLocationChanged(location: Location) {

        Log.d("xxxx", "Cambiando")
        TODO("Not yet implemented")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)


    }

    override fun cambiandoCiudadListener(view: View, post: Post, isImagen: Boolean) {
        abrirOpciones()
    }

    override fun consultarPropinas(view: View, propinas: Propinas) {
        TODO("Not yet implemented")
    }

    override fun CrearPrimerReporte(view: View) {



    }


}
