package com.jomeva.driving.activities.activities

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jomeva.driving.R
import com.jomeva.driving.activities.adapters.ChatAdapter
import com.jomeva.driving.activities.modelos.Chats
import com.jomeva.driving.activities.providers.ChatsProvider
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query

class FragmentThree : Fragment() {
    lateinit var mReciclerView: RecyclerView
    lateinit var mlinearLayoutManager: LinearLayoutManager
private lateinit var  mAdapter: ChatAdapter
    private lateinit var  mChatProvider: ChatsProvider
    private lateinit var  mAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mAuth= FirebaseAuth.getInstance()
        val mView=inflater.inflate(R.layout.fragment_three, container, false)
        mReciclerView= mView.findViewById(R.id.reciclerViewChat)
        mlinearLayoutManager=LinearLayoutManager(context)
        mReciclerView.layoutManager = this.mlinearLayoutManager

        val adRequest: AdRequest = AdRequest.Builder().build()
        val bannerventanaConsulta =
            mView!!.findViewById<AdView>(R.id.activityperfilpostAds)
        bannerventanaConsulta!!.loadAd(adRequest)

        return mView
    }

    override fun onStart() {
        super.onStart()
        val users=mAuth.currentUser
        if (users != null) {
            mChatProvider = ChatsProvider()

            var query: Query = mChatProvider.getAll(mAuth.currentUser!!.uid.toString())

            var options: FirestoreRecyclerOptions<Chats?> =
                FirestoreRecyclerOptions.Builder<Chats>().setQuery(
                    query,
                    Chats::class.java
                ).build()

            mAdapter = ChatAdapter(options, context)
            mReciclerView.adapter = this.mAdapter
            mAdapter.startListening()
        }
        /**/
    }
    override fun onStop() {
        super.onStop()
        val users=mAuth.currentUser
        if (users != null) {
            mAdapter.stopListening()
        }

    }
}