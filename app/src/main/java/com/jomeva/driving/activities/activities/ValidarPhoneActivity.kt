package com.jomeva.driving.activities.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jomeva.driving.R
import com.jomeva.driving.activities.modelos.LogicaFirestoreUsuarios
import com.jomeva.driving.activities.providers.AuthProviders
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import kotlinx.android.synthetic.main.activity_validar_phone.*
import java.util.concurrent.TimeUnit

//1
class ValidarPhoneActivity : AppCompatActivity() {
    private lateinit var mAuthProviders: AuthProviders
    private lateinit var mExtraCelular: String
    private  var KEY_VERIFICATION_ID="key_verification_id"
    private lateinit var mLogicaFirestoreUsuarios: LogicaFirestoreUsuarios
    private val mAuth: FirebaseAuth? = null
    var mVerificationId: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_validar_phone)
        mLogicaFirestoreUsuarios= LogicaFirestoreUsuarios()
        mAuthProviders = AuthProviders()
        mExtraCelular = intent.getStringExtra("celular").toString()
        requestCode()
        verficarcode.setOnClickListener {
            if (editTextCode.text.toString()!="" && editTextCode.text.toString().length>=6){
                cargandoAuth.visibility=View.VISIBLE
                signIn()
            }else{
                Toast.makeText(this,"el codigo no es válido",Toast.LENGTH_LONG).show()
            }

        }

        reenviarCode.setOnClickListener {
            Log.d("reenviando code:", "")
            requestCode()
        }

/*
        if (mVerificationId == null && savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }*/
    }
    /*
        override fun onSaveInstanceState(@NonNull outState: Bundle) {
            super.onSaveInstanceState(outState)
            outState.putString(KEY_VERIFICATION_ID, mVerificationId)
            Log.d("mVerific guardan:", "$mVerificationId")
        }
        override fun onRestoreInstanceState(savedInstanceState: Bundle) {
            super.onRestoreInstanceState(savedInstanceState)
            mVerificationId = savedInstanceState.getString(KEY_VERIFICATION_ID)
            Log.d("mVerific restau:", "$mVerificationId")
        }*/
    private fun requestCode() {
        val phoneNumber: String = mExtraCelular
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber,
            60,
            TimeUnit.SECONDS,
            this,
            object : OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                    //Called if it is not needed to enter verification code
                    Log.d("onVerificationCo:", "complet")
                    // signInWithCredential(phoneAuthCredential)
                    val code = phoneAuthCredential.smsCode
                    if (code!=null){
                        editTextCode.setText(code)
                        signIn()
                    }

                    Log.d("codigo:", "$code")
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    //incorrect phone number, verification code, emulator, etc.
                    Log.d("fallando:", "")
                }

                override fun onCodeSent(
                    verificationId: String,
                    forceResendingToken: ForceResendingToken
                ) {
                    //now the code has been sent, save the verificationId we may need it
                    super.onCodeSent(verificationId, forceResendingToken)
                    mVerificationId = verificationId
                    Log.d("mVerificationId:", "$mVerificationId")
                }

                override fun onCodeAutoRetrievalTimeOut(verificationId: String) {
                    //called after timeout if onVerificationCompleted has not been called
                    super.onCodeAutoRetrievalTimeOut(verificationId)

                }
            }
        )
    }

    override fun onPause() {
        super.onPause()
        cargandoAuth.visibility=View.GONE
    }
    //573242858661 +16505551234
    fun signIn() {
        val code: String = editTextCode.text.toString()
        Log.d("code: ", "$code")
        Log.d("mVerificationId: sign ", "$mVerificationId")
        if(code!=""){

            mAuthProviders.signInPhone(mVerificationId, code)!!.addOnCompleteListener { esto ->
                if (esto.isSuccessful) {
                    val uid=  mAuthProviders.getId()
                    mLogicaFirestoreUsuarios.getUserByUid(uid).get().addOnSuccessListener {
                        val numberDocuments: Int = it.size()
                        Log.d("numberDocuments", "$numberDocuments")
                        if (numberDocuments > 0) {
                            cargandoAuth.visibility=View.GONE
                            val intent = Intent(this, HomeActivity::class.java)
                            startActivity(intent)
                            Log.d("existe", "$numberDocuments")
                        } else {
                            Log.d("No existe", "$numberDocuments")
                            val intent = Intent(this, RegistrarActivity::class.java)
                            intent.putExtra("uid", uid)
                            intent.putExtra("phone", mExtraCelular)
                            startActivity(intent)
                        }
                    }

                    Log.d("Logueado", "Exitoso")
                } else {
                    Log.d("Logueado", "fallido")

/*
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                Log.d("isSuccess", "bad")*/
                }
            }
        }

    }
}


