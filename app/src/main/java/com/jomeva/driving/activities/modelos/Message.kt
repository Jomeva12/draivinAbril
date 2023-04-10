package com.jomeva.driving.activities.modelos

class Message {
    var id: String? = null
    var idSender: String? = null
    var idReceiver: String? = null
    var idChat: String? = null
    var url: String? = ""
    var type: String? = ""
    var message: String? = ""
    var nota: String? = ""
    var timestamp: Long = 0
    var isViewed = false
    public var nameFile: String = ""
    constructor() {}
    constructor(
        id: String?,
        idSender: String?,
        idReceiver: String?,
        idChat: String?,
        url: String?,
        type: String?,
        message: String?,
        timestamp: Long,
        isViewed: Boolean
    ) {
        this.id = id
        this.idSender = idSender
        this.idReceiver = idReceiver
        this.idChat = idChat
        this.url = url
        this.type = type
        this.message = message
        this.timestamp = timestamp
        this.isViewed = isViewed
    }

    override fun toString(): String {
        return "Message(id=$id, idSender=$idSender, idReceiver=$idReceiver, idChat=$idChat, url=$url, type=$type, message=$message, timestamp=$timestamp, isViewed=$isViewed)"
    }


}
