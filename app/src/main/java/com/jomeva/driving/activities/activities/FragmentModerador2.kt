package com.jomeva.driving.activities.activities

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.tasks.Task
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import com.jomeva.driving.R
import com.jomeva.driving.activities.modelos.PicoYPlaca
import com.jomeva.driving.activities.providers.PicoYPlacaProvider
import com.jomeva.driving.activities.util.PreferenciasGenerales
import kotlinx.android.synthetic.main.fragment_moderador2.*

class FragmentModerador2 : Fragment() {
    private lateinit var cerrarMensaje: ImageView
    private lateinit var cardMensaje: CardView
    private lateinit var mPicoYPlacaProvider: PicoYPlacaProvider
    private lateinit var mPicoYPlaca: PicoYPlaca
    private lateinit var registration:ListenerRegistration
    var idPicoYPlaca = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val mView = inflater.inflate(R.layout.fragment_moderador2, container, false)
        mPicoYPlacaProvider = PicoYPlacaProvider()
        mPicoYPlaca = PicoYPlaca()

        cardMensaje = mView.findViewById(R.id.cardMensajeM)
        cerrarMensaje = mView.findViewById(R.id.cerrarMensajeM)
        if (PreferenciasGenerales(context!!).getpreferenceUtilModerador() == "false") {
            cardMensaje.visibility = View.VISIBLE
            cerrarMensaje.visibility = View.VISIBLE

        } else {
            cardMensaje.visibility = View.GONE
            cerrarMensaje.visibility = View.GONE
        }
        cerrarMensaje.setOnClickListener {
            PreferenciasGenerales(context!!).setpreferenceUtilModerador("true")
            cardMensaje.visibility = View.GONE
            cerrarMensaje.visibility = View.GONE

        }

        val btn = mView.findViewById<Button>(R.id.btnActualizar)

        getPicoYplaca()

        btn.setOnClickListener {
            actualizarPicoYplaca()
        }

        val adRequest: AdRequest = AdRequest.Builder().build()
        val bannerventanaConsulta = mView!!.findViewById<AdView>(R.id.fragmenttwoModeradorAds)
        bannerventanaConsulta!!.loadAd(adRequest)

        return mView
    }

    private fun getPicoYplaca() {
         mPicoYPlacaProvider.getPicoYplacaByCity(PreferenciasGenerales(context!!).getpreferenceCiudadUsuario())
            .get().addOnSuccessListener {
                if (it.documents.isNotEmpty()){

                if (it.documents[0].exists()) {
                    idPicoYPlaca = it.documents.get(0).getString("idPicoYPlaca").toString()
                    val ciudad = it.documents.get(0).getString("ciudad").toString()
                    val lunes_taxi = it.documents.get(0).getString("lunes_taxi").toString()
                    val lunes_particular =
                        it.documents.get(0).getString("lunes_particular").toString()
                    val lunes_motos = it.documents.get(0).getString("lunes_motos").toString()

                    val martes_taxi = it.documents.get(0).getString("martes_taxi").toString()
                    val martes_particular =
                        it.documents.get(0).getString("martes_particular").toString()
                    val martes_motos = it.documents.get(0).getString("martes_motos").toString()

                    val miercoles_taxis =
                        it.documents.get(0).getString("miercoles_taxis").toString()
                    val miercoles_particular =
                        it.documents.get(0).getString("miercoles_particular").toString()
                    val miercoles_motos =
                        it.documents.get(0).getString("miercoles_motos").toString()

                    val jueves_taxis = it.documents.get(0).getString("jueves_taxis").toString()
                    val jueves_particular =
                        it.documents.get(0).getString("jueves_particular").toString()
                    val jueves_motos = it.documents.get(0).getString("jueves_motos").toString()

                    val viernes_taxis = it.documents.get(0).getString("viernes_taxis").toString()
                    val viernes_particular =
                        it.documents.get(0).getString("viernes_particular").toString()
                    val viernes_motos = it.documents.get(0).getString("viernes_motos").toString()

                    edit_lunes_taxi.setText(lunes_taxi)
                    edit_lunes_motos.setText(lunes_motos)
                    edit_lunes_particular.setText(lunes_particular)

                    edit_martes_taxi.setText(martes_taxi)
                    edit_martes_motos.setText(martes_motos)
                    edit_martes_particular.setText(martes_particular)

                    edit_miercoles_taxis.setText(miercoles_taxis)
                    edit_miercoles_motos.setText(miercoles_motos)
                    edit_miercoles_particular.setText(miercoles_particular)

                    edit_jueves_taxis.setText(jueves_taxis)
                    edit_jueves_motos.setText(jueves_motos)
                    edit_jueves_particular.setText(jueves_particular)

                    edit_viernes_taxis.setText(viernes_taxis)
                    edit_viernes_motos.setText(viernes_motos)
                    edit_viernes_particular.setText(viernes_particular)
                }
            }
            }
    }

    private fun actualizarPicoYplaca() {
        mPicoYPlaca.idPicoYPlaca = idPicoYPlaca
        mPicoYPlaca.ciudad = PreferenciasGenerales(context!!).getpreferenceCiudadUsuario()
        mPicoYPlaca.lunes_taxi = edit_lunes_taxi.text.toString()
        mPicoYPlaca.lunes_motos = edit_lunes_motos.text.toString()
        mPicoYPlaca.lunes_particular = edit_lunes_particular.text.toString()

        mPicoYPlaca.martes_taxi = edit_martes_taxi.text.toString()
        mPicoYPlaca.martes_motos = edit_martes_motos.text.toString()
        mPicoYPlaca.martes_particular = edit_martes_particular.text.toString()

        mPicoYPlaca.miercoles_taxis = edit_miercoles_taxis.text.toString()
        mPicoYPlaca.miercoles_motos = edit_miercoles_motos.text.toString()
        mPicoYPlaca.miercoles_particular = edit_miercoles_particular.text.toString()

        mPicoYPlaca.jueves_taxis = edit_jueves_taxis.text.toString()
        mPicoYPlaca.jueves_motos = edit_jueves_motos.text.toString()
        mPicoYPlaca.jueves_particular = edit_jueves_particular.text.toString()

        mPicoYPlaca.viernes_taxis = edit_viernes_taxis.text.toString()
        mPicoYPlaca.viernes_motos = edit_viernes_motos.text.toString()
        mPicoYPlaca.viernes_particular = edit_viernes_particular.text.toString()

        mPicoYPlacaProvider.save(mPicoYPlaca).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context, "Guardado", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
       // registration?.remove()
    }

}