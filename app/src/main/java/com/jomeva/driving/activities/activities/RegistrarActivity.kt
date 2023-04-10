package com.jomeva.driving.activities.activities

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.jomeva.driving.R
import com.jomeva.driving.activities.modelos.LogicaFirestoreUsuarios

import com.jomeva.driving.activities.providers.ImageProvider
import com.jomeva.driving.activities.util.FileUtil
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.regitrar_activity.*
import java.io.File
import java.util.*


class RegistrarActivity : AppCompatActivity() {
    //////////Foto//////////////////////////
    private lateinit var mOptions: Array<CharSequence>
    private lateinit var mBuilderSelector: AlertDialog.Builder
    private lateinit var mAbsolutePhotoPath: String
    private lateinit var mPhotoPath: String
    private var mPhotoFile: File? = null
    private var PHOTO_REQUEST_CODE_PERFIL = 3
    private var GALLERY_REQUEST_CODE_PERFIL = 1
    private var mFile1: File? = null
    private lateinit var mImageProvider: ImageProvider

    /////////////////////////////////
    private lateinit var mExtraUid: String
    private lateinit var mExtraPhone: String
    private lateinit var photo: String
    private lateinit var mLogicaFirestoreUsuarios: LogicaFirestoreUsuarios
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.regitrar_activity)
        mExtraUid = intent.getStringExtra("uid").toString()
        mExtraPhone = intent.getStringExtra("phone").toString()
        mImageProvider = ImageProvider()
        photo = ""
        mLogicaFirestoreUsuarios = LogicaFirestoreUsuarios()

        registrar.setOnClickListener {
            actualizarPerfil()
        }
        mBuilderSelector = AlertDialog.Builder(this)
        val title = mBuilderSelector.setTitle("Selecciona una opción")
        mOptions = arrayOf<CharSequence>("Galería", "Cámara")
        imagenPerfil.setOnClickListener {
            selecionarOpciones(GALLERY_REQUEST_CODE_PERFIL, PHOTO_REQUEST_CODE_PERFIL, 1)
        }
        imagenPerfil2.setOnClickListener {
            selecionarOpciones(GALLERY_REQUEST_CODE_PERFIL, PHOTO_REQUEST_CODE_PERFIL, 1)
        }
    }


    private fun actualizarPerfil() {

        val nombre = nombre.text.toString()
        if (nombre != "") {
            if (mFile1 != null) {
                saveImagePerfil(mFile1!!,nombre)
            } else
                if (mPhotoFile != null) {
                    saveImagePerfil(mPhotoFile!!,nombre)
                } else {
                    mLogicaFirestoreUsuarios.registrarEnBdPhone(this,mExtraUid, mExtraPhone, nombre, photo)

                }
        } else {
            Toast.makeText(this, "El nombre no debe ser vacío", Toast.LENGTH_SHORT).show()
        }
    }

    private fun selecionarOpciones(GALLERY_REQUEST_CODE: Int, PHOTO_REQUEST_CODE: Int, x: Int) {
        mBuilderSelector.setItems(mOptions) { _, i ->
            if (i == 0) {
                if (x == 1) {
                    openGalery(GALLERY_REQUEST_CODE)
                    println("galeria perfil")
                }
            } else {
                if (i == 1) {
                    if (x == 1) {
                        println("foto perfil $PHOTO_REQUEST_CODE")
                        tomarFoto(PHOTO_REQUEST_CODE)
                    }
                }
            }
        }
        mBuilderSelector.show()
    }

    private fun openGalery(GALLERY_REQUEST_CODE: Int) {
        val galeria = Intent(Intent.ACTION_GET_CONTENT)
        galeria.setType("image/*")
        startActivityForResult(galeria, GALLERY_REQUEST_CODE)
    }

    private fun tomarFoto(PHOTO_REQUEST_CODE: Int) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            val photoFile: File?
            try {
                photoFile = createPhotoFile(PHOTO_REQUEST_CODE)
                if (photoFile != null) {
                    mPhotoFile = photoFile
                    val photoUri: Uri = FileProvider.getUriForFile(
                        this,
                        "com.example.driving.activities",
                        photoFile
                    )
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                    startActivityForResult(intent, PHOTO_REQUEST_CODE)
                }
            } catch (e: java.lang.Exception) {
                Toast.makeText(this, "Hubo un error ${e.message}", Toast.LENGTH_SHORT).show()
                println("errror del archivo ${e.message}")
            }


        }
    }

    private fun createPhotoFile(requestCode: Int): File {
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val photoFile: File = File.createTempFile("${Date()}_photo", ".jpg", storageDir)
        if (requestCode == PHOTO_REQUEST_CODE_PERFIL) {
            mPhotoPath = "file:${photoFile.absolutePath}"
            mAbsolutePhotoPath = photoFile.absolutePath
            mPhotoFile = photoFile
            println("Entrando a foto perfil")
        }
        return photoFile
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PHOTO_REQUEST_CODE_PERFIL && resultCode == RESULT_OK) {
            Picasso.get().load(mPhotoPath).error(R.drawable.ic_cam).into(imagenPerfil)
        }
        /*Selecionar desde la galería*/
        if (requestCode == GALLERY_REQUEST_CODE_PERFIL && resultCode == RESULT_OK) {
            try {
                data?.data?.let { data ->
                    mFile1 = FileUtil.from(this, data)
                    println("URL->galeriaPerfil-> $data")
                    imagenPerfil.setImageBitmap(BitmapFactory.decodeFile(mFile1!!.absolutePath))
                }

            } catch (e: Exception) {
                Log.d("error", "se  produjo un error")
                Toast.makeText(this, "Se produjo un error", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun saveImagePerfil(tipodefoto1: File,nombreUser:String) {

        val nombre="${Date().time}.jpg"
        mImageProvider.save(this, tipodefoto1,nombre).addOnCompleteListener { tarea1 ->
            if (tarea1.isSuccessful) {
                mImageProvider.getStorage().downloadUrl.addOnSuccessListener { uriPerfil ->
                   val urlPerfil = uriPerfil.toString()

                    mLogicaFirestoreUsuarios.registrarEnBdPhone(
                        this,
                        mExtraUid,
                        mExtraPhone,
                        nombreUser,
                        urlPerfil
                    )

                   // mLogicaFirestoreUsuarios.crearUsuario(mExtraUid, mExtraPhone, nombre, urlPerfil)
                    val intent=Intent(this,HomeActivity::class.java)
                    startActivity(intent)
                }
            }
        }


    }

}