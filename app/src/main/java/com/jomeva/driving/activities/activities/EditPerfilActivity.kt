package com.jomeva.driving.activities.activities

import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

import com.jomeva.driving.R
import com.jomeva.driving.activities.modelos.Usuarios
import com.jomeva.driving.activities.providers.ImageProvider
import com.jomeva.driving.activities.providers.PostProvider
import com.jomeva.driving.activities.providers.UserProviders

import com.fxn.pix.Options
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

import kotlinx.android.synthetic.main.activity_perfiledit.*
import java.io.File

import kotlin.collections.ArrayList
import com.fxn.pix.Pix

import android.content.pm.PackageManager
import android.os.*
import android.view.LayoutInflater
import android.view.View
import com.fxn.utility.PermUtil
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import kotlinx.android.synthetic.main.cuadro_mensaje_check.view.*
import kotlinx.android.synthetic.main.cuadro_preguntar.view.*
import kotlinx.android.synthetic.main.fragment_two.*
import java.util.*

class EditPerfilActivity : AppCompatActivity() {

    private lateinit var mImageProvider: ImageProvider
    private lateinit var mprovider: PostProvider
    private lateinit var mUserProviders: UserProviders
    //////////////FotoPost//////////////
     private lateinit var mAuth: FirebaseAuth
     private  var mImageFile: File?= null
    var mReturnValues: ArrayList<String> = ArrayList()
    private var mOptiones: Options? = null
private lateinit var mAlertDialog: AlertDialog
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfiledit)
        mUserProviders = UserProviders()
        mImageProvider = ImageProvider()
        mprovider = PostProvider()
        mAuth = FirebaseAuth.getInstance()

     val adRequest: AdRequest = AdRequest.Builder().build()
        val bannerventanaConsulta = findViewById<AdView>(R.id.activity_perfilEditAds)
        bannerventanaConsulta!!.loadAd(adRequest)
        //***********
        getUsuario()

        //************//https://androidrepo.com/repo/akshay2211-PixImagePicker-android-camera
        mOptiones = Options.init()
            .setRequestCode(100) //Request code for activity results
            .setCount(1) //Number of images to restict selection count
            .setFrontfacing(false) //Front Facing camera on start
            .setPreSelectedUrls(mReturnValues) //Pre selected Image Urls
            .setSpanCount(4) //Span count for gallery min 1 & max 5
            .setMode(Options.Mode.Picture) //Option to select only pictures or videos or both
            .setVideoDurationLimitinSeconds(0) //Duration for video recording
            .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT) //Orientaion
            .setPath("/pix/images")



        editPhotoperfil.setOnClickListener {
            Log.d("cargandoFoto", "nada")
            startPix()

        }




        botoneditPhotoperfil.setOnClickListener {
            startPix()
        }

        btnActualizarPerfil.setOnClickListener {
        if (mImageFile==null){

            if (txtnombreusuario.text.toString()==""){
                Toast.makeText(this, " No se puede actualizar con nombre vacío", Toast.LENGTH_SHORT).show()

            } else{
                mensajePersonalizado()
                ActualizarInfo("", "")
            }
        }else{
            if (txtnombreusuario.text.toString()==""){
                Toast.makeText(this, " No se puede actualizar con nombre vacío", Toast.LENGTH_SHORT).show()

            } else{
                mensajePersonalizado()
                saveImage()
            }

        }

        }
        back.setOnClickListener {
            var intent = Intent(this, PerfilActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun startPix() {
        Pix.start(this, mOptiones);
    }


    override fun onBackPressed() {

        println("detruyendo la activity")
        var intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
   private fun getUsuario() {
        mUserProviders.getUsuario(mAuth.uid.toString()).addOnSuccessListener {
            if (it.exists()) {
                if (it.contains("nombre")) {
                    txtnombreusuario.setText("${it.get("nombre")}")
                }
                if (it.contains("photo")) {
                    var imagenPerfil: String = it.get("photo").toString()
                    if (it.get("photo").toString() == "") {
                        Picasso.get().load(R.drawable.ic_person_24_gray)
                            .error(R.drawable.ic_person_24_gray).into(
                            editPhotoperfil
                        )
                    } else {
                        Picasso.get().load(imagenPerfil).error(R.drawable.ic_person_24_gray)
                            .into(editPhotoperfil)
                    }
                }

            }
        }
    }
    private fun mensajePersonalizado() {
        val alertDialog: AlertDialog
        val builder: AlertDialog.Builder =
            AlertDialog.Builder(this!!)
        val v: View =
            LayoutInflater.from(this).inflate(R.layout.cuadro_mensaje_check, null)
        builder.setView(v)
      /*  val adRequest: AdRequest = AdRequest.Builder().build()
        val bannerventanaConsulta = v!!.findViewById<AdView>(R.id.cuadro_mensaje_checkAds)
        bannerventanaConsulta!!.loadAd(adRequest)*/

        builder.setCancelable(false)
        alertDialog = builder.create()
        alertDialog.show()
mAlertDialog=alertDialog
        Handler(Looper.myLooper()!!).postDelayed({ alertDialog.dismiss()},4000)
        v.cerrarPosterCheck.setOnClickListener {
            alertDialog.dismiss()
        }


    }
    private fun ActualizarInfo(urlPerfil: String, nameFile: String) {
        Log.d("actualizando1","${txtnombreusuario.text.toString()}")
        if (txtnombreusuario.text.toString()!="") {
        val usuarios = Usuarios()
        usuarios.nombre = txtnombreusuario.text.toString()
        usuarios.uid = mAuth.uid.toString()
        usuarios.photoPerfil=urlPerfil
usuarios.nameFile=nameFile

        mUserProviders.upDatePerfil(usuarios).addOnCompleteListener {
            if (it.isSuccessful) {

                Handler(Looper.myLooper()!!).postDelayed({
                    mAlertDialog.dismiss()
                    val intent=Intent(this,PerfilActivity::class.java)
                    startActivity(intent)
                   finish()
               }, 3000)

            } else {
                Toast.makeText(this, "No se pudo actualizar", Toast.LENGTH_SHORT).show()

            }

        }
    }
    }

    private fun saveImage() {
        val nombre="${Date().time}.jpg"
        mImageProvider.save(this, mImageFile!!,nombre).addOnCompleteListener { it ->
            if (it.isSuccessful) {
                val nameFile=mImageProvider.mstorage.name
                mImageProvider.getStorage().downloadUrl.addOnSuccessListener { uriPerfil ->
                    val urlPerfil: String = uriPerfil.toString()
                    ActualizarInfo(urlPerfil,nameFile)

                }
            }else{
               //No se pudo guardar
            }
        }
    }





    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == 100) {
            mReturnValues = data?.getStringArrayListExtra(Pix.IMAGE_RESULTS)!!
           mImageFile = File(mReturnValues[0])
            editPhotoperfil.setImageBitmap(BitmapFactory.decodeFile(mImageFile?.getAbsolutePath()))
        }
}
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            if(requestCode==PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS){
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Pix.start(this, mOptiones)
                } else {
                    Toast.makeText(
                        this,
                        "Por favor concede permisos para acceder a la cámara",
                        Toast.LENGTH_LONG
                    ).show()
                }
               // return

            }
    }




}