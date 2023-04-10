package com.jomeva.driving.activities.util

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import android.view.View
import java.io.IOException

class ReproductorAudio {
    private lateinit var mediaPlayer: MediaPlayer
    private var durationMs = 0
    constructor(){
        mediaPlayer=MediaPlayer()
    }
    fun reproducir(context:Context,v:View,mFileNameRecord:String){
        try {
            Log.d("mFileNameRecord3", "->$mFileNameRecord")
            mediaPlayer.reset()
            mediaPlayer.setDataSource(mFileNameRecord)
            mediaPlayer.prepare()
            durationMs = mediaPlayer.duration

            //mediaPlayer.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        } catch (e: IOException) {
            Log.e("LOG_TAG", "$e prepare() failed")
        }
    }
}