package com.jomeva.driving.activities.modelos

import android.content.Context

class Preferencias(context: Context) {
    private  var preferenceBache:String="preferenceBache"
    private  var preferenceReten:String="preferenceReten"
    private  var preferenceCamara:String="preferenceCamara"
    private  var preferenceTrancon:String="preferenceTrancon"
    private  var preferencePeligrosa:String="preferencePeligrosa"
    private  var preferenceViaCerrada:String="preferenceViaCerrada"
    private  var preferenceEspeciales:String="preferenceEspeciales"
    private  var preferenceUsuarios:String="preferenceUsuarios"
    //preferenciasAlertas
    private  var preferenceAlertasBache:Boolean=true
    private  var preferencetodasAlertas:Boolean=true
    private  var preferenceAlertasReten:Boolean=true
    private  var preferenceAlertasCamara:Boolean=true
    private  var preferenceAlertasCerrada:Boolean=true
    private  var preferenceAlertasAccidente:Boolean=true
    private  var preferenceAlertasPeligro:Boolean=true
    private  var preferenceAlertasEspecial:Boolean=true
    private  var preferenceAlertasUsuarios:Boolean=true



        var MyDB="MyDB"
        val storage=context.getSharedPreferences(MyDB,0)


  fun getpreferenceBache():String{
      return storage.getString(preferenceBache,"0")!!
  }
    fun setpreferenceBache(bacheDistancia:String){
        storage.edit().putString(preferenceBache,bacheDistancia).apply()
    }
    //retenes
    fun getpreferenceReten():String{
        return storage.getString(preferenceReten,"0")!!
    }
    fun setpreferenceReten(retenDistancia:String){
        storage.edit().putString(preferenceReten,retenDistancia).apply()
    }
    //camara
    fun getpreferenceCamara():String{
        return storage.getString(preferenceCamara,"0")!!
    }
    fun setpreferenceCamara(camaraDistancia:String){
        storage.edit().putString(preferenceCamara,camaraDistancia).apply()
    }
    //via cerrada
    fun getpreferenceViaCerrada():String{
        return storage.getString(preferenceViaCerrada,"0")!!
    }
    fun setpreferenceViaCerrada(cerradaDistancia:String){
        storage.edit().putString(preferenceViaCerrada,cerradaDistancia).apply()
    }
    //accidente
    fun getpreferenceTrancon():String{
        return storage.getString(preferenceTrancon,"0")!!
    }
    fun setpreferenceTrancon(tranconDistancia:String){
        storage.edit().putString(preferenceTrancon,tranconDistancia).apply()
    }
    //peligrosa
    fun getpreferencePeligrosa():String{
        return storage.getString(preferencePeligrosa,"0")!!
    }
    fun setpreferencePeligrosa(peligroDistancia:String){
        storage.edit().putString(preferencePeligrosa,peligroDistancia).apply()
    }
    //especial
    fun getpreferenceEspeciales():String{
        return storage.getString(preferenceEspeciales,"0")!!
    }
    fun setpreferenceEspeciales(especialDistancia:String){
        storage.edit().putString(preferenceEspeciales,especialDistancia).apply()
    }
    //usuarios
    fun getpreferenceUsuarios():String{
        return storage.getString(preferenceUsuarios,"0")!!
    }
    fun setpreferenceUsuarios(usuarioslDistancia:String){
        storage.edit().putString(preferenceUsuarios,usuarioslDistancia).apply()
    }
    //preferencias alertas
    fun setpreferenceTodasAlertas(todasAlertas:Boolean){
       storage.edit().putBoolean("preferencetodasAlertas",todasAlertas).apply()
    }
    fun getpreferencepreferencetodasAlertas():Boolean {
        return storage.getBoolean("preferencetodasAlertas",true)!!
    }
    fun setpreferenceAlertasReten(todasAlertas:Boolean){
        storage.edit().putBoolean("preferenceAlertasReten",todasAlertas).apply()
    }
    fun getpreferenceAlertasReten():Boolean {
        return storage.getBoolean("preferenceAlertasReten",true)!!
    }
    fun setpreferenceAlertasCamara(todasAlertas:Boolean){
        storage.edit().putBoolean("preferenceAlertasCamara",todasAlertas).apply()
    }
    fun getpreferenceAlertasCamara():Boolean {
        return storage.getBoolean("preferenceAlertasCamara",true)!!
    }
    fun setpreferenceAlertasCerrada(todasAlertas:Boolean){
        storage.edit().putBoolean("preferenceAlertasCerrada",todasAlertas).apply()
    }
    fun getpreferenceAlertasCerrada():Boolean {
        return storage.getBoolean("preferenceAlertasCerrada",true)!!
    }
    fun setpreferenceAlertasAccidente(todasAlertas:Boolean){
        storage.edit().putBoolean("preferenceAlertasAccidente",todasAlertas).apply()
    }
    fun getpreferenceAlertasAccidente():Boolean {
        return storage.getBoolean("preferenceAlertasAccidente",true)!!
    }
    fun setpreferenceAlertasPeligro(todasAlertas:Boolean){
        storage.edit().putBoolean("preferenceAlertasPeligro",todasAlertas).apply()
    }
    fun getpreferenceAlertasPeligro():Boolean {
        return storage.getBoolean("preferenceAlertasPeligro",true)!!
    }
    fun setpreferenceAlertasEspecial(todasAlertas:Boolean){
        storage.edit().putBoolean("preferenceAlertasEspecial",todasAlertas).apply()
    }
    fun getpreferenceAlertasEspecial():Boolean {
        return storage.getBoolean("preferenceAlertasEspecial",true)!!
    }
    fun setpreferenceAlertasUsuarios(todasAlertas:Boolean){
        storage.edit().putBoolean("preferenceAlertasUsuarios",todasAlertas).apply()
    }
    fun getpreferenceAlertasUsuarios():Boolean {
        return storage.getBoolean("preferenceAlertasUsuarios",true)!!
    }
    fun setpreferenceAlertasBache(todasAlertas:Boolean){
        storage.edit().putBoolean("preferenceAlertasBache",todasAlertas).apply()
    }
    fun getpreferenceAlertasBache():Boolean {
        return storage.getBoolean("preferenceAlertasBache",true)!!
    }
}