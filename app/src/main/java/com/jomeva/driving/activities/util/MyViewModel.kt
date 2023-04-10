package com.jomeva.driving.activities.util

import android.content.Context
import androidx.lifecycle.ViewModel

class MyViewModel: ViewModel() {

    private  var contexto: Context?=null
    fun getContexto(): Context? {
        return contexto
    }
    fun setContexto(contexto: Context){
        this.contexto=contexto
    }
}