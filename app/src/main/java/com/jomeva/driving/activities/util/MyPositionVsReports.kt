package com.jomeva.driving.activities.util

import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.jomeva.driving.activities.activities.FragmentTwo
import com.jomeva.driving.activities.modelos.Preferencias
import com.jomeva.driving.activities.modelos.ReportesRecycler
import com.jomeva.driving.activities.providers.ReporteMapaProvider
import com.firebase.geofire.GeoLocation
import com.google.android.gms.maps.model.LatLng
import kotlin.math.atan
import kotlin.math.sqrt

class MyPositionVsReports {

        private var myLat: Double = 0.0
        private var myLon: Double = 0.0
        private var lastPositionLAT: Double = 0.0
        private var lastPositionLON: Double = 0.0
        private var lastIdReport = ""
        private var miAngulo = 0.0
        private lateinit var mReporteMapaProvider: ReporteMapaProvider
        private lateinit var mPreferencias: Preferencias
        public lateinit var posiciones: MutableList<Map<String, GeoLocation>>
        public var mReportesRecycler: ReportesRecycler = ReportesRecycler()
        private var mFragmentTwo = FragmentTwo()

        @RequiresApi(Build.VERSION_CODES.N)
        fun compareToMe(
            context: Context,
            points: MutableList<Map<String, GeoLocation>>,
            mCurrentLatLng: LatLng
        ) {
            myLat = mCurrentLatLng.latitude
            myLon = mCurrentLatLng.longitude
            //Log.d("myDegress","${myLat} $myLon")
            var theta = 0.0
            var myDegress = 0f
            var distancia = 0.0
            var k = ""

            Log.d("myDegress", "${miAngulo} ")
            for ((key, valor) in points[0]) {
                k = key

                val aMap = valor.latitude
                val bMap = valor.longitude
                var a = (aMap - myLat) * (aMap - myLat)
                var b = (bMap - myLon) * (bMap - myLon)
                val div = (aMap - myLat) / (bMap - myLon)
                theta = Math.toDegrees(atan(div))
                distancia = sqrt(a + b) * 100000
            }
            if ((miAngulo > theta - 10) && (miAngulo < theta + 10) && lastIdReport != k) {
                mReporteMapaProvider = ReporteMapaProvider()
                mPreferencias = Preferencias(context)
                mReporteMapaProvider.getReporte(k).addOnSuccessListener {
                    if (it.exists()) {
                        if (it.contains("categoria")) {
                            val categoria = it.getString("categoria")
                            if (distancia <= distanciaPreferencia(categoria).toDouble()) {
                                Toast.makeText(
                                    context,
                                    "$categoria cerca de ${distancia} en tu dirección ${
                                        distanciaPreferencia(categoria)
                                    }",
                                    Toast.LENGTH_SHORT
                                ).show()
                                mReportesRecycler.categoria = categoria.toString()

                                //mFragmentTwo.detectaReportes()
                             // mFragmentTwo.mostrarReportes()
                            }
                        }
                    }
                }
                lastIdReport = k
                Log.d("getReportesActive3", "$miAngulo $miAngulo $k")
            }

        }

        private fun distanciaPreferencia(categoria: String?): String {
            return when (categoria) {
                "Bache" -> {
                    mPreferencias.getpreferenceBache()

                }
                "Especial" -> {
                    mPreferencias.getpreferenceEspeciales()
                }
                "Vía cerrada" -> {
                    mPreferencias.getpreferenceViaCerrada()
                }
                "Accidente-trancón" -> {
                    mPreferencias.getpreferenceTrancon()
                }
                "Zona Peligrosa" -> {
                    mPreferencias.getpreferencePeligrosa()
                }
                "Cámara" -> {
                    mPreferencias.getpreferenceCamara()
                }
                "Retén" -> {
                    mPreferencias.getpreferenceReten()

                }
                else -> {
                    "Sin info"

                }
            }

        }

        fun frontToMe(
            points: MutableList<Map<String, GeoLocation>>,
            mCurrentLatLng: LatLng
        ) {


        }

        fun getMyDegrees(mCurrentLatLng: LatLng) {

            val newPositionLAT = mCurrentLatLng.latitude
            val newPositionLON = mCurrentLatLng.longitude
            var theta = 0.0

            val x = lastPositionLON - newPositionLON
            val y = lastPositionLAT - newPositionLAT
            var div = if (x != 0.0) {
                y / x
            } else {
                0.0
            }

            theta = (Math.toDegrees(atan(div)))
            if (newPositionLAT != lastPositionLAT && newPositionLON != lastPositionLON) {
                lastPositionLAT = newPositionLAT
                lastPositionLON = newPositionLON
            }



            miAngulo = theta
        }

}


