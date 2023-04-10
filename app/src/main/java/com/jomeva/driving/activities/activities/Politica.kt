package com.jomeva.driving.activities.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.jomeva.driving.R
import com.jomeva.driving.activities.util.PreferenciasGenerales
import kotlinx.android.synthetic.main.activity_politica.*

class Politica : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_politica)
        if (PreferenciasGenerales(this).getpreferencePolitica()=="true"){
            linearLayoutBack.visibility=View.VISIBLE
            btnPolitica.visibility=View.GONE
            checkbox_id.visibility=View.GONE
        }else{
            linearLayoutBack.visibility=View.GONE
        }
      back.setOnClickListener {
          finish()
      }
        btnPolitica.setOnClickListener {
            if (checkbox_id.isChecked){
                //cargando...
                cargando.visibility=View.VISIBLE
                PreferenciasGenerales(this).setpreferencePolitica("true")
                val intent=Intent(this,HomeActivity::class.java)
                cargando.visibility=View.GONE
                startActivity(intent)
                finish()
            }else{
                Toast.makeText(this,"Debe leer la política para disfrutar de Draivin",Toast.LENGTH_LONG).show()

            }

        }
    }
}