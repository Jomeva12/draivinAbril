package com.jomeva.driving.activities.activities

import androidx.fragment.app.Fragment


class DetalleFragment : Fragment() {
  /*  // TODO: Rename and change types of parameters
    private lateinit var mPostProviders: PostProvider
    private lateinit var mUserProviders: UserProviders
    private lateinit var mComentarioProvider: ComentarioProvider
    private lateinit var mComentarioAdapter: ComentarioAdapter
    private lateinit var mNotificationProviders: NotificationProvider
    private lateinit var mTokenProvider: TokenProvider
    private lateinit var mExtraPostId: String
    private lateinit var mIdPoster: String
    private lateinit var mComentarios: Comentarios
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mIduser:String
    private lateinit var mEmojIconActions: EmojIconActions
    lateinit var mlinearLayoutManager: LinearLayoutManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        reciclerViewComentario
      //  mEmojIconActions = EmojIconActions(this, vista, comentario, emojiComentario)
      //  mEmojIconActions.ShowEmojIcon()
        mAuth = FirebaseAuth.getInstance()
        mPostProviders = PostProvider()
        mIdPoster = ""
        mUserProviders = UserProviders()
        mTokenProvider=TokenProvider()
        mComentarioProvider = ComentarioProvider()
        mNotificationProviders=NotificationProvider()
     //   mExtraPostId = intent.getStringExtra("id").toString()

       // getPost()

        //mlinearLayoutManager = LinearLayoutManager(this)
        reciclerViewComentario.setLayoutManager(this.mlinearLayoutManager)

        perfilLayout.setOnClickListener {
        *//*    if (!mIdPoster.isEmpty()) {
                val intent = Intent(this, PerfilActivityPost::class.java)
                intent.putExtra("uid", mIdPoster)
                this?.startActivity(intent)

            }*//*

        }
        back.setOnClickListener {
          //  finish()
        }

        comentario.addTextChangedListener {
            if (comentario.text.toString() != "") {
                publicarComentario.visibility = View.VISIBLE
            } else {
                publicarComentario.visibility = View.INVISIBLE

            }
        }



        publicarComentario.setOnClickListener {
            val comment = comentario.text.toString()
           // crearComentario(comment)
            comentario.setText("")

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detalle, container, false)
    }
   *//* private fun crearComentario(comment: String) {
        println("Clic en publicar")
        mComentarios = Comentarios()
        var fechalocal = Date().getTime()
        mComentarios.comentario = comment
        mComentarios.idUser = mAuth.currentUser.uid
        mComentarios.idPost = mExtraPostId
        mComentarios.fecha = fechalocal
        mComentarioProvider.crearComentario(mComentarios).addOnCompleteListener {
            if (it.isSuccessful) {
                sendNotification(comment)
                Toast.makeText(this, "Comentario publicado", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "No se pudo publicar", Toast.LENGTH_SHORT).show()

            }
        }
    }
    private fun sendNotification(comment: String) {
        if (mIduser == null) {
            Log.d("usuario->","$mIduser")
            return

        }

        mTokenProvider.getToken(mIduser).addOnSuccessListener {

            if (it.exists()) {

                if (it.contains("token")) {

                    val token = it.get("token").toString()

                    var map = mutableMapOf("title" to "Nuevo comentario")
                    map.put("body", comment)
                    var body = FCMBody(token, "high", "4500s", map)

                    mNotificationProviders.sendNotification(body)!!
                        // .enqueue(object :retrofit2.Callback<FCMresponse?>
                        .enqueue(object: retrofit2.Callback<FCMResponse?>  {
                            override fun onResponse(
                                call: Call<FCMResponse?>?,
                                response: Response<FCMResponse?>
                            ) {
                                if (response.body() != null) {

                                    if (response.body()!!.success == 1) {
                                        Log.d("Enviado...","${response}")


                                    } else {

                                    }
                                } else {


                                }
                            }

                            override fun onFailure(call: Call<FCMResponse?>?, t: Throwable?) {}
                        })


                }
            }else{

            }
        }
    }
    private fun getPost() {
        mPostProviders.getPostbyId(mExtraPostId).addOnSuccessListener {
            if (it.exists()) {
                if (it.contains("descripcion")) {
                    descripcionpost.setText("${it.get("descripcion")}")
                }
                if (it.contains("fecha")) {
                    var fecha: Long? = it.getLong("fecha")
                    var mRelativeTime: String? =
                        fecha?.let { it1 -> RelativeTime.RelativeTime.getTimeAgo(it1, this) }
                    txtRelaiveTime.setText("${mRelativeTime}")
                }

                if (it.contains("imagen")) {
                    if (it.get("imagen").toString() == "") {

                    }else {
                        var imagenPost: String = it.get("imagen").toString()
                        Picasso.get().load(imagenPost).error(R.drawable.ic_camera_24)
                            .into(imagepostCard)
                    }

                }
                if (it.contains("uid")) {
                    val uid = it.get("uid").toString()
                    getUserPost(uid)
                    mIduser = uid
                    mIdPoster = uid
                }

            }
        }
    }

    private fun getUserPost(uid: String) {
        mUserProviders.getUsuario(uid).addOnSuccessListener {
            if (it.exists()) {
                if (it.contains("nombre")) {
                    nombrePoster.setText("${it.get("nombre")}")
                }
                if (it.contains("photo")) {
                    var imagenPerfil: String = it.get("photo").toString()
                    if(it.get("photo").toString()==""){
                        Picasso.get().load(R.drawable.person_24).error(R.drawable.person_24).into(photoPerfilPost)
                    }else {
                        Picasso.get().load(imagenPerfil).error(R.drawable.person_24)
                            .into(photoPerfilPost)
                    }
                }
            }
        }
    }*/
}