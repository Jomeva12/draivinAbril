package com.jomeva.driving.activities.activities

import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import com.jomeva.driving.R
import com.jomeva.driving.activities.adapters.OptionsPagerAdapter
import com.jomeva.driving.activities.providers.ImageProvider
import com.jomeva.driving.activities.util.ExtensionFile
import com.jomeva.driving.activities.util.ShadowTransformer
import kotlinx.android.synthetic.main.activity_confirm_image_send.*
import java.util.*
import kotlin.collections.ArrayList





class ConfirmImageSendActivity : AppCompatActivity() {
    lateinit var contex:Context

    lateinit var messages: com.jomeva.driving.activities.modelos.Message

    lateinit var data:ArrayList<String>
      lateinit var idChat:String
    lateinit var idReceiver:String
     lateinit var idSender:String
    lateinit var intencion:String
     lateinit var mImageProvider: ImageProvider
   lateinit var  pagerAdapter: OptionsPagerAdapter
    companion object{
        var mensajes=com.jomeva.driving.activities.modelos.Message()
        var mExtraUserId1=""
        var mExtraUserId2=""
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_image_send)
        setStatusBar()
        mImageProvider=ImageProvider()
        data= intent.getStringArrayListExtra("data") as ArrayList<String>
        idChat= intent?.getStringExtra("idChat").toString()
        messages=com.jomeva.driving.activities.modelos.Message()
        idReceiver= intent?.getStringExtra("idReceiver").toString()
        intencion= intent?.getStringExtra("intencion").toString()
        idSender= intent?.getStringExtra("idSender").toString()
       // if (data!=null){

    //for (i in 0..data.size-1){
        Log.d("intencion", "mensaje: " + intencion)


if (intencion=="enviaImagen"){
    var m=com.jomeva.driving.activities.modelos.Message()
    m.idChat=idChat
    m.idSender=idSender
    m.idReceiver=idReceiver
    m.isViewed=false
    m.timestamp= Date().time


    // m.url=data[i].toString()
    // m.url=mImageFile
    if (ExtensionFile.isImageFile(data[0])){
        m.type="imagen"
        m.message="\uD83D\uDCF7"
    }else if (ExtensionFile.isVideoFile(data[0])){
        m.type="video"
        m.message="\uD83C\uDFA5"

    }
    ConfirmImageSendActivity.mensajes=m
    mExtraUserId1=idSender
    mExtraUserId2=idReceiver
    Toast.makeText(this,"alerta",Toast.LENGTH_LONG).show()
    Log.d("XXXXXXXXXXXXXXXX", "mensaje: " + messages)

}else if (intencion=="verImagen"){

}
        pagerAdapter= OptionsPagerAdapter(
            applicationContext,
            supportFragmentManager,
            dpPixels(2,this),
            data,intencion )
    //}
//}


        val shadowTransformer=ShadowTransformer(viewPagerFotos,pagerAdapter)
        shadowTransformer.enableScaling(true)
        viewPagerFotos.adapter=pagerAdapter
        viewPagerFotos.setPageTransformer(false,shadowTransformer)
    }
   /* fun setMessage(position: Int, message: String?) {
        messages[position].message=message
    }*/
    fun send() {

        /*if (ContextCompat.checkSelfPermission(
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                MY_READ_EXTERNAL_REQUEST
            )
        }*/
        /*for (i in messages.indices) {
            Log.d("PRUEBA", "Comentario m: " + messages[i])
        }*/
       // mImageProvider.uploadMultiple(this,messages)
    }

    public fun dpPixels(dp:Int,context: Context):Float{
        return dp*(context.resources.displayMetrics.density)
    }
    private fun setStatusBar(){
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            window.statusBarColor=resources.getColor(R.color.fullBlack,theme)

        }else if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.LOLLIPOP){
            window.statusBarColor=resources.getColor(R.color.fullBlack)

        }
    }
}