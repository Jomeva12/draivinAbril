package com.jomeva.driving.activities.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.jomeva.driving.R
import com.jomeva.driving.activities.util.PreferenciasGenerales

class SplashLoading : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_loading)

        val adRequest: AdRequest = AdRequest.Builder().build()
        val bannerventanaConsulta =
            findViewById<AdView>(R.id.activityperfilpostAds)
        bannerventanaConsulta.loadAd(adRequest)

        Handler(Looper.myLooper()!!).postDelayed({
            val intent= Intent(this,HomeActivity::class.java)
           // intent.putExtra("ciudad",PreferenciasGenerales(this!!).getpreferenceCiudadUsuario())
            startActivity(intent)
            finish()

       }, 2500)
    }
}