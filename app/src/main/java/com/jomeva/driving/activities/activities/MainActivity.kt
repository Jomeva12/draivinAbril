package com.jomeva.driving.activities.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.jomeva.driving.activities.util.PreferenciasGenerales
import com.google.android.gms.ads.MobileAds
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // setContentView(R.layout.activity_main)
        mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser

        if (PreferenciasGenerales(this).getpreferencePolitica()=="true"){
            MobileAds.initialize(this)
            val homeIntent = Intent(this, HomeActivity::class.java)
            startActivity(homeIntent)
            finish()
        }else{
            val inten = Intent(this, Politica::class.java)
            startActivity(inten)
            finish()

        }

        //si está registrado
        /*if (user == null) {
            if (PreferenciasGenerales(this).getpreferencePolitica()=="true"){
                val loginIntent = Intent(this, AuthActivity::class.java)
                startActivity(loginIntent)
                finish()
            }else{
                val inten = Intent(this, Politica::class.java)
                startActivity(inten)
                finish()
            }


        } else {
            MobileAds.initialize(this)
            val homeIntent = Intent(this, HomeActivity::class.java)
            startActivity(homeIntent)
            finish()
        }*/



    }

}