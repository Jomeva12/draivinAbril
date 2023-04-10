package com.jomeva.driving.activities.modelos


class Usuarios {
    public var nombre: String = ""
    public lateinit var autenticacion: String
    public  var uid: String=""
    public var photoPerfil: String = ""
    public var ciudad: String = ""
    public var fecha: Long = 0
    public var lastConection: Long = 0
    public var online: Boolean = false
    public var monedas: Int = 100
    public var nameFile: String = ""
    public var tiempoEnLaApp = "0"


    constructor()
    constructor(
        nombre: String,
        autenticacion: String,
        uid: String,
        photoPerfil: String,

        ciudad: String,
        fecha: Long,
        lastConection: Long,
        online: Boolean,
        monedas: Int
    ) {
        this.nombre = nombre
        this.autenticacion = autenticacion
        this.uid = uid
        this.photoPerfil = photoPerfil

        this.ciudad = ciudad
        this.fecha = fecha
        this.lastConection = lastConection
        this.online = online
        this.monedas = monedas
    }


}