package com.jomeva.driving.activities.activities

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.*
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.jomeva.driving.R
import kotlinx.android.synthetic.main.activity_vista_web.*

class VistaWeb : AppCompatActivity() {
    private lateinit var url:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vista_web)
        backToHome.setOnClickListener {
            finish()
        }
        val adRequest: AdRequest = AdRequest.Builder().build()
        activityperfilpostAds.loadAd(adRequest)

        url=intent.getStringExtra("url").toString()

        webView.webViewClient=object :WebViewClient(){

        }
        webView.webChromeClient=object : WebChromeClient(){

        }
       val setting=webView.settings
        setting.javaScriptEnabled=true
        webView.loadUrl(url)
    }
}