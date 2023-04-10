package com.jomeva.driving.activities.util

import android.content.Context
import android.location.Address
import android.util.Log

class PreferenciasGenerales(context: Context) {
    companion object{
        public var mLista: MutableList<String> = arrayListOf()
        public var idMyChat:String=""
        fun setCiudades(list: List<Address>){

            mLista.clear()
            if (!list[0].subAdminArea.isNullOrBlank()){
                mLista?.add(list[0].subAdminArea)
            }else if (!list[0].locality.isNullOrBlank()){
                mLista?.add(list[0].locality)

            }else if (!list[0].adminArea.isNullOrBlank()){
                mLista?.add(list[0].adminArea)

            }
            Log.d("ciudadesss","$mLista")
            Log.d("ciudadesss2","$list")



        }
        fun getCiudades():MutableList<String>{

            return mLista

        }

        fun setIdChat(id:String){
            idMyChat=id
        }
        fun getIdChat():String{
            return idMyChat
        }
    }
    var MyDBGeneral="MyDB"
    val storage=context.getSharedPreferences(MyDBGeneral,0)

    private  var preferenceUsuario:String=""
    private  var preferencePolitica:String="preferencePolitica"
    private  var preferenceGuiaPoster:String="preferenceGuiaPoster"
    private  var preferenceGuiaSoyModerador:String="preferenceGuiaSoyModerador"
    private  var preferenceGuiaPrimeraPropina:String="preferenceGuiaPrimeraPropina"
    private  var preferenceGuiaMenuHome:String="preferenceGuiaMenuHome"
    private  var preferenceMapa:String="preferenceMapa"
    private  var preferenceUbicacionReporte:String="preferenceUbicacionReporte"
    private  var preferenceTareaModerador:String="preferenceTareaModerador"
    private  var preferenceUtilModerador:String="preferenceUtilModerador"
    private  var preferenceSolicitudModerador:String="preferenceSolicitudModerador"
    private  var preferenceEquipoModerador:String="preferenceEquipoModerador"
    private  var preferenceLat:String="preferenceLat"
    private  var preferenceLon:String="preferenceLon"
    private  var preferenceTiempoApp:String="preferenceTiempoApp"
    private  var preferencePrimerReporte:String="preferencePrimerReporte"

    fun getpreferencePrimerReporte():String{
        return storage.getString(preferencePrimerReporte,"0")!!
    }
    fun setpreferencePrimerReporte(ciudad:String){
        storage.edit().putString(preferencePrimerReporte,ciudad).apply()
    }
    fun getpreferenceCiudadUsuario():String{
        return storage.getString(preferenceUsuario,"")!!
    }
    fun setpreferenceCiudadUsuario(ciudad:String){
        storage.edit().putString(preferenceUsuario,ciudad).apply()
    }
    fun getpreferencePolitica():String{
        return storage.getString(preferencePolitica,"false")!!
    }
    fun setpreferencePolitica(bolean:String){
        storage.edit().putString(preferencePolitica,bolean).apply()
    }
    fun getpreferenceGuiaPoster():String{
        return storage.getString(preferenceGuiaPoster,"false")!!
    }
    fun setpreferenceGuiaPoster(bolean:String){
        storage.edit().putString(preferenceGuiaPoster,bolean).apply()
    }
    fun getpreferenceGuiaSoyModerador():String{
        return storage.getString(preferenceGuiaSoyModerador,"false")!!
    }
    fun setpreferenceGuiaSoyModerador(bolean:String){
        storage.edit().putString(preferenceGuiaSoyModerador,bolean).apply()
    }
    fun getpreferenceGuiaPrimeraPropina():String{
        return storage.getString(preferenceGuiaPrimeraPropina,"false")!!
    }
    fun setpreferenceGuiaPrimeraPropina(bolean:String){
        storage.edit().putString(preferenceGuiaPrimeraPropina,bolean).apply()
    }
    fun getpreferenceGuiaMenuHome():String{
        return storage.getString(preferenceGuiaMenuHome,"false")!!
    }
    fun setpreferenceGuiaMenuHome(bolean:String){
        storage.edit().putString(preferenceGuiaMenuHome,bolean).apply()
    }
    fun getpreferenceMapa():String{
        return storage.getString(preferenceMapa,"dia")!!
    }
    fun setpreferenceMapa(bolean:String){
        storage.edit().putString(preferenceMapa,bolean).apply()
    }
    fun getpreferenceUbicacionReporte():String{
        return storage.getString(preferenceUbicacionReporte,"false")!!
    }
    fun setpreferenceUbicacionReporte(bolean:String){
        storage.edit().putString(preferenceUbicacionReporte,bolean).apply()
    }
    fun getpreferenceTareaModerador():String{
        return storage.getString(preferenceTareaModerador,"false")!!
    }
    fun setpreferenceTareaModerador(bolean:String){
        storage.edit().putString(preferenceTareaModerador,bolean).apply()
    }
    fun getpreferenceUtilModerador():String{
        return storage.getString(preferenceUtilModerador,"false")!!
    }
    fun setpreferenceUtilModerador(bolean:String){
        storage.edit().putString(preferenceUtilModerador,bolean).apply()
    }
    fun getpreferenceSolicitudModerador():String{
        return storage.getString(preferenceSolicitudModerador,"false")!!
    }
    fun setpreferenceSolicitudModerador(bolean:String){
        storage.edit().putString(preferenceSolicitudModerador,bolean).apply()
    }
    fun getpreferenceEquipoModerador():String{
        return storage.getString(preferenceEquipoModerador,"false")!!
    }
    fun setpreferenceEquipoModerador(bolean:String){
        storage.edit().putString(preferenceEquipoModerador,bolean).apply()
    }
    fun getpreferenceLat():String{
        return storage.getString(preferenceLat,"0")!!
    }
    fun setpreferenceLat(bolean:String){
        storage.edit().putString(preferenceLat,bolean).apply()
    }
    fun getpreferenceLon():String{
        return storage.getString(preferenceLon,"0")!!
    }
    fun setpreferenceLon(bolean:String){
        storage.edit().putString(preferenceLon,bolean).apply()
    }
    fun getpreferenceTiempoApp():String{
        return storage.getString(preferenceTiempoApp,"0")!!
    }
    fun setpreferenceTiempoApp(bolean:String){
        storage.edit().putString(preferenceTiempoApp,bolean).apply()
    }


}