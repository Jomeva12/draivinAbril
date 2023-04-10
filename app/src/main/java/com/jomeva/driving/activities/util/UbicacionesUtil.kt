package com.jomeva.driving.activities.util

class UbicacionesUtil {
    companion object{
        private lateinit var latLng:com.google.android.gms.maps.model.LatLng
        fun setLatLng(latLng: com.google.android.gms.maps.model.LatLng){
            this.latLng=latLng
        }
        fun getLatLng():com.google.android.gms.maps.model.LatLng{
            return latLng
        }
    }
}