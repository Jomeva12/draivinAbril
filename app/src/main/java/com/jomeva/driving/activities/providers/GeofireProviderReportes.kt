package com.jomeva.driving.activities.providers

import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.GeoQuery
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class GeofireProviderReportes {

    private lateinit var mDatabase: DatabaseReference
    private lateinit var mGeoFire: GeoFire

    constructor() {
        mDatabase = FirebaseDatabase.getInstance().reference.child("reportes_activos")
        mGeoFire = GeoFire(mDatabase)

    }

    public fun saveReporte(idUser: String, latlng: LatLng){
        mGeoFire.setLocation(idUser, GeoLocation(latlng.latitude, latlng.longitude))
    }

    fun removeLocation(idUser: String?) {
        mGeoFire.removeLocation(idUser)
    }
    fun getActiveReports(latLng: LatLng): GeoQuery? {
        val geoQuery: GeoQuery =
            mGeoFire.queryAtLocation(GeoLocation(latLng.latitude, latLng.longitude), 5.0)
        geoQuery.removeAllListeners()
        return geoQuery
    }
    fun removeReporte(idReporte: String?) {
        mGeoFire.removeLocation(idReporte)
    }
}