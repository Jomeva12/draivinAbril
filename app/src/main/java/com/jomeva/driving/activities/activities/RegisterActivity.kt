package com.jomeva.driving.activities.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jomeva.driving.R
import com.jomeva.driving.activities.modelos.Usuarios
import com.jomeva.driving.activities.providers.UserProviders
import com.jomeva.driving.activities.util.PreferenciasGenerales
import kotlinx.android.synthetic.main.activity_register.*
import java.util.regex.Matcher
import java.util.regex.Pattern


class RegisterActivity : AppCompatActivity() {
    private lateinit var mAuth:FirebaseAuth
    lateinit var mUserProviders: UserProviders
    var mFirestore: FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        mAuth=FirebaseAuth.getInstance()
        mFirestore = FirebaseFirestore.getInstance()
        mUserProviders=UserProviders()

        registrar.setOnClickListener {
            register()
        }
    }
    private fun register() {
        val username: String = nombre.text.toString()
        val email: String = correo.text.toString()
        val password: String = contrasena.text.toString()
        val confirmPassword: String = validarContrasena.text.toString()
        if (username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
            if (isEmailValid(email)) {
                if (password == confirmPassword) {
                    if (password.length >= 6) {
                        createUser(username, email, password)
                    } else {
                        Toast.makeText(
                            this,
                            "La contraseña debe tener al menos 6 caracteres",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(this, "Las contraseña no coinciden", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(
                    this,
                    "Insertaste todos los campos pero el correo no es valido",
                    Toast.LENGTH_LONG
                ).show()
            }
        } else {
            Toast.makeText(this, "Para continuar inserta todos los campos", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun createUser(username: String, email: String, password: String) {
        cargandoAuth.visibility=View.VISIBLE
        mUserProviders.register(email, password)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {


                val id: String = mAuth.currentUser.uid
                val map: MutableMap<String, Any> = HashMap()
               map["autenticacion"] = email
                map["nombre"] = username
                map["monedas"] = "100"
                map["uid"] = id
                map["ciudad"] = PreferenciasGenerales(this!!).getpreferenceCiudadUsuario()
                map["photo"] = ""
                map["tiempoEnLaApp"] = "0"



                mFirestore!!.collection("usuarios").document(id).set(map).addOnCompleteListener(
                    OnCompleteListener<Void?> { task ->
                        if (task.isSuccessful) {
                            cargandoAuth.visibility=View.GONE
                            val intent=Intent(this,HomeActivity::class.java)
                            startActivity(intent)
                            finish()
                            Toast.makeText(
                                this@RegisterActivity,
                                "El usuario se almaceno correctamente en la base de datos",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                this@RegisterActivity,
                                "No se pudo almacenar el usuario en la base de datos",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
            } else {
                Toast.makeText(
                    this@RegisterActivity,
                    "No se pudo registrar el usuario",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    fun isEmailValid(email: String?): Boolean {
        val expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
        val pattern: Pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher: Matcher = pattern.matcher(email)
        return matcher.matches()
    }
}