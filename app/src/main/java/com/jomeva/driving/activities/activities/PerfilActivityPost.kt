package com.jomeva.driving.activities.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.jomeva.driving.R
import com.jomeva.driving.activities.adapters.PostsAdapter
import com.jomeva.driving.activities.modelos.Post
import com.jomeva.driving.activities.providers.LikeProvider
import com.jomeva.driving.activities.providers.PostProvider
import com.jomeva.driving.activities.providers.UserProviders
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.jomeva.driving.activities.util.RelativeTime
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_perfil_post.*
import kotlinx.android.synthetic.main.activity_perfil_post.Nmonedas
import kotlinx.android.synthetic.main.activity_perfil_post.back
import kotlinx.android.synthetic.main.activity_perfil_post.imagenDelPerfil
import kotlinx.android.synthetic.main.activity_perfil_post.nombrePerfil
import kotlinx.android.synthetic.main.activity_perfil_post.numeroDePost
import kotlinx.android.synthetic.main.activity_perfil_post.reciclerViewPerfilPost
import kotlinx.android.synthetic.main.cuadro_preguntar.view.*


class PerfilActivityPost : AppCompatActivity() {
    lateinit var mPostProvider: PostProvider
    lateinit var mPostAdapter: PostsAdapter
    lateinit var mlinearLayoutManager: LinearLayoutManager

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mUserProviders: UserProviders

    private lateinit var mLikeProvider: LikeProvider
    private  var mExtraUserId: String=""
    private  var mExtraMonedasPost:String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_post)
        mUserProviders = UserProviders()
        mAuth = FirebaseAuth.getInstance()
        mPostProvider= PostProvider()
        mLikeProvider= LikeProvider()
        mExtraUserId= intent.getStringExtra("uid").toString()
        mExtraMonedasPost=intent.getStringExtra("monedasput").toString()
        getUsuario()
        getPostNumber()

        val adRequest: AdRequest = AdRequest.Builder().build()
        val bannerventanaConsulta =
            findViewById<AdView>(R.id.activityperfilpostAds)
        bannerventanaConsulta.loadAd(adRequest)



        Log.d("monedas en perfilpost->", " $mExtraMonedasPost")

        back.setOnClickListener{
            finish()
        }
        irAchat.setOnClickListener {
            val users=mAuth.currentUser
            if (users != null) {
                val intent = Intent(this, ChatActivity::class.java)
                intent.putExtra("user2", mExtraUserId)
                intent.putExtra("user1", mAuth.currentUser!!.uid)
                intent.putExtra("idChat", mAuth.currentUser!!.uid + mExtraUserId)
                intent.putExtra("estadoPropina", "false")
                startActivity(intent)
            }else{
                mensajePersonalizadoLogin()
            }


        }

        mlinearLayoutManager = LinearLayoutManager(this)
        reciclerViewPerfilPost.setLayoutManager(this.mlinearLayoutManager)
    }
    private fun mensajePersonalizadoLogin() {

        val alertDialog: AlertDialog
        val builder: AlertDialog.Builder =
            AlertDialog.Builder(this!!)
        val v: View = LayoutInflater.from(this).inflate(R.layout.cuadro_registrarse_primera_vez, null)
        builder.setView(v)

        builder.setCancelable(true)
        alertDialog = builder.create()
        alertDialog.show()

        v.afirmativo.setOnClickListener {

            val intent=Intent(this, AuthActivity::class.java)
          startActivity(intent)

        }
        v.negativo.setOnClickListener {
            alertDialog.dismiss()
        }
    }
    private fun getTotalMonedasById() {

                    mUserProviders.getNmonedasbyUser(mExtraUserId).addOnSuccessListener {
                      if (it.exists()){
                            if (it.contains("monedas")){
                                Nmonedas.text = it.get("monedas").toString()
                            }
                        }
                    }

    }
    override fun onStart() {
        super.onStart()
        mPostProvider = PostProvider()
        println(mAuth.uid.toString())

        var query: Query =mPostProvider.getPostbyUserPerfil(mExtraUserId)
        var options: FirestoreRecyclerOptions<Post?> = FirestoreRecyclerOptions.Builder<Post>().setQuery(query,
            Post::class.java).build()
        mPostAdapter= PostsAdapter(options,this)
        reciclerViewPerfilPost.setAdapter(this.mPostAdapter)
        mPostAdapter.startListening()
    }
    override fun onStop() {
        super.onStop()
        mPostAdapter.stopListening()
    }
    private fun getUsuario() {

        mUserProviders.getUsuario(mExtraUserId).addOnSuccessListener {
            if (it.exists()) {
                if (it.contains("nombre")) {
                    nombrePerfil.setText("${it.get("nombre")}")
                }
                if (it.contains("monedas")) {
                    Nmonedas.setText("${it.get("monedas")}")
                }
                if (it.contains("tiempoEnLaApp")) {
                    val t=it.get("tiempoEnLaApp").toString().toLong()
                    tiempoUsoApp.text = RelativeTime.RelativeTime.timeAUsoApp(t).toString()
                }
                if (it.contains("photo")) {
                    var imagenPerfil: String = it.get("photo").toString()
                    if(it.get("photo").toString()==""){
                        Picasso.get().load(R.drawable.ic_person_azul).error(R.drawable.ic_person_azul).into(imagenDelPerfil)
                    }else {
                        Picasso.get().load(imagenPerfil).error(R.drawable.ic_person_azul)
                            .into(imagenDelPerfil)
                    }

                }

            }
        }
    }
    private fun getPostNumber(){
        mPostProvider.getPostbyUser(mExtraUserId).get().addOnSuccessListener {

            var numero:Int=it.size()
            numeroDePost.text = "$numero"
        }
    }
}