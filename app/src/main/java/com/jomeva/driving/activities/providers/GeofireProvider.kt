package com.jomeva.driving.activities.providers

import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.GeoQuery
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


//aprender y crear database 20210822
class GeofireProvider {
    private lateinit var mDatabase: DatabaseReference
    private lateinit var mGeoFire: GeoFire

    constructor() {
        mDatabase = FirebaseDatabase.getInstance().reference.child("user_activos")
        mGeoFire = GeoFire(mDatabase)

    }

    public fun saveLocation(idUser: String, latlng: LatLng){
        mGeoFire.setLocation(idUser, GeoLocation(latlng.latitude, latlng.longitude))
    }


    fun removeLocation(idUser: String?) {
        mGeoFire.removeLocation(idUser)
    }
    fun getActiveDrivers(latLng: LatLng,distanciaUsuarios:Double): GeoQuery? {
        val geoQuery: GeoQuery =
            mGeoFire.queryAtLocation(GeoLocation(latLng.latitude, latLng.longitude), distanciaUsuarios)
        geoQuery.removeAllListeners()
        return geoQuery
    }
}