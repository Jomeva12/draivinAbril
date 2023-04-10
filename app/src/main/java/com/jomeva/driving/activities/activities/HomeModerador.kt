package com.jomeva.driving.activities.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.MenuInflater
import android.view.View
import android.widget.PopupMenu
import com.jomeva.driving.R
import com.jomeva.driving.activities.adapters.MyPagerAdapterModerador
import com.jomeva.driving.activities.providers.UserProviders
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_home_moderador.*
import kotlinx.android.synthetic.main.activity_home_moderador.tabLayout

class HomeModerador : AppCompatActivity() {
    private lateinit var mUserProviders: UserProviders
    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_moderador)
        mUserProviders= UserProviders()
        mAuth=FirebaseAuth.getInstance()
        getUsuario()
        backToHome.setOnClickListener {
            finish()
        }
        imagenPerfilModerador.setOnClickListener {
            metodoMenu(it)
        }

        val fragmentAdapter = MyPagerAdapterModerador(supportFragmentManager)
        viewPagerModerador.adapter = fragmentAdapter
        tabLayout.setupWithViewPager(viewPagerModerador)
    }
    private fun metodoMenu(it: View) {
        val popup = PopupMenu(this, it)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.menu_moderador, popup.menu)

        /**/
        for (i in 0 until popup.menu.size()) {
            val item = popup.menu.getItem(i)
            val spanString = SpannableString(popup.menu.getItem(i).title.toString())
            spanString.setSpan(
                ForegroundColorSpan(getColor(R.color.white)),
                0,
                spanString.length,
                0
            ) //fix the color to white
            item.title = spanString
        }
        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.sugerencias -> {
                    var intent = Intent(this, SugerenciasActivity::class.java)
                    startActivity(intent)
                    return@setOnMenuItemClickListener true
                }    else -> {
                    return@setOnMenuItemClickListener true
                }
            }
        }
        popup.show()
    }
    private fun getUsuario() {

        mUserProviders.getUsuario(mAuth.uid.toString()).addOnSuccessListener {
            if (it.exists()) {
                if (it.contains("photo")) {
                    var imagenPerfil: String = it.get("photo").toString()
                    if (it.get("photo").toString() == "") {
                        Picasso.get().load(R.drawable.person_24).error(R.drawable.person_24).into(imagenPerfilModerador)
                    } else {
                        Log.d("getUserfoto", "v")
                        Picasso.get().load(imagenPerfil).error(R.drawable.person_24)
                            .into(imagenPerfilModerador)
                    }
                }


            }
        }
    }
}