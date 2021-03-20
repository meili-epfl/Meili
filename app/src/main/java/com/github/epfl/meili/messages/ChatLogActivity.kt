package com.github.epfl.meili

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.github.epfl.meili.messages.ChatMessageViewModel
import com.github.epfl.meili.messages.FirebaseMessageDatabaseAdapter
import com.github.epfl.meili.models.ChatMessage
import com.github.epfl.meili.models.User
import com.google.android.gms.maps.model.PointOfInterest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item

class ChatLogActivity : AppCompatActivity() {


    companion object {
        val TAG = "ChatLog"
    }

    private val adapter = GroupAdapter<GroupieViewHolder>()
    val auth = Firebase.auth

    val currentUser = User("MeiliIdentifier", "Meili") //TODO: set User services where we can getCurrentUser and retrieve info fromn otherUsers

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        findViewById<RecyclerView>(R.id.recycleview_chat_log).adapter = adapter


        val poi = intent.getParcelableExtra<PointOfInterest>("POI_KEY")
        supportActionBar?.title = poi?.name


        val groupId: String = poi?.placeId!!


        Log.d(TAG, "the poi is ${poi.name} and has id ${poi.placeId}")
        ChatMessageViewModel.setMessageDatabase(FirebaseMessageDatabaseAdapter("POI/${poi.placeId}"))

        //val myId: String = auth.uid!!

        listenForMessages(groupId)

        findViewById<Button>(R.id.button_chat_log).setOnClickListener {
            performSendMessage(groupId)
        }


    }

    private fun performSendMessage(groupId: String) {
        val text = findViewById<EditText>(R.id.edit_text_chat_log).text.toString()
        findViewById<EditText>(R.id.edit_text_chat_log).text.clear()

        ChatMessageViewModel.addMessage(text, "", groupId, System.currentTimeMillis() / 1000)
    }

    private fun listenForMessages(groupId: String) {

        val groupMessageObserver = Observer<List<ChatMessage>?> { list ->
            list.forEach { message ->
                Log.d(TAG, "loading message: ${message.text}")

                adapter.add(ChatItem(message.text, message.fromId == currentUser.uid))

                //scroll down
                val lastItemPos = adapter.itemCount -1
                findViewById<RecyclerView>(R.id.recycleview_chat_log).scrollToPosition(lastItemPos)
            }
        }

        ChatMessageViewModel.messages.observe(this, groupMessageObserver)
    }
}

class ChatItem(val text: String, val me: Boolean) : Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        if (me) {
            return R.layout.chat_from_me_row
        } else {
            return R.layout.chat_from_other_row
        }

    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.text_gchat_message).text = text
    }
}