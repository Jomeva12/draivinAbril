package com.jomeva.driving.activities.activities

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jomeva.driving.R
import com.jomeva.driving.activities.adapters.PropinaAporteAdapter
import com.jomeva.driving.activities.modelos.Comentarios
import com.jomeva.driving.activities.modelos.FCMBody
import com.jomeva.driving.activities.modelos.FCMResponse
import com.jomeva.driving.activities.providers.*
import com.jomeva.driving.activities.util.RelativeTime
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_mapa_propina.*
import kotlinx.android.synthetic.main.activity_mapa_propina.comentario
import kotlinx.android.synthetic.main.activity_mapa_propina.publicarComentario

/*import kotlinx.android.synthetic.main.layout_bottom_mapa_propina.*
import kotlinx.android.synthetic.main.layout_bottom_mapa_propina.view.*
import kotlinx.android.synthetic.main.layout_bottom_mapa_propina.view.foto
import kotlinx.android.synthetic.main.layout_bottom_mapa_propina.view.monedas
import kotlinx.android.synthetic.main.layout_bottom_mapa_propina.view.nombre
import kotlinx.android.synthetic.main.layout_bottom_mapa_propina.view.publicaciones*/

import retrofit2.Call
import retrofit2.Response
import java.util.*


class MapaPropina : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {
    private lateinit var mfragmentTwo: FragmentTwo
    private lateinit var Lat: String
    private lateinit var Lon: String

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mMap: GoogleMap

    //comentarios
    lateinit var mlinearLayoutManager: LinearLayoutManager
    private lateinit var mComentarioProvider: ComentarioProvider
    private lateinit var mExtraIdpropina: String
    private lateinit var mExtraDescripcion: String
    private lateinit var mExtrauid: String
    private lateinit var mExtraEstado: String
    private lateinit var mPropinaAporteAdapter: PropinaAporteAdapter
    private lateinit var mComentarios: Comentarios
    private lateinit var mIduser: String
    private lateinit var mTokenProvider: TokenProvider
    private lateinit var mNotificationProviders: NotificationProvider
    private lateinit var mPropinasProvider: PropinasProvider
    private lateinit var mIdPosterPropina: String
    private var marcador: Marker? = null

    private lateinit var mUserProviders: UserProviders
    private lateinit var mPostProviders: PostProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mapa_propina)
        mapViewPropina.onCreate(savedInstanceState)
        val adRequest: AdRequest = AdRequest.Builder().build()
        activity_mapa_propinaAds!!.loadAd(adRequest)

        mAuth = FirebaseAuth.getInstance()
        Lat = intent.getStringExtra("Lat").toString()
        Lon = intent.getStringExtra("Lon").toString()

        mExtraIdpropina = intent.getStringExtra("idPropina").toString()
        mExtrauid = intent.getStringExtra("uid").toString()
        mExtraEstado = intent.getStringExtra("estado").toString()
        mExtraDescripcion = intent.getStringExtra("descripcion").toString()
        mPropinasProvider = PropinasProvider()
        mNotificationProviders = NotificationProvider()
        mlinearLayoutManager = LinearLayoutManager(this)
        reciclerViewComentarioMapaPriopina.layoutManager = this.mlinearLayoutManager
        mTokenProvider = TokenProvider()
        mUserProviders = UserProviders()
        mPostProviders = PostProvider()
        getPostPropina(savedInstanceState)
        Log.d("mExtraEstado->", "->$mExtraEstado")



        comentario.addTextChangedListener {
            if (comentario.text.toString() != "") {
                publicarComentario.visibility = View.VISIBLE
            } else {
                publicarComentario.visibility = View.INVISIBLE

            }
        }
        atras.setOnClickListener {
            finish()
        }
        publicarComentario.setOnClickListener {
            val comment = comentario.text.toString()
            crearComentario(comment)
            comentario.setText("")

        }

        getUser()
    }

    private fun crearComentario(comment: String) {
        println("Clic en publicar")
        mComentarios = Comentarios()
        var fechalocal = Date().time
        mComentarios.comentario = comment
        mComentarios.uid = mAuth.currentUser!!.uid
        mComentarios.idPost = mExtraIdpropina
        mComentarios.uidPost = mExtrauid
        mComentarios.fecha = fechalocal
        mComentarioProvider.crearComentario(mComentarios).addOnCompleteListener {
            if (it.isSuccessful) {
                sendNotification(comment)
                mPropinaAporteAdapter.notifyDataSetChanged()
            } else {
                Toast.makeText(this, "No se pudo publicar", Toast.LENGTH_SHORT).show()

            }
        }
    }
    private fun sendNotification(comment: String) {

        if (mExtrauid == null) {
            Log.d("usuario->", "$mIduser")
            return
        }

        mTokenProvider.getToken(mExtrauid).addOnSuccessListener {
            if (it.exists()) {
                if (it.contains("token")) {
                    val token = it.get("token").toString()
                    var map = mutableMapOf("title" to "Nuevo aporte")
                    map.put("body", comment)

                    map.put("user1", mAuth.currentUser!!.uid)
                    map.put("user2", mExtrauid)
                    map.put("tipo", "comentarioPropina")
                    map.put("idPropinas", mExtraIdpropina)
                    map.put("lat", Lat)
                    map.put("lon", Lon)
                    map.put("estado", mExtraEstado)


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
    private fun getPostPropina(savedInstanceState: Bundle?) {

        mPropinasProvider.getPropinabyId(mExtraIdpropina).addOnSuccessListener {
            if (it.exists()) {
                if (it.contains("descripcion")) {
                    descripcionpropina.setText("${it.get("descripcion")}")
                    NmegustaPropina.text = it.getString("monedas")
                    recalcularDimensiones(savedInstanceState)


                }
                /*  if (it.contains("fecha")) {
                      var fecha: Long? = it.getLong("fecha")
                      var mRelativeTime: String? =
                          fecha?.let { it1 -> RelativeTime.RelativeTime.getTimeAgo(it1, this) }
                      txtRelaiveTime.setText("${mRelativeTime}")
                  }
                  if (it.contains("monedas")) {
                      textMoneda.setText("${it.get("monedas")}")

                  }
  */
                if (it.contains("uid")) {
                    val uid = it.get("uid").toString()

                    //mIduser = uid
                    // mIdPosterPropina = uid
                }

            }
        }
    }

    fun recalcularDimensiones(savedInstanceState: Bundle?) {
        if (Lon == "" && Lat == "") {
            mapViewPropina.visibility = View.GONE
            descripcionpropina.textSize = 23f

            val params = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
            linearTop.layoutParams = params

            Handler(Looper.getMainLooper()).postDelayed({
               val alturaLinear=  linearTop.height+78
                Log.d("alturaLinear->", "->$alturaLinear")

                val paramsLinear = CoordinatorLayout.LayoutParams(
                    CoordinatorLayout.LayoutParams.MATCH_PARENT,
                    CoordinatorLayout.LayoutParams.WRAP_CONTENT )
                linearRecycler.layoutParams=paramsLinear
                //linearRecycler.setPadding(10,alturaLinear,50,95)

                if (mExtraEstado != "") {
                    linearComentario.visibility = View.GONE
                    linearRecycler.setPadding(10,alturaLinear,50,8)

                } else {
                    linearComentario.visibility = View.VISIBLE
                    linearRecycler.setPadding(10,alturaLinear,50,95)
                }

            },200)



        } else {
            linearRecycler.setPadding(10,0,50,95)
            if (mExtraEstado != "") {
                linearComentario.visibility = View.GONE
                linearRecycler.setPadding(10,0,50,8)

            } else {
                linearComentario.visibility = View.VISIBLE
                linearRecycler.setPadding(10,0,50,95)
            }

            mapViewPropina.visibility = View.VISIBLE
            mapViewPropina.onSaveInstanceState(savedInstanceState)
            mapViewPropina.onResume()
            mapViewPropina.getMapAsync(this)


        }
    }
    override fun onStart() {
        super.onStart()
        Log.d("mapass", "Entrando")
        mComentarioProvider = ComentarioProvider()
        var query: Query = mComentarioProvider.getComentariobyPost(mExtraIdpropina)
        var options: FirestoreRecyclerOptions<Comentarios?> =
            FirestoreRecyclerOptions.Builder<Comentarios>().setQuery(
                query,
                Comentarios::class.java
            ).build()

        mPropinaAporteAdapter = PropinaAporteAdapter(options, this)
        reciclerViewComentarioMapaPriopina.setAdapter(this.mPropinaAporteAdapter)

        mPropinaAporteAdapter.startListening()
        //observador
        mPropinaAporteAdapter.registerAdapterDataObserver(object :
            RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {

                super.onItemRangeInserted(positionStart, itemCount)

                //getVisto()
                val numberMessage: Int = mPropinaAporteAdapter.itemCount
                val lastMessagePosition: Int =
                    mlinearLayoutManager.findLastCompletelyVisibleItemPosition()
                if (lastMessagePosition == -1 || positionStart >= numberMessage - 1 && lastMessagePosition == positionStart - 1) {
                    reciclerViewComentarioMapaPriopina.scrollToPosition(positionStart)
                }
            }
        })
    }

    override fun onStop() {
        super.onStop()
        if (mPropinaAporteAdapter != null) {
            mPropinaAporteAdapter.stopListening()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
       /* mMap.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
                this, R.raw.map_style_oscuro
            )
        )*/
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        // mMap.setOnCameraIdleListener(mCameraListener)
        mMap.uiSettings.isZoomControlsEnabled = true
        val coordenadas = LatLng(Lat.toDouble(), Lon.toDouble())
        /* val imagen =BitmapDescriptorFactory.fromResource(
            assets.list("happy.json"),
        )*/
        mMap.uiSettings.isMapToolbarEnabled = false
        marcador = mMap.addMarker(
            MarkerOptions()
                .position(coordenadas)
                .title("$mExtraDescripcion")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))

        )
//modificar para que se intuitivo el acceso alos detalles de la pregunta


        mMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(coordenadas, 15f),
            1000,
            null,
        )
        marcador?.tag = mExtraIdpropina
        var marker: Marker
        mMap.setOnInfoWindowClickListener(this)


    }

    /**/
    public fun iniciarLocalizacion(x: String) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {

                if (gpsActived()) {

                    //mMap.isMyLocationEnabled = true


                } else {
                    //showAlertDialogNOGPS();
                }
            } else {
                mfragmentTwo.checkLocationPermissions();
            }
        } else {
            if (gpsActived()) {

                //mMap!!.isMyLocationEnabled = true
            } else {
                //showAlertDialogNOGPS();
            }
        }
    }

    public fun gpsActived(): Boolean {
        var isActive = false
        val locationManager =
            this!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            isActive = true
        }
        Log.d("isActive->", "$isActive")
        return isActive
    }

    private fun getPostNumber(v: View) {
        /* mPostProviders.getPostbyUser(mAuth.uid.toString()).get().addOnSuccessListener {
             var numero:Int=it.size()
             v.publicaciones.setText("$numero")
         }*/
    }

    fun getUser() {
        /*mUserProviders.getUsuario(mExtrauid).addOnSuccessListener { it ->

            if (it.contains("uid")) {*/
        //val uid = it.get("uid").toString()
        mUserProviders.getUsuario(mExtrauid).addOnSuccessListener { it2 ->
            if (it2.exists()) {

                    if (it2.contains("photo")) {
                        var imagenPerfil: String = it2.get("photo").toString()
                        if (it2.get("photo").toString() == "") {
                            Picasso.get().load(R.drawable.person_24)
                                .error(R.drawable.person_24)
                                .into(fotoPerfil)
                        } else {
                            Picasso.get().load(imagenPerfil).error(R.drawable.person_24)
                                .into(fotoPerfil)
                        }
                    }
                    if (it2.contains("nombre")) {
                        var nombre: String = it2.get("nombre").toString()
                        nombreUsuario.text = nombre

                    }
                    getPropina()


            }
        }
        /*  }
      }*/
    }

    private fun getPropina() {
        Log.d("entrandos", mExtraIdpropina)
        mPropinasProvider.getPropinabyId(mExtraIdpropina).addOnSuccessListener {
            if (it.exists()) {
                Log.d("entrandos2", mExtraIdpropina)
                if (it.contains("fecha")) {
                    val fecha: Long = it.get("fecha") as Long
                    Log.d("entrandos3", fecha.toString())
                    val relative = RelativeTime.RelativeTime.getTimeAgo(fecha, this).toString()
                    Log.d("entrandos4", relative)
                    textViewRelativeTime.text = relative
                }
            }
        }
    }

    override fun onInfoWindowClick(it: Marker) {
        /* val idPropina = it.tag.toString()
         mPropinasProvider.getPropinabyId(idPropina).addOnSuccessListener {
             val bottomSheetDialog = BottomSheetDialog(this!!, R.style.BottomSheetDialogTheme)
             val bottomSheetView = LayoutInflater.from(this)
                 .inflate(R.layout.layout_bottom_mapa_propina, bottomSheet_mapa_propina)
             if (it.exists()) {
                 if (it.contains("uid")) {
                     val uid = it.get("uid").toString()
                     mUserProviders.getUsuario(uid.toString()).addOnSuccessListener { it2 ->
                         if (it2.exists()) {
                             if (it2.contains("nombre")) {
                                 bottomSheetView.nombre.text = it2.get("nombre").toString()

                             }

                          if (it2.contains("photo")) {
                                     var imagenPerfil: String = it2.get("photo").toString()
                                     if (it2.get("photo").toString() == "") {
                                         Picasso.get().load(R.drawable.person_24)
                                             .error(R.drawable.person_24)
                                             .into(bottomSheetView.foto)
                                     } else {
                                         Picasso.get().load(imagenPerfil).error(R.drawable.person_24)
                                             .into(bottomSheetView.foto)
                                     }
                                 }
                           if (it2.contains("monedas")) {
                                     bottomSheetView.monedas.text = it2.get("monedas").toString()
                            }
                             getPostNumber(bottomSheetView)
                         }
                     }

                     //acá traigo lo de propinas
                     if (it.contains("descripcion")) {
                         val descripcion = it.get("descripcion")
                         bottomSheetView.descripcion.text = descripcion.toString()
                     }
                     if (it.contains("monedas")) {
                         val monedas = it.get("monedas")
                         bottomSheetView.Nmonedas.text = monedas.toString()
                     }
                     bottomSheetView.foto.setOnClickListener {
                         if (uid.isNotEmpty()) {
                             val intent = Intent(this, PerfilActivityPost::class.java)
                             intent.putExtra("uid", uid)
                             this?.startActivity(intent)
                         }
                     }
                     bottomSheetDialog.setContentView(bottomSheetView)
                     bottomSheetDialog.show()
                 }



             }


         }*/
    }


}