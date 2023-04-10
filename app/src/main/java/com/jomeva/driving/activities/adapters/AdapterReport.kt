package com.jomeva.driving.activities.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jomeva.driving.R
import com.jomeva.driving.activities.modelos.ReportesRecycler
import kotlinx.android.synthetic.main.cardview_report.view.*

class AdapterReport : RecyclerView.Adapter<AdapterReport.ViewHolder> {
    private var modelo: MutableList<ReportesRecycler>
    private var inflater: LayoutInflater
    private var context: Context? = null
    private var ultimoId:String=""

    constructor(context: Context, modelo: MutableList<ReportesRecycler>) : super() {
        this.modelo = modelo
        this.inflater = LayoutInflater.from(context)
        this.context = context

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        val categoria = modelo[position].categoria
        val distancia = modelo[position].distancia
        ultimoId=modelo[position].idReporte

        holder.itemView.categoriaTVFragment.text = categoria







       // holder.itemView.distanciaTVFragment.text = "$distancia"
    }
    public class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        init {


        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = inflater.inflate(R.layout.cardview_report, parent, false)

        return ViewHolder(vista)
    }


    override fun getItemCount(): Int {
        var tam = modelo.size
        if (tam < 4) {
            tam = modelo.size
        } else {
            tam = 3
        }
        return tam
    }
}