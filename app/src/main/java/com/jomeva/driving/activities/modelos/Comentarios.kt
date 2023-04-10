package com.jomeva.driving.activities.modelos

class Comentarios {
    public  var idComentario:String=""
    public  var comentario:String=""
    public  var uid:String=""
    public  var uidPost:String=""
    public  var idPost:String=""
    public var fecha: Long? = null

    constructor()
    constructor(
        idComentario: String,
        comentario: String,
        uid: String,
        uidPost: String,
        idPost: String,
        fecha: Long?
    ) {
        this.idComentario = idComentario
        this.comentario = comentario
        this.uid = uid
        this.uidPost = uidPost
        this.idPost = idPost
        this.fecha = fecha
    }
}