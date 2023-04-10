package com.jomeva.driving.activities.modelos

class Chats {
    var idChats: String? = null
    var idUser1: String? = null
    var idUser2: String? = null
    var isWriting = false
    var timestamp: Long = 0
    var idNotification:Long=0
    lateinit var ids:ArrayList<String>

    constructor()
    constructor(
        idChats: String?,
        idUser1: String?,
        idUser2: String?,
        isWriting: Boolean,
        timestamp: Long,
        idNotification: Long,
        ids: ArrayList<String>
    ) {
        this.idChats = idChats
        this.idUser1 = idUser1
        this.idUser2 = idUser2
        this.isWriting = isWriting
        this.timestamp = timestamp
        this.idNotification = idNotification
        this.ids = ids
    }


}