package com.jomeva.driving.activities.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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

import kotlinx.android.synthetic.main.activity_perfil.*
/*import kotlinx.android.synthetic.main.activity_perfil.back
import kotlinx.android.synthetic.main.activity_perfil.imagenDelPerfil
import kotlinx.android.synthetic.main.activity_perfil.nombrePerfil
import kotlinx.android.synthetic.main.activity_perfil.numeroDePost
import kotlinx.android.synthetic.main.activity_perfil.reciclerViewPerfilPost*/





class PerfilActivity : AppCompatActivity() {
    lateinit var mPostProvider: PostProvider
    lateinit var mlinearLayoutManager: LinearLayoutManager
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mUserProviders: UserProviders
    private lateinit var mPostProviders: PostProvider
    lateinit var mPostAdapter: PostsAdapter
    private lateinit var mLikeProvider: LikeProvider


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)
        mUserProviders = UserProviders()
        mLikeProvider= LikeProvider()
        mAuth = FirebaseAuth.getInstance()
        mPostProviders= PostProvider()

        getUsuario()
        getPostNumber()
        getTotalMonedas()
        linearLayoutEdit.setOnClickListener {
            val intent = Intent(this, EditPerfilActivity::class.java)
            startActivity(intent)
            Log.d("Editando...","")
        }

        back.setOnClickListener{
            Log.d("atras...","")
            var intent=Intent(this,HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        val adRequest: AdRequest = AdRequest.Builder().build()
        val bannerventanaConsulta =
            findViewById<AdView>(R.id.activityperfilAds)
        bannerventanaConsulta.loadAd(adRequest)

        mlinearLayoutManager = LinearLayoutManager(this)
        reciclerViewPerfilPost.layoutManager = this.mlinearLayoutManager
    }
    private fun getTotalMonedas() {
        mUserProviders.getNmonedasbyUser(mAuth.uid.toString()).addOnSuccessListener {
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


        var query:Query=mPostProvider.getPostbyUserPerfil(mAuth.uid.toString())
        var options: FirestoreRecyclerOptions<Post?> = FirestoreRecyclerOptions.Builder<Post>().setQuery(query,
            Post::class.java).build()
        mPostAdapter= PostsAdapter(options,this)
        reciclerViewPerfilPost.adapter = this.mPostAdapter
        mPostAdapter.startListening()
    }
    override fun onStop() {
        super.onStop()
        mPostAdapter.stopListening()
    }
    private fun getUsuario() {

        mUserProviders.getUsuario(mAuth.uid.toString()).addOnSuccessListener {
            if (it.exists()) {
                if (it.contains("nombre")) {
                    nombrePerfil.text ="${it.get("nombre")}"

                }
                if (it.contains("tiempoEnLaApp")) {
                    val t=it.get("tiempoEnLaApp").toString().toLong()
                    tiempoUsoApp.text =RelativeTime.RelativeTime.timeAUsoApp(t).toString()
                }


                if (it.contains("photo")) {
                    var imagenPerfil: String = it.get("photo").toString()
                    if(it.get("photo").toString()==""){
                        Picasso.get().load(R.drawable.ic_person_24_gray).error(R.drawable.ic_person_24_gray).into(imagenDelPerfil)
                    }else {

                        Picasso.get().load(imagenPerfil).error(R.drawable.ic_person_24_gray)
                            .into(imagenDelPerfil)
                    }

                }

            }
        }
    }
 private fun getPostNumber(){
     mPostProviders.getPostbyUser(mAuth.uid.toString()).get().addOnSuccessListener {
       var numero:Int=it!!.size()
         numeroDePost.setText("$numero")
     }
 }
    override fun onBackPressed() {
        Toast.makeText(this,"detruyendo la activity", Toast.LENGTH_SHORT).show()
        println("detruyendo la activity")
        var intent=Intent(this,HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}