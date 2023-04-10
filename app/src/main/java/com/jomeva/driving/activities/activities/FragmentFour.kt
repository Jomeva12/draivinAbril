package com.jomeva.driving.activities.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jomeva.driving.activities.adapters.PropinaAporteAdapter
import com.jomeva.driving.activities.adapters.PropinasAdapter
import com.jomeva.driving.activities.modelos.*
import com.jomeva.driving.activities.util.MyListener
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.cuadro_comentario_propinas.view.*
import kotlinx.android.synthetic.main.cuadro_comentario_propinas.view.comentarioBottomSheet
import kotlinx.android.synthetic.main.cuadro_comentario_propinas.view.publicarComentario
import kotlinx.android.synthetic.main.cuadro_comentario_propinas.view.reciclerViewComentario
import kotlinx.android.synthetic.main.fragment_four.*

import retrofit2.Call
import retrofit2.Response
import java.util.*
import com.jomeva.driving.R
import com.jomeva.driving.activities.providers.*
import com.jomeva.driving.activities.util.PreferenciasGenerales
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.squareup.picasso.Picasso
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions
import kotlinx.android.synthetic.main.cuadro_comentario_propinas.view.imagenEmoji
import kotlinx.android.synthetic.main.cuadro_comentarios_eventos_transparentes.*
import kotlinx.android.synthetic.main.cuadro_preguntar.view.*

import kotlinx.android.synthetic.main.cuadro_propina_mapa.*
import kotlinx.android.synthetic.main.cuadro_propina_mapa.view.*
import kotlinx.android.synthetic.main.fragment_one.*


class FragmentFour : Fragment(), MyListener {
    ///////////////////Propina adapter

    private lateinit var mComentarioProvider: ComentarioProvider
    lateinit var mlinearLayoutManagerComentario: LinearLayoutManager
    private lateinit var mPropinaAporteAdapter: PropinaAporteAdapter
    private lateinit var mComentarios: Comentarios
    lateinit var mlinearLayoutManagerComentarioPropina: LinearLayoutManager
    private lateinit var mTokenProvider: TokenProvider
    private lateinit var mNotificationProviders: NotificationProvider
    private lateinit var mMonedas: String

    ///////////////////////////////////mapa
    private lateinit var mMap: GoogleMap
    private var marcador: Marker? = null
    private lateinit var coordenadas: LatLng
    lateinit var mPostProvider: PostProvider

    ///////////////////////
    private lateinit var mUserProviders: UserProviders
    private lateinit var mAlerDialog: AlertDialog
    lateinit var mReciclerView: RecyclerView
    lateinit var mlinearLayoutManager: LinearLayoutManager
    lateinit var mPropinasAdapter: PropinasAdapter
    private lateinit var mPropinasProvider: PropinasProvider
    private lateinit var mAuth: FirebaseAuth

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()


        mMonedas = ""
        mUserProviders = UserProviders()
        adapter()
        btnPropinas.setOnClickListener {
            val users=mAuth.currentUser
            if (users != null) {
                alertDialogPostPropina()
            }else{
                mensajePersonalizadoLogin(context!!)
            }

        }


    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        var mView = inflater.inflate(R.layout.fragment_four, container, false)
        val adRequest: AdRequest = AdRequest.Builder().build()
        val bannerventanaConsulta = mView!!.findViewById<AdView>(R.id.cuadro_propina_mapa)
        bannerventanaConsulta!!.loadAd(adRequest)

        mReciclerView = mView.findViewById(R.id.reciclerViewPropinas)
        mlinearLayoutManager = LinearLayoutManager(context)
        mReciclerView.layoutManager = this.mlinearLayoutManager


        return mView
    }

    override fun onStart() {
        super.onStart()




    }

    override fun onResume() {
        super.onResume()
        adapter()
    }
fun adapter(){

    val ciudad = PreferenciasGenerales(context!!).getpreferenceCiudadUsuario()
    mPropinasProvider = PropinasProvider()
    var query: Query = mPropinasProvider.getPropinaByCiudad(ciudad!!)
         var options: FirestoreRecyclerOptions<Propinas?> =
            FirestoreRecyclerOptions.Builder<Propinas>().setQuery(
                query,
                Propinas::class.java
            ).build()


    mPropinasAdapter = PropinasAdapter(options, context)
    if(isAdded) {
        if (mPropinasAdapter != null) {
            mReciclerView.adapter = this.mPropinasAdapter
            mPropinasAdapter?.startListening()
           // mPropinasAdapter?.notifyDataSetChanged()

        }
    }
}
    override fun onStop() {
        super.onStop()
        mPropinasAdapter.stopListening()
    }

    fun visitaGuiadaPropinas() {
        TapTargetView.showFor(
            activity,
            TapTarget.forView(
                btnPropinas,
                "Crear consulta",
                "Las consultas creadas serán vistas en tu zona o ciudad, asegúrate que sea clara la respuesta y ofrece recompensa para motivar a los usuarios."
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

                    PreferenciasGenerales(context!!).setpreferenceGuiaPrimeraPropina("true")

                    alertDialogPostPropina()
                }
            }
        )

    }
    private fun mensajePersonalizadoLogin(context: Context) {

        val alertDialog: AlertDialog
        val builder: AlertDialog.Builder =
            AlertDialog.Builder(context!!)
        val v: View = LayoutInflater.from(context).inflate(R.layout.cuadro_registrarse_primera_vez, null)
        builder.setView(v)

        builder.setCancelable(true)
        alertDialog = builder.create()
        alertDialog.show()

        v.afirmativo.setOnClickListener {

            val intent= Intent(context, AuthActivity::class.java)
            context!!.startActivity(intent)

        }
        v.negativo.setOnClickListener {
            alertDialog.dismiss()
        }
    }
    private fun alertDialogPostPropina() {
        if (PreferenciasGenerales(context!!).getpreferenceGuiaPrimeraPropina() == "false") {
            visitaGuiadaPropinas()
        } else {

            val alertDialog: AlertDialog
            val builder: AlertDialog.Builder =
                AlertDialog.Builder(context!!)
            val v: View = LayoutInflater.from(context).inflate(R.layout.cuadro_propina_mapa, null)

            builder.setView(v)
            getUsuario(v)
            builder.setCancelable(true)

            alertDialog = builder.create()
            alertDialog.show()
           val adRequest: AdRequest = AdRequest.Builder().build()
            val bannerventanaConsulta = alertDialog!!.findViewById<AdView>(R.id.cuadro_propina_mapa)
            bannerventanaConsulta!!.loadAd(adRequest)

            alertDialog.linearLikePropina.setOnClickListener {
                v.NmegustaPropina.text =
                    "${alertDialog.NmegustaPropina.text.toString().toInt() + 1}"

            }
            v.cerrarCuadro.setOnClickListener {
                alertDialog.dismiss()
            }
            v.btnpublicar.setOnClickListener {

                if (v.txtDescripcion.text.toString() == "") {
                    Toast.makeText(context, "No hay descripcion", Toast.LENGTH_LONG).show()
                } else {


                    guardarPropinas(v)
                    alertDialog.dismiss()


                    //mensajeSuccess()
                }

            }


        }
    }


    private fun guardarPropinas(v: View) {
        val descripcion = v.txtDescripcion.text
        val monedas = v.NmegustaPropina.text.toString()
        if (monedas > 0.toString() && monedas != "" && descripcion.toString() != "") {

            var mPropinas: Propinas = Propinas()
            mPropinas.descripcion = descripcion.toString()
            mPropinas.uid = mAuth.uid.toString()
            mPropinas.monedas = monedas
            mPropinas.idAporte = ""
            mPropinas.ciudad = PreferenciasGenerales(context!!).getpreferenceCiudadUsuario()
            mPropinas.fecha = Date().time
            Log.d("datos propinas", "$mPropinas")
            mPropinasProvider.save(mPropinas).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(context, "Publicación exitosa", Toast.LENGTH_SHORT).show()
                    if (mPropinasAdapter!=null){
                        mPropinasAdapter?.notifyDataSetChanged()
                    }
//notoficar
                sendNotificationEventosGrupo(descripcion.toString())
                } else {
                    Toast.makeText(
                        context,
                        "No Se pudo publicar, verifique su conexión",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            Toast.makeText(
                context,
                "No puede estar vacío descripción y monedas",
                Toast.LENGTH_SHORT
            ).show()

        }

    }

    private fun sendNotificationEventosGrupo(texto: String) {
        if(PreferenciasGenerales(context!!).getpreferenceCiudadUsuario()!=""){
            var lista= arrayListOf<String>()
            mTokenProvider = TokenProvider()
            mUserProviders.getUserByCiudad(PreferenciasGenerales(context!!).getpreferenceCiudadUsuario()).get().addOnSuccessListener {

                for (i in it){
                    if (i.get("uid").toString()!=mAuth.uid){
                        mTokenProvider.getToken(i.get("uid").toString()).addOnSuccessListener {
                            mNotificationProviders = NotificationProvider()
                            if (it.exists()) {
                                if (it.contains("token")) {
                                    val token = it.get("token").toString()
                                    //lista.add(token)

                                    enviarNotificacionesByCiudad(token,texto)

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

    override fun cambiandoCiudadListener(view: View, post: Post, isImagen: Boolean) {
        TODO("Not yet implemented")
    }

    override fun consultarPropinas(view: View, propinas: Propinas) {
        mPostProvider = PostProvider()
        mPropinasProvider = PropinasProvider()
        val bottomSheetDialog = BottomSheetDialog(view.context)
        val bottomsheetBehavior: BottomSheetBehavior<View>
        val bottomSheetView = LayoutInflater.from(view.context)
            .inflate(
                R.layout.cuadro_comentario_propinas,
                bottom_sheet_comentario_transparente
            )
        bottomSheetDialog.setContentView(bottomSheetView)
//behavior
        bottomsheetBehavior = BottomSheetBehavior.from(bottomSheetView.parent as View)
        // set to behavior
        bottomsheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        val layout =
            bottomSheetDialog.findViewById<CoordinatorLayout>(R.id.bottom_sheet_comentario_propinas)
        assert(layout != null)
        layout!!.minimumHeight = Resources.getSystem().displayMetrics.heightPixels


        bottomSheetDialog.show()
        val adRequest: AdRequest = AdRequest.Builder().build()
        val bannerventanaConsulta = layout!!.findViewById<AdView>(R.id.cuadro_comentario_propinaAds)
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

        mPropinasProvider.getPropinabyId(propinas.idPropinas).addOnSuccessListener {
            if (it.exists()) {
                if (it.contains("descripcion")) {
                    val descripcion = it.getString("descripcion")
                    val N = it.getString("monedas")
                    layout.descripcionpropina.setText(descripcion)
                    layout.NmegustaPropinaBotton.text = N
                }
            }
        }

        mlinearLayoutManagerComentario = LinearLayoutManager(context)
        layout.reciclerViewComentario.layoutManager = this.mlinearLayoutManagerComentario
        adapterComentario(view, layout.reciclerViewComentario, propinas.idPropinas)
        ////////////////////////////////////////////////////////////////////////////////////

        layout.comentarioBottomSheet.addTextChangedListener {
            if (layout.comentarioBottomSheet.text.toString() != "") {
                layout.publicarComentario.visibility = View.VISIBLE
            } else {
                layout.publicarComentario.visibility = View.INVISIBLE

            }
        }
        layout.publicarComentario.setOnClickListener {
            val auth=FirebaseAuth.getInstance()
            val users=auth.currentUser
            if (users != null) {
                val comment = layout.comentarioBottomSheet.text.toString()
                crearComentario(comment, propinas)
                layout.comentarioBottomSheet.setText("")
            }else{
                mensajePersonalizadoLogin(view.context)
            }


        }
    }

    override fun CrearPrimerReporte(view: View) {
        TODO("Not yet implemented")
    }

    private fun adapterComentario(
        view: View,
        mReciclerViewComentario: RecyclerView,
        idPost: String
    ) {
        mComentarioProvider = ComentarioProvider()

        var query: Query = mComentarioProvider.getComentariobyPost(idPost)

        var options: FirestoreRecyclerOptions<Comentarios?> =
            FirestoreRecyclerOptions.Builder<Comentarios>().setQuery(
                query,
                Comentarios::class.java
            ).build()

        mPropinaAporteAdapter = PropinaAporteAdapter(options, view!!.context)
        mReciclerViewComentario.adapter = this.mPropinaAporteAdapter
        mPropinaAporteAdapter.startListening()
    }

    private fun crearComentario(comment: String, propinas: Propinas) {
        println("Clic en publicar")
        mAuth = FirebaseAuth.getInstance()
        mComentarios = Comentarios()
        mComentarioProvider = ComentarioProvider()
        var fechalocal = Date().time
        mComentarios.comentario = comment
        mComentarios.uid = mAuth.currentUser!!.uid
        mComentarios.idPost = propinas.idPropinas
        mComentarios.uidPost = propinas.uid
        mComentarios.fecha = fechalocal
        mComentarioProvider.crearComentario(mComentarios).addOnCompleteListener {
            if (it.isSuccessful) {
                sendNotification(comment, propinas)
                Log.d("cargarComent ", "->subido")
            } else {
                Log.d("cargarComent ", "->negado")
            }
        }
    }

    private fun sendNotification(comment: String, propinas: Propinas) {
        mTokenProvider = TokenProvider()
        if (propinas.uid == null) {

            return

        }
        mTokenProvider.getToken(propinas.uid).addOnSuccessListener {
            mNotificationProviders = NotificationProvider()
            if (it.exists()) {

                if (it.contains("token")) {

                    val token = it.get("token").toString()

                    var map = mutableMapOf("title" to "Nuevo comentario Propina")
                    map.put("body", comment)
                    map.put("tipo", "comentarioPropina")
                    map.put("user1", mAuth.currentUser!!.uid)
                    map.put("user2", propinas.uid)
                    map.put("idPost", propinas.idPropinas)

                    Log.d("mapas", "${map.toString()}")
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


}