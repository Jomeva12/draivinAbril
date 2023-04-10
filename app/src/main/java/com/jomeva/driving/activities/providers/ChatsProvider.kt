package com.jomeva.driving.activities.providers

import com.jomeva.driving.activities.modelos.Chats
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class ChatsProvider {
    var mCollection: CollectionReference = FirebaseFirestore.getInstance().collection("Chats")

    fun create(chat: Chats) {
       /* mCollection.document(chat.idUser1.toString()).collection("Users").document(chat.idUser2.toString())
        .set(chat)
        mCollection.document(chat.idUser2.toString()).collection("Users").document(chat.idUser1.toString())
            .set(chat)*/
        mCollection.document("${chat.idUser1.toString()}${chat.idUser2.toString()}").set(chat)
    }

    public fun getAll(idUser: String): Query {
        return mCollection.whereArrayContains("ids",idUser)
    }


    public fun getChatUser1User2(idUser1: String,idUser2: String): Query {
        var ids:ArrayList<String> =ArrayList()
        ids.add(idUser1+idUser2)
        ids.add(idUser2+idUser1)
        return mCollection.whereIn("idChats",ids)

    }



}
