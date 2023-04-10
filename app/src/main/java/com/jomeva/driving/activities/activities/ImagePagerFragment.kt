package com.jomeva.driving.activities.activities

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.cardview.widget.CardView
import com.jomeva.driving.R
import com.jomeva.driving.activities.adapters.CardAdapter
import kotlinx.android.synthetic.main.fragment_image_pager.*
import kotlinx.android.synthetic.main.fragment_image_pager.view.*
import java.io.File

import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.jomeva.driving.activities.modelos.Message
import com.jomeva.driving.activities.providers.ImageProvider
import com.jomeva.driving.activities.providers.MessagesProvider
import com.jomeva.driving.activities.providers.UserProviders
import com.jomeva.driving.activities.util.ExtensionFile
import com.jomeva.driving.activities.util.ScaleListener
import com.fxn.utility.PermUtil
import com.google.firebase.auth.FirebaseAuth
import java.util.*


class ImagePagerFragment : Fragment() {
    // TODO: Rename and change types of parameters

    var mCardViewOption: CardView? = null
    var mView: View? = null
    private lateinit var mImageProvider: ImageProvider
    lateinit var mFile: File
    lateinit var messages: Message
    private lateinit var mMessagesProvider: MessagesProvider
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mAlertDialog: AlertDialog

    //zoom a imagen
    private lateinit var detectorZoom: ScaleGestureDetector
    private var xBegin: Float = 0.0f
    private var yBegin: Float = 0.0f
    private lateinit var uri: Uri

    ////
    private lateinit var mUserProvider: UserProviders

    // lateinit var mPicturaFragment:ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_image_pager, container, false)
        mCardViewOption = mView?.findViewById(R.id.optionImagesOption)
        mImageProvider = ImageProvider()

        xBegin = mView!!.pictureFragment.scaleX
        yBegin = mView!!.pictureFragment.scaleY
        var mScaleListener = ScaleListener(mView!!.pictureFragment)
        detectorZoom = ScaleGestureDetector(context!!, mScaleListener)
        mView!!.setOnTouchListener { v, event ->
            Log.d("toque", "$v")
            detectorZoom.onTouchEvent(event)

        }
        //mCardViewOption?.maxCardElevation=mCardViewOption?.cardElevation * CardAdapter.MAX_ELEVATION_FACTOR
        mCardViewOption?.cardElevation
            ?.times(CardAdapter.MAX_ELEVATION_FACTOR)
            ?.let { mCardViewOption?.setMaxCardElevation(it) };
        mMessagesProvider = MessagesProvider()
        mUserProvider = UserProviders()
        mAuth = FirebaseAuth.getInstance()
        val imagenPath = arguments?.getString("imagen")
        val size = arguments?.getInt("size")
        val position = arguments?.getInt("position")
        messages = Message()


        if (ExtensionFile.isImageFile(imagenPath)) {
            mView!!.frameVideo.visibility = View.GONE
            mView!!.pictureFragment.visibility = View.VISIBLE
            if (imagenPath != null) {
                mFile = File(imagenPath)
                mView!!.pictureFragment.setImageBitmap(BitmapFactory.decodeFile(mFile.absolutePath))

            }
        } else if (ExtensionFile.isVideoFile(imagenPath)) {
            mView!!.frameVideo.visibility = View.VISIBLE
            mView!!.pictureFragment.visibility = View.GONE
            mFile = File(imagenPath)
            uri = Uri.fromFile(File(imagenPath))
            // uri = Uri.parse(imagenPath)
            mView!!.videoView.setVideoURI(uri)
        }

        mView!!.frameVideo.setOnClickListener {
            if (!videoView.isPlaying) {
                btnPlay.visibility = View.GONE
                imageVideo.visibility = View.GONE
                videoView.start()
            } else {
                btnPlay.visibility = View.VISIBLE
                imageVideo.visibility = View.VISIBLE
                videoView.pause()
            }
        }

        if (size == 1) {
            mView!!.linearPager.setPadding(0, 0, 0, 0)
            val params: ViewGroup.MarginLayoutParams =
                mView!!.atras.layoutParams as ViewGroup.MarginLayoutParams
            params.leftMargin = 10
            params.topMargin = 35
        }
        mView!!.atras.setOnClickListener {
            activity?.finish()
        }
            mView!!.send.setOnClickListener {
//chequeo permisos
                if (ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){

                }
                if (ExtensionFile.isImageFile(imagenPath)) {
                    saveImage()
                } else if (ExtensionFile.isVideoFile(imagenPath)) {

                }
            }
        return mView
    }

    override fun onPause() {
        super.onPause()
        if (videoView.isPlaying) {
            videoView.pause()
        }
    }

    public fun getCard(): CardView? {
        return mCardViewOption
    }



    private fun saveImage() {

        var nombre = UUID.randomUUID().toString()
        nombre = "$nombre.jpg"

        Thread.sleep(1000)
        val thread = Thread {
            mImageProvider.save(requireContext(), mFile!!, nombre).addOnCompleteListener { it ->
                if (it.isSuccessful) {
                    mImageProvider.getStorage().downloadUrl.addOnSuccessListener { uriPerfil ->
                        val urlImagen: String = uriPerfil.toString()
                        Log.d("urlImagen", "$urlImagen")
                        guardarDescripcion(urlImagen)
                    }
                } else {

                }
            }
        }
        thread.start()
        mAlertDialog.dismiss()
        activity?.finish()
    }

    private fun guardarDescripcion(url: String) {
        var mensaje = Message()
        mensaje = ConfirmImageSendActivity.mensajes
        mensaje.url = url
        var texto = mView?.descripcion?.text.toString()
        if (!texto.isEmpty()) {
            mensaje.message = texto
        }
        mMessagesProvider.create(mensaje).addOnCompleteListener {
            if (it.isSuccessful) {
                /* if (mAlertDialog.isShowing){
                     mAlertDialog.dismiss()
                 }*/

                activity?.finish()
                //getMyInfo()
                //getToken(mensaje)

                //mMessageAdapter.notifyDataSetChanged()
            } else {

            }
        }
    }



    companion object {

        @JvmStatic
        fun newInstance(position: Int, imagenPath: String, size: Int, intencion: String): Fragment {
            val fragment: ImagePagerFragment = ImagePagerFragment()
            Log.d("imagenPath2", "$imagenPath")
            val arg: Bundle = Bundle()
            arg.putInt("position", position)
            arg.putInt("size", size)
            arg.putString("imagen", imagenPath)

            arg.putString("intencion", intencion)
            fragment.arguments = arg
            return fragment
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Pix.start(this, mOptiones)
                Toast.makeText(
                    requireContext(),
                    "Ya tiene permisos",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Por favor concede permisos para acceder a la cámara",
                    Toast.LENGTH_LONG
                ).show()
            }

        }

    }
}