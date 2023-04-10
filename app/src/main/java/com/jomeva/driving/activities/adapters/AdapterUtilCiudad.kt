package com.jomeva.driving.activities.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.jomeva.driving.R
import com.jomeva.driving.activities.modelos.UtilCiudad
import com.jomeva.driving.activities.providers.PicoYPlacaProvider
import com.jomeva.driving.activities.util.PreferenciasGenerales
import kotlinx.android.synthetic.main.fila_util_ciudad.view.*

class AdapterUtilCiudad : RecyclerView.Adapter<AdapterUtilCiudad.ViewHolder> {
    private var modelo: MutableList<UtilCiudad>
    private var inflater: LayoutInflater
    private var context: Context? = null
    private var ultimoId: String = ""
    private lateinit var mPicoYPlacaProvider: PicoYPlacaProvider
    
    constructor(context: Context, modelo: MutableList<UtilCiudad>) : super() {
        this.modelo = modelo
        this.inflater = LayoutInflater.from(context)
        this.context = context

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.titulo.text = modelo[position].titulo.toString()

        holder.itemView.titulo.setOnClickListener {
            Log.d("tamañoo","${modelo[position].titulo}")
            if( holder.itemView.tarjeta.isVisible   ){

                holder.itemView.tarjeta.visibility=View.GONE
            }else{


                holder.itemView.tarjeta.visibility=View.VISIBLE
                if (holder.itemView.titulo.text=="Pico y placa"){
                    getPicoYplaca(holder)
                }else   if (holder.itemView.titulo.text=="Clínicas"){
                    Toast.makeText(context,"Pronto habilitaremos este item",Toast.LENGTH_LONG).show()
                    holder.itemView.picoYPlacaLinear.visibility=View.GONE
                }else   if (holder.itemView.titulo.text=="Ambulancias"){
                    Toast.makeText(context,"Pronto habilitaremos este item",Toast.LENGTH_LONG).show()
                    holder.itemView.picoYPlacaLinear.visibility=View.GONE
                }else   if (holder.itemView.titulo.text=="Tarifas"){
                    Toast.makeText(context,"Pronto habilitaremos este item",Toast.LENGTH_LONG).show()
                    holder.itemView.picoYPlacaLinear.visibility=View.GONE
                }else   if (holder.itemView.titulo.text=="Obras activas"){
                    Toast.makeText(context,"Pronto habilitaremos este item",Toast.LENGTH_LONG).show()
                    holder.itemView.picoYPlacaLinear.visibility=View.GONE
                } else   if (holder.itemView.titulo.text=="Grúas"){
                    Toast.makeText(context,"Pronto habilitaremos este item",Toast.LENGTH_LONG).show()
                    holder.itemView.picoYPlacaLinear.visibility=View.GONE
                }else   if (holder.itemView.titulo.text=="Mecánicos"){
                    Toast.makeText(context,"Pronto habilitaremos este item",Toast.LENGTH_LONG).show()
                    holder.itemView.picoYPlacaLinear.visibility=View.GONE
                }

            }

        }
    }

    private fun getPicoYplaca(holder: ViewHolder) {
        mPicoYPlacaProvider = PicoYPlacaProvider()
        mPicoYPlacaProvider.getPicoYplacaByCity(PreferenciasGenerales(context!!).getpreferenceCiudadUsuario()).get().addOnSuccessListener {
            Log.d("fafafafa","${PreferenciasGenerales(context!!).getpreferenceCiudadUsuario()}")
            if ( it.size()>0){


            val idPicoYPlaca = it.documents.get(0).getString("idPicoYPlaca").toString()
            val ciudad = it.documents.get(0).getString("ciudad").toString()
            val lunes_taxiT = it.documents.get(0).getString("lunes_taxi").toString()
            val lunes_particularT = it.documents.get(0).getString("lunes_particular").toString()
            val lunes_motosT = it.documents.get(0).getString("lunes_motos").toString()

            val martes_taxiT = it.documents.get(0).getString("martes_taxi").toString()
            val martes_particularT = it.documents.get(0).getString("martes_particular").toString()
            val martes_motosT = it.documents.get(0).getString("martes_motos").toString()

            val miercoles_taxisT = it.documents.get(0).getString("miercoles_taxis").toString()
            val miercoles_particularT =
                it.documents.get(0).getString("miercoles_particular").toString()
            val miercoles_motosT = it.documents.get(0).getString("miercoles_motos").toString()

            val jueves_taxisT = it.documents.get(0).getString("jueves_taxis").toString()
            val jueves_particularT = it.documents.get(0).getString("jueves_particular").toString()
            val jueves_motosT = it.documents.get(0).getString("jueves_motos").toString()

            val viernes_taxisT = it.documents.get(0).getString("viernes_taxis").toString()
            val viernes_particularT = it.documents.get(0).getString("viernes_particular").toString()
            val viernes_motosT = it.documents.get(0).getString("viernes_motos").toString()

               holder.itemView.lunes_taxi.text = "$lunes_taxiT"
               holder.itemView.lunes_motos.setText("$lunes_motosT")
               holder.itemView.lunes_particular.setText("$lunes_particularT")

               holder.itemView.martes_taxi.setText("$martes_taxiT")
               holder.itemView.martes_motos.setText("$martes_motosT")
               holder.itemView.martes_particular.setText("$martes_particularT")

               holder.itemView.miercoles_taxis.setText("$miercoles_taxisT")
               holder.itemView.miercoles_motos.setText("$miercoles_motosT")
               holder.itemView.miercoles_particular.setText("$miercoles_particularT")

               holder.itemView.jueves_taxis.setText("$jueves_taxisT")
               holder.itemView.jueves_motos.setText("$jueves_motosT")
               holder.itemView.jueves_particular.setText("$jueves_particularT")

               holder.itemView.viernes_taxis.setText("$viernes_taxisT")
               holder.itemView.viernes_motos.setText("$viernes_motosT")
               holder.itemView.viernes_particular.setText("$viernes_particularT")
        }
        }
    }

    public class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        init {


        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = inflater.inflate(R.layout.fila_util_ciudad, parent, false)

        return ViewHolder(vista)
    }


    override fun getItemCount(): Int {
        var tam = modelo.size

        return tam
    }
}