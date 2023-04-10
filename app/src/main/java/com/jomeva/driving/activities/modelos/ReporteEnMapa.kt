package com.jomeva.driving.activities.modelos

class ReporteEnMapa {
    public var idReporte:String=""
    public var uid:String=""
    public var categoria:String=""

    public var ciudad:String=""
    public var lat:String=""
    public var lng:String=""
    public var foto:String=""
    public var descripcion:String=""
    public var nPersonasRating:String=""
    public var raitingTotal:String=""
    public var fecha: Long? = null
    public var confiabilidad:String=""


    constructor()
    constructor(
        idReporte: String,
        uid: String,
        categoria: String,

        ciudad: String,
        lat: String,
        lng: String,
        foto: String,
        descripcion: String,
        nPersonasRating: String,
        raitingTotal: String,
        fecha: Long?,
        confiabilidad: String
    ) {
        this.idReporte = idReporte
        this.uid = uid
        this.categoria = categoria

        this.ciudad = ciudad
        this.lat = lat
        this.lng = lng
        this.foto = foto
        this.descripcion = descripcion
        this.nPersonasRating = nPersonasRating
        this.raitingTotal = raitingTotal
        this.fecha = fecha
        this.confiabilidad = confiabilidad
    }


}