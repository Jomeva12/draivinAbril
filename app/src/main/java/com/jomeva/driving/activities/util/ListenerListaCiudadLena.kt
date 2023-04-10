package com.jomeva.driving.activities.util

import android.content.Context
import android.location.Address

interface ListenerListaCiudadLena {
    fun listenerLista(context: Context,addressList: List<Address>)
}