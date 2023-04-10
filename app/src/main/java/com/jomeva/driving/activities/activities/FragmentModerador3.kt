package com.jomeva.driving.activities.activities

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jomeva.driving.R
import com.jomeva.driving.activities.adapters.ModeradorAdapter
import com.jomeva.driving.activities.modelos.Moderadores
import com.jomeva.driving.activities.providers.ModeradoresProvider
import com.jomeva.driving.activities.util.PreferenciasGenerales
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.firebase.firestore.Query


class FragmentModerador3 : Fragment() {

lateinit var mModeradoresProvider: ModeradoresProvider
    lateinit var mlinearLayoutManager: LinearLayoutManager
    private lateinit var mModeradorAdapter: ModeradorAdapter
    private lateinit var mRecyclerViewMode: RecyclerView
    private lateinit var cerrarMensaje: ImageView
    private lateinit var cardMensaje: CardView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val mView =  inflater.inflate(R.layout.fragment_moderador3, container, false)

        mRecyclerViewMode=mView.findViewById(R.id.reciclerModeradores)
        mlinearLayoutManager = LinearLayoutManager(context)
        mRecyclerViewMode.layoutManager = this.mlinearLayoutManager


        cerrarMensaje=mView.findViewById(R.id.cerrarMensaje)
        cardMensaje=mView.findViewById(R.id.cardMensaje)
        if (PreferenciasGenerales(context!!).getpreferenceSolicitudModerador()=="false"){
            cardMensaje.visibility=View.VISIBLE
            cerrarMensaje.visibility=View.VISIBLE

        }else{
            cardMensaje.visibility=View.GONE
            cerrarMensaje.visibility=View.GONE
        }
        cerrarMensaje.setOnClickListener {
            PreferenciasGenerales(context!!).setpreferenceSolicitudModerador("true")
            cardMensaje.visibility=View.GONE
            cerrarMensaje.visibility=View.GONE
        }
        val adRequest: AdRequest = AdRequest.Builder().build()
        val bannerventanaConsulta = mView!!.findViewById<AdView>(R.id.fragmentthreeModeradorAds)
        bannerventanaConsulta!!.loadAd(adRequest)
        return mView
    }

    override fun onStart() {
        super.onStart()
        mModeradoresProvider=ModeradoresProvider()
        var query: Query = mModeradoresProvider.getModeradorbyCity(PreferenciasGenerales(context!!).getpreferenceCiudadUsuario())


        var options: FirestoreRecyclerOptions<Moderadores?> =
            FirestoreRecyclerOptions.Builder<Moderadores>().setQuery(
                query,
                Moderadores::class.java
            ).build()
        mModeradorAdapter = ModeradorAdapter(options, context)
        mRecyclerViewMode.adapter = this.mModeradorAdapter
        mModeradorAdapter.notifyDataSetChanged()
        mModeradorAdapter.startListening()
    }
}