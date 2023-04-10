package com.jomeva.driving.activities.adapters

import com.jomeva.driving.R
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jomeva.driving.activities.activities.ChatActivity
import com.jomeva.driving.activities.modelos.Chats
import com.jomeva.driving.activities.providers.MessagesProvider
import com.jomeva.driving.activities.providers.UserProviders
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.cardview_chats.view.*


class ChatAdapter(options: FirestoreRecyclerOptions<Chats?>?, var context: Context?) :
    FirestoreRecyclerAdapter<Chats, ChatAdapter.ViewHolder>(options!!) {

    private lateinit var mUserProvider: UserProviders
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mMessagesProvider: MessagesProvider

    override fun onBindViewHolder(holder: ViewHolder, position: Int, chat: Chats) {
        val document: DocumentSnapshot = snapshots.getSnapshot(position)
        val idChat = document.id
        mAuth = FirebaseAuth.getInstance()
        if (mAuth.currentUser!!.uid == chat.idUser1) {
            getUserChat(chat.idUser2!!, holder)

        } else {
            getUserChat(chat.idUser1!!, holder)

        }

        holder.itemView.linearChat.setOnClickListener {

            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("user1", chat.idUser1)
            intent.putExtra("user2", chat.idUser2)
            intent.putExtra("estadoPropina", "false")
            intent.putExtra("idChat", idChat)
            this.context?.startActivity(intent)

        }
        getLastMessage(idChat, holder)
        var sender = ""
        if (mAuth.currentUser!!.uid == chat.idUser1) {
            sender = chat.idUser2.toString()
        } else {
            sender = chat.idUser1.toString()
        }
        getMessagesNoRead(idChat, sender, holder)
    }

    private fun getMessagesNoRead(idChat: String, sender: String, holder: ViewHolder) {
        mMessagesProvider = MessagesProvider()


        Log.d("Nchat", "${idChat}")

        mMessagesProvider.getMessagebychatAndSender(idChat, sender)
            .addSnapshotListener { it, error ->
                val size: Int = it!!.size()
                Log.d("ultimo----->", "${size}")
                Log.d("contador", "${contador++}")

                if (size > 0) {

                    holder.itemView.frameSinLeer.visibility = View.VISIBLE
                    holder.itemView.NmensajesSinLeer.text = "$size"
                } else {
                    holder.itemView.frameSinLeer.visibility = View.GONE
                    Log.d("sin dato cero", "${0}")
                }
            }


    }

    var contador: Int = 0
    private fun getLastMessage(idChat: String, holder: ViewHolder) {
        mMessagesProvider = MessagesProvider()


        mMessagesProvider.getLastMessage(idChat).get().addOnSuccessListener {
            val size: Int = it.size()
            if (size > 0) {
                val lasMessage = it.documents[0].getString("message")
                if (lasMessage!!.length > 30) {
                    holder.itemView.ultimochat.text = lasMessage!!.substring(0, 50) + "..."
                } else {
                    holder.itemView.ultimochat.text = lasMessage.toString()
                }

            }
        }
    }


    private fun getUserChat(idUser: String, holder: ViewHolder) {
        mUserProvider = UserProviders()
        mUserProvider.getUsuario(idUser).addOnSuccessListener {
            if (it.exists()) {
                if (it.contains("nombre")) {
                    holder.itemView.nombreChat.text = "${it.get("nombre")}"
                }

                if (it.contains("photo")) {
                    var imagenPerfil: String = it.get("photo").toString()
                    if (it.get("photo").toString() == "") {
                        Picasso.get().load(R.drawable.ic_person_azul)
                            .error(R.drawable.ic_person_azul).into(holder.itemView.imagenChat)
                    } else {

                        Picasso.get().load(imagenPerfil).error(R.drawable.ic_person_azul)
                            .into(holder.itemView.imagenChat)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.cardview_chats, parent, false)
        return ViewHolder(view)
    }

    public class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        init {
            var nombreChat: TextView = view.findViewById(R.id.nombreChat)
            var ultimochat: TextView = view.findViewById(R.id.ultimochat)
            var imagenChat: ImageView = view.findViewById(R.id.imagenChat)

        }

    }


}