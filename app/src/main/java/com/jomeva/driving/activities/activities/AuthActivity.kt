package com.jomeva.driving.activities.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.ktx.Firebase
import com.jomeva.driving.R
import com.jomeva.driving.activities.modelos.LogicaFirestoreUsuarios
import com.jomeva.driving.activities.providers.UserProviders
import kotlinx.android.synthetic.main.activity_auth.*
import kotlinx.android.synthetic.main.activity_auth.cargandoAuth
import kotlinx.android.synthetic.main.activity_auth.contrasena
import kotlinx.android.synthetic.main.activity_auth.correo
import kotlinx.android.synthetic.main.activity_auth.registrar
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.cuadro_iniciar_celular.view.*
import kotlinx.android.synthetic.main.cuadro_preguntar.view.*
import kotlinx.android.synthetic.main.cuadro_preguntar.view.afirmativo


//https://driving-404f9.firebaseapp.com/__/auth/handler

class AuthActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var mLogicaFirestoreUsuarios: LogicaFirestoreUsuarios
    lateinit var mUserProviders: UserProviders

    companion object {
        private const val RC_SIGN_IN: Int = 120
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        mLogicaFirestoreUsuarios = LogicaFirestoreUsuarios()
        mUserProviders = UserProviders()
        //Analitica
        val firebaseAnalytics = Firebase.analytics
        val bundle = Bundle()
        bundle.putString("Mensaje", "Ha cargado correctamente")
        firebaseAnalytics.logEvent("InitScreen", bundle)

        mAuth = FirebaseAuth.getInstance()

        registrar.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
        iniciarCorreo.setOnClickListener {
            login()
        }
        btnGoogle.setOnClickListener {
            if (isOnlineNet()) {

                signingInGoogle()
            } else {
                mensajePersonalizado()
            }
        }

        /*iniciarCelular.setOnClickListener {
            mensajeIniciarCelular()
        }*/
        /* enviarcode.setOnClickListener {

                 if (editTextcelular.text.equals("") || editTextcelular.text.length < 10) {
                  *//*   val alerta = AlertDialog.Builder(this)
                        .setTitle("Falta información")
                        .setMessage("El campo de número no debe ser vacío ni tener menos de 10 dígitos")
                        .setIcon(R.drawable.logo_largo_sin_fondo)
                        .setNegativeButton("Aceptar") { dialog: DialogInterface?, which: Int ->
                        }
                    alerta.show()*//*
                    mensajePersonalizadoFaltaInfo()

                } else {
                    val code: String = ccp.selectedCountryCodeWithPlus
                    val intent = Intent(this, ValidarPhoneActivity::class.java)
                    Log.d("codigo+numero", code + editTextcelular.text)
                    intent.putExtra("celular", code + editTextcelular.text)
                    startActivity(intent)
                    finish()
                }
            }*/

    }
    private fun signingInGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.client_id_editado_po_mi))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        googleSignInClient.signOut()

        val signInIntent: Intent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }


    private fun login() {
        val email: String = correo.text.toString().trim()
        val password: String = contrasena.text.toString().trim()
        //lottie loading

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(
                this,
                "El email o la contraseña que ingresaste están vacías",
                Toast.LENGTH_LONG
            ).show()
        } else {
            cargandoAuth.visibility = View.VISIBLE

            mUserProviders.login(email, password)?.addOnCompleteListener { task ->
                //lottie loading salir
                cargandoAuth.visibility = View.GONE
                if (task.isSuccessful) {
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(
                        this,
                        "El email o la contraseña que ingresaste no son correctas",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            Log.d("CAMPO", "email: $email")
            Log.d("CAMPO", "password: $password")
        }
    }

    private fun mensajePersonalizado() {
        val texto = "No tiene conexión a internet"
        val lot = "mundo.json"

        val alertDialog: AlertDialog
        val builder: AlertDialog.Builder =
            AlertDialog.Builder(this!!)
        val v: View = LayoutInflater.from(this).inflate(R.layout.cuadro_preguntar, null)

        builder.setView(v)
        v.textVie.text = texto
        v.lottieAnimationViewModoAlerta.setAnimation(lot)
        builder.setCancelable(false)
        alertDialog = builder.create()
        alertDialog.show()
        v.negativo.visibility = View.GONE
        v.afirmativo.text = "Reintentar"
        v.afirmativo.setOnClickListener {

            if (isOnlineNet()) {
                alertDialog.dismiss()
                signingInGoogle()
            } else {
                alertDialog.dismiss()
                mensajePersonalizado()
            }
        }
    }





    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //if (requestCode == RC_SIGN_IN) {
            //val response = IdpResponse.fromResultIntent(data)
        Log.d("AuthActivityvvvvvv1", "firebaseAuthWithGoogle:" +data)
            if (requestCode == RC_SIGN_IN) {
                cargandoAuth.visibility = View.VISIBLE
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                val exception = task.exception
                //if (task.isSuccessful) {
                    try {
                        // Google Sign In was successful, authenticate with Firebase
                        val account = task.getResult(ApiException::class.java)!!
                        Log.d("AuthActivityvvvvvv", "firebaseAuthWithGoogle:" + account.id)
                        firebaseAuthWithGoogle(account.idToken!!)
                    } catch (e: ApiException) {
                        mensajePersonalizadoLoginGoogle()
                        cargandoAuth.visibility = View.GONE
                        // Google Sign In failed, update UI appropriately
                        Log.w("AuthActivityyyyyy", "Google sign in failed", e)
                    }
             /*   } else {
                    //mensaje de no abrir google
                    mensajePersonalizadoLoginGoogle()
                    cargandoAuth.visibility = View.GONE
                    Log.w("AuthActivity", exception.toString())
                }*/
           // }
        }
    }
    private fun mensajePersonalizadoLoginGoogle() {

        val alertDialog: AlertDialog
        val builder: AlertDialog.Builder =
            AlertDialog.Builder(this!!)
        val v: View =
            LayoutInflater.from(this).inflate(R.layout.mensae_google, null)
        builder.setView(v)

        builder.setCancelable(true)
        alertDialog = builder.create()
        alertDialog.show()

        v.afirmativo.setOnClickListener {

            alertDialog.dismiss()

        }

    }
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    mLogicaFirestoreUsuarios.crearUsuario(this)
                    cargandoAuth.visibility = View.GONE
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("AuthActivity", "signInWithCredential:failure", task.exception)

                }
            }
    }


    fun isOnlineNet(): Boolean {
        try {
            val p = Runtime.getRuntime().exec("ping -c 1 www.google.es")
            val `val` = p.waitFor()
            return `val` == 0
        } catch (e: Exception) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
        return false
    }

}


