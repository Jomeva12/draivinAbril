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
import com.jomeva.driving.activities.adapters.ModeradorSolicitudAdapter
import com.jomeva.driving.activities.modelos.PeticionModerador
import com.jomeva.driving.activities.providers.PeticionModeradorProvider
import com.jomeva.driving.activities.util.PreferenciasGenerales
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.firebase.firestore.Query

class FragmentModerador4 : Fragment() {
    private lateinit var cerrarMensaje: ImageView
    private lateinit var cardMensaje: CardView
    lateinit var mlinearLayoutManager: LinearLayoutManager
    private lateinit var mRecyclerViewPendientes: RecyclerView
    private lateinit var mModeradorPendienteAdapter: ModeradorSolicitudAdapter
    private lateinit var mPeticionModeradorProvider: PeticionModeradorProvider
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val mView=inflater.inflate(R.layout.fragment_moderador4, container, false)
        mRecyclerViewPendientes=mView.findViewById(R.id.reciclerModeradoresPendientes)
        mlinearLayoutManager = LinearLayoutManager(context)
        mRecyclerViewPendientes.layoutManager = this.mlinearLayoutManager

        cerrarMensaje=mView.findViewById(R.id.cerrarMensaje)
        cardMensaje=mView.findViewById(R.id.cardMensaje)

        if (PreferenciasGenerales(context!!).getpreferenceEquipoModerador()=="false"){
            cardMensaje.visibility=View.VISIBLE
            cerrarMensaje.visibility=View.VISIBLE

        }else{
            cardMensaje.visibility=View.GONE
            cerrarMensaje.visibility=View.GONE
        }
        cerrarMensaje.setOnClickListener {
            PreferenciasGenerales(context!!).setpreferenceEquipoModerador("true")
            cardMensaje.visibility=View.GONE
            cerrarMensaje.visibility=View.GONE
        }
        val adRequest: AdRequest = AdRequest.Builder().build()
        val bannerventanaConsulta = mView!!.findViewById<AdView>(R.id.fragmentfourModeradorAds)
        bannerventanaConsulta!!.loadAd(adRequest)

        return mView
    }
    override fun onStart() {
        super.onStart()
        mPeticionModeradorProvider= PeticionModeradorProvider()
        var query: Query = mPeticionModeradorProvider.getPeticionesByCity(PreferenciasGenerales(context!!).getpreferenceCiudadUsuario())


        var options: FirestoreRecyclerOptions<PeticionModerador?> =
            FirestoreRecyclerOptions.Builder<PeticionModerador>().setQuery(
                query,
                PeticionModerador::class.java
            ).build()
        mModeradorPendienteAdapter = ModeradorSolicitudAdapter(options,context!!)
        mRecyclerViewPendientes.adapter = this.mModeradorPendienteAdapter
        mModeradorPendienteAdapter.notifyDataSetChanged()
        mModeradorPendienteAdapter.startListening()
    }

}