package com.jomeva.driving.activities.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jomeva.driving.R
import com.jomeva.driving.activities.adapters.AdapterSugerencias
import com.jomeva.driving.activities.modelos.SugerenciasUsuarios
import com.jomeva.driving.activities.providers.SugerenciasProvider
import com.jomeva.driving.activities.util.PreferenciasGenerales
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_sugerencias.*

class SugerenciasActivity : AppCompatActivity() {
    private lateinit var mAdapterSugerencias: AdapterSugerencias
    private lateinit var mRecyclerViewSugerencias: RecyclerView
    lateinit var mlinearLayoutManager: LinearLayoutManager
    private lateinit var mSugerenciasProvider: SugerenciasProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sugerencias)

        val adRequest: AdRequest = AdRequest.Builder().build()
        val bannerventanaConsulta =
            findViewById<AdView>(R.id.activityperfilpostAds)
        bannerventanaConsulta.loadAd(adRequest)

        val adRequest2: AdRequest = AdRequest.Builder().build()
        val bannerventanaConsulta2 =
            findViewById<AdView>(R.id.activity_mapa_propinaAds)
        bannerventanaConsulta2.loadAd(adRequest2)


        mRecyclerViewSugerencias=findViewById(R.id.reciclerSugerencias)
        mlinearLayoutManager = LinearLayoutManager(this)
        mRecyclerViewSugerencias.layoutManager = this.mlinearLayoutManager
        back.setOnClickListener {
            finish()
        }
    }
    override fun onStart() {
        super.onStart()
        mSugerenciasProvider= SugerenciasProvider()

        var query: Query = mSugerenciasProvider.getModeradorbyCity(PreferenciasGenerales(this!!).getpreferenceCiudadUsuario())


        var options: FirestoreRecyclerOptions<SugerenciasUsuarios?> =
            FirestoreRecyclerOptions.Builder<SugerenciasUsuarios>().setQuery(
                query,
                SugerenciasUsuarios::class.java
            ).build()

        mAdapterSugerencias = AdapterSugerencias(options,this!!)
        mRecyclerViewSugerencias.adapter = this.mAdapterSugerencias
        mAdapterSugerencias.notifyDataSetChanged()
        mAdapterSugerencias.startListening()
    }
}