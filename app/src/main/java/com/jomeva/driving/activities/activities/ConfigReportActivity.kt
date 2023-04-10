package com.jomeva.driving.activities.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SeekBar
import com.jomeva.driving.R

import com.jomeva.driving.activities.modelos.Preferencias
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import kotlinx.android.synthetic.main.activity_config_report.*

class ConfigReportActivity : AppCompatActivity() {

    private lateinit var mPreferencias: Preferencias

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config_report)

      val adRequest: AdRequest = AdRequest.Builder().build()
        val bannerventanaConsulta = findViewById<AdView>(R.id.activity_config_reportAds)
        bannerventanaConsulta!!.loadAd(adRequest)
        back.setOnClickListener {
            finish()
        }
        mPreferencias = Preferencias(this)

        //bache
        if (mPreferencias.getpreferenceBache() == "0") {
            mPreferencias.setpreferenceBache("${150}")
            seekBache.progress = mPreferencias.getpreferenceBache().toInt().toInt()
        } else {
            seekBache.progress = mPreferencias.getpreferenceBache().toInt().toInt()
        }
        distanciaBache.text = mPreferencias.getpreferenceBache()

        //reten
        if (mPreferencias.getpreferenceReten() == "0") {
            mPreferencias.setpreferenceReten("${250}")
            seekReten.progress = mPreferencias.getpreferenceReten().toInt().toInt()
        } else {
            seekReten.progress = mPreferencias.getpreferenceReten().toInt().toInt()
        }
        distanciaReten.text = mPreferencias.getpreferenceReten()

        //camara
        if (mPreferencias.getpreferenceCamara() == "0") {
            mPreferencias.setpreferenceCamara("${250}")
            seekCamara.progress = mPreferencias.getpreferenceCamara().toInt().toInt()
        } else {
            seekCamara.progress = mPreferencias.getpreferenceCamara().toInt().toInt()
        }
        distanciaCamara.text = mPreferencias.getpreferenceCamara()

        //cerrada
        if (mPreferencias.getpreferenceViaCerrada() == "0") {
            mPreferencias.setpreferenceViaCerrada("${250}")
            seekCerrada.progress = mPreferencias.getpreferenceViaCerrada().toInt().toInt()
        } else {
            seekCerrada.progress = mPreferencias.getpreferenceViaCerrada().toInt().toInt()
        }
        distanciaCerrada.text = mPreferencias.getpreferenceViaCerrada()

//cerrada
        if (mPreferencias.getpreferenceTrancon() == "0") {
            mPreferencias.setpreferenceTrancon("${250}")
            seekAccidente.progress = mPreferencias.getpreferenceTrancon().toInt().toInt()
        } else {
            seekAccidente.progress = mPreferencias.getpreferenceTrancon().toInt().toInt()
        }
        distanciaAccidente.text = mPreferencias.getpreferenceTrancon()

        //peligrosa
        if (mPreferencias.getpreferencePeligrosa() == "0") {
            mPreferencias.setpreferencePeligrosa("${150}")
            seekPeligrosa.progress = mPreferencias.getpreferencePeligrosa().toInt().toInt()
        } else {
            seekPeligrosa.progress = mPreferencias.getpreferencePeligrosa().toInt().toInt()
        }
        distanciaPeligrosa.text = mPreferencias.getpreferencePeligrosa()
        //peligrosa
        if (mPreferencias.getpreferenceEspeciales() == "0") {
            mPreferencias.setpreferenceEspeciales("${150}")
            seekEspecial.progress = mPreferencias.getpreferenceEspeciales().toInt().toInt()
        } else {
            seekEspecial.progress = mPreferencias.getpreferenceEspeciales().toInt().toInt()
        }
        distanciaEspecial.text = mPreferencias.getpreferenceEspeciales()

        //usuarios
        if (mPreferencias.getpreferenceUsuarios() == "0") {
            mPreferencias.setpreferenceUsuarios("${7500}")
            seekUsuarios.progress = mPreferencias.getpreferenceUsuarios().toInt().toInt()
        } else {
            seekUsuarios.progress = mPreferencias.getpreferenceUsuarios().toInt().toInt()
        }
        distanciaUsuarios.text = mPreferencias.getpreferenceUsuarios()

        seekBache.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                // here, you react to the value being set in seekBar
                mPreferencias.setpreferenceBache(progress.toString())
                distanciaBache.text = mPreferencias.getpreferenceBache()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // you can probably leave this empty
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // you can probably leave this empty
            }
        })
        seekReten.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                // here, you react to the value being set in seekBar
                mPreferencias.setpreferenceReten(progress.toString())
                distanciaReten.text = mPreferencias.getpreferenceReten()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // you can probably leave this empty
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // you can probably leave this empty
            }
        })
        seekCamara.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                // here, you react to the value being set in seekBar
                mPreferencias.setpreferenceCamara(progress.toString())
                distanciaCamara.text = mPreferencias.getpreferenceCamara()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // you can probably leave this empty
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // you can probably leave this empty
            }
        })
        seekCerrada.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                // here, you react to the value being set in seekBar
                mPreferencias.setpreferenceViaCerrada(progress.toString())
                distanciaCerrada.text = mPreferencias.getpreferenceViaCerrada()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // you can probably leave this empty
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // you can probably leave this empty
            }
        })
        seekAccidente.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                // here, you react to the value being set in seekBar
                mPreferencias.setpreferenceTrancon(progress.toString())
                distanciaAccidente.text = mPreferencias.getpreferenceTrancon()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // you can probably leave this empty
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // you can probably leave this empty
            }
        })
        seekPeligrosa.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                // here, you react to the value being set in seekBar
                mPreferencias.setpreferencePeligrosa(progress.toString())
                distanciaPeligrosa.text = mPreferencias.getpreferencePeligrosa()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // you can probably leave this empty
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // you can probably leave this empty
            }
        })
        seekEspecial.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                // here, you react to the value being set in seekBar
                mPreferencias.setpreferenceEspeciales(progress.toString())
                distanciaEspecial.text = mPreferencias.getpreferenceEspeciales()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // you can probably leave this empty
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // you can probably leave this empty
            }
        })
        seekUsuarios.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                // here, you react to the value being set in seekBar
                mPreferencias.setpreferenceUsuarios(progress.toString())
                distanciaUsuarios.text = mPreferencias.getpreferenceUsuarios()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // you can probably leave this empty
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // you can probably leave this empty
            }
        })



        //preferencias alertas bache
        if ( mPreferencias.getpreferenceAlertasBache()){
            alertaBache.setImageResource(R.drawable.campana_on)
        }else{
            alertaBache.setImageResource(R.drawable.campana_off)
        }

        alertaBache.setOnClickListener {
            if ( mPreferencias.getpreferenceAlertasBache()){
                mPreferencias.setpreferenceAlertasBache(false)
                alertaBache.setImageResource(R.drawable.campana_off)
            }else{
                mPreferencias.setpreferenceAlertasBache(true)
                alertaBache.setImageResource(R.drawable.campana_on)
            }
        }

//preferencias alertas reten
        if ( mPreferencias.getpreferenceAlertasReten()){
            alertaReten.setImageResource(R.drawable.campana_on)
        }else{
            alertaReten.setImageResource(R.drawable.campana_off)
        }

        alertaReten.setOnClickListener {
            if ( mPreferencias.getpreferenceAlertasReten()){
                mPreferencias.setpreferenceAlertasReten(false)
                alertaReten.setImageResource(R.drawable.campana_off)
            }else{
                mPreferencias.setpreferenceAlertasReten(true)
                alertaReten.setImageResource(R.drawable.campana_on)
            }
        }
//preferencias alertas camara
        if ( mPreferencias.getpreferenceAlertasCamara()){
            alertaCamara.setImageResource(R.drawable.campana_on)
        }else{
            alertaCamara.setImageResource(R.drawable.campana_off)
        }

        alertaCamara.setOnClickListener {
            if ( mPreferencias.getpreferenceAlertasCamara()){
                mPreferencias.setpreferenceAlertasCamara(false)
                alertaCamara.setImageResource(R.drawable.campana_off)
            }else{
                mPreferencias.setpreferenceAlertasCamara(true)
                alertaCamara.setImageResource(R.drawable.campana_on)
            }
        }
        //preferencias alertas CERRADA
        if ( mPreferencias.getpreferenceAlertasCerrada()){
            alertaCerrada.setImageResource(R.drawable.campana_on)
        }else{
            alertaCerrada.setImageResource(R.drawable.campana_off)
        }

        alertaCerrada.setOnClickListener {
            if ( mPreferencias.getpreferenceAlertasCerrada()){
                mPreferencias.setpreferenceAlertasCerrada(false)
                alertaCerrada.setImageResource(R.drawable.campana_off)
            }else{
                mPreferencias.setpreferenceAlertasCerrada(true)
                alertaCerrada.setImageResource(R.drawable.campana_on)
            }
        }
        //preferencias alertas accidente
        if ( mPreferencias.getpreferenceAlertasAccidente()){
            alertaAccidente.setImageResource(R.drawable.campana_on)
        }else{
            alertaAccidente.setImageResource(R.drawable.campana_off)
        }

        alertaAccidente.setOnClickListener {
            if ( mPreferencias.getpreferenceAlertasAccidente()){
                mPreferencias.setpreferenceAlertasAccidente(false)
                alertaAccidente.setImageResource(R.drawable.campana_off)
            }else{
                mPreferencias.setpreferenceAlertasAccidente(true)
                alertaAccidente.setImageResource(R.drawable.campana_on)
            }
        }
        //preferencias alertas peligro
        if ( mPreferencias.getpreferenceAlertasPeligro()){
            alertaPeligrosa.setImageResource(R.drawable.campana_on)
        }else{
            alertaPeligrosa.setImageResource(R.drawable.campana_off)
        }

        alertaPeligrosa.setOnClickListener {
            if ( mPreferencias.getpreferenceAlertasPeligro()){
                mPreferencias.setpreferenceAlertasPeligro(false)
                alertaPeligrosa.setImageResource(R.drawable.campana_off)
            }else{
                mPreferencias.setpreferenceAlertasPeligro(true)
                alertaPeligrosa.setImageResource(R.drawable.campana_on)
            }
        }
        //preferencias alertas especial
        if ( mPreferencias.getpreferenceAlertasEspecial()){
            alertaEspecial.setImageResource(R.drawable.campana_on)
        }else{
            alertaEspecial.setImageResource(R.drawable.campana_off)
        }

        alertaEspecial.setOnClickListener {
            if ( mPreferencias.getpreferenceAlertasEspecial()){
                mPreferencias.setpreferenceAlertasEspecial(false)
                alertaEspecial.setImageResource(R.drawable.campana_off)
            }else{
                mPreferencias.setpreferenceAlertasEspecial(true)
                alertaEspecial.setImageResource(R.drawable.campana_on)
            }
        }
        //preferencias alertas usuarios
        if ( mPreferencias.getpreferenceAlertasUsuarios()){
            alertaUsuarios.setImageResource(R.drawable.campana_on)
        }else{
            alertaUsuarios.setImageResource(R.drawable.campana_off)
        }
        alertaUsuarios.setOnClickListener {
            if ( mPreferencias.getpreferenceAlertasUsuarios()){
                mPreferencias.setpreferenceAlertasUsuarios(false)
                alertaUsuarios.setImageResource(R.drawable.campana_off)
            }else{
                mPreferencias.setpreferenceAlertasUsuarios(true)
                alertaUsuarios.setImageResource(R.drawable.campana_on)
            }
        }

    }
}