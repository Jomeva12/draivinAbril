package com.jomeva.driving.activities.providers


import com.jomeva.driving.activities.modelos.Message
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class MessagesProvider {
    var mCollection: CollectionReference = FirebaseFirestore.getInstance().collection("Messages")


    fun create(message: Message): Task<Void> {
        val document = mCollection.document()
        message.id = document.id
        return document.set(message)
    }

    public fun getChatbyId(idChat: String): Query {
        return mCollection.whereEqualTo("idChat", idChat)
            .orderBy("timestamp", Query.Direction.ASCENDING)
    }

    public fun getMessagebychatAndSender(idChat: String, idSender: String): Query {
        return mCollection.whereEqualTo("idChat", idChat).whereEqualTo("idSender", idSender)
            .whereEqualTo("viewed", false)
    }


    public fun getLast3bychatAndSender(idChat: String, idSender: String): Query {
        return mCollection
            .whereEqualTo("idChat", idChat)
            .whereEqualTo("idSender", idSender)
            .whereEqualTo("viewed", false)
            .orderBy("timestamp", Query.Direction.DESCENDING).limit(1)
    }
    public fun getLastMessage(idChat: String): Query {
        return mCollection.whereEqualTo("idChat", idChat).orderBy("timestamp", Query.Direction.DESCENDING).limit(1)

    }

    fun upDateVisto(documento: String, estado: Boolean): Task<Void> {
        val mapa: Map<String, Any> = mutableMapOf("viewed" to estado)
        return mCollection.document(documento).update(mapa)
    }
    public fun deleteMessage(idMensaje: String) : Task<Void> {
        return mCollection.document(idMensaje).delete()
    }

}