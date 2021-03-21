package com.github.epfl.meili

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.github.epfl.meili.home.AuthenticationService
import com.github.epfl.meili.home.FirebaseAuthenticationService
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
import java.sql.Timestamp
import java.util.*

class ChatLogActivity : AppCompatActivity() {

    companion object {
        val TAG = "ChatLog"
    }

    private val adapter = GroupAdapter<GroupieViewHolder>()

    private lateinit var authService : AuthenticationService
    private lateinit var currentUser: User
    private lateinit var groupId: String
    //TODO: ensure you are signed in to be able to send message you can add screen that says not signed in

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        findViewById<RecyclerView>(R.id.recycleview_chat_log).adapter = adapter

        authService = FirebaseAuthenticationService()

        currentUser = authService.getCurrentuser()!!

        val poi = intent.getParcelableExtra<PointOfInterest>("POI_KEY")
        supportActionBar?.title = poi?.name


        groupId = poi?.placeId!!


        Log.d(TAG, "the poi is ${poi.name} and has id ${poi.placeId}")
        ChatMessageViewModel.setMessageDatabase(FirebaseMessageDatabaseAdapter("POI/${poi.placeId}"))

        listenForMessages()

        findViewById<Button>(R.id.button_chat_log).setOnClickListener {
            performSendMessage()
        }
    }

    private fun performSendMessage() {
        val text = findViewById<EditText>(R.id.edit_text_chat_log).text.toString()
        findViewById<EditText>(R.id.edit_text_chat_log).text.clear()

        ChatMessageViewModel.addMessage(text, currentUser.uid, groupId, System.currentTimeMillis() / 1000, currentUser.username)
    }

    private fun listenForMessages() {

        val groupMessageObserver = Observer<List<ChatMessage>?> { list ->
            adapter.clear() //TODO: verify if there is a better way to do this

            list.forEach { message ->

                Log.d(TAG, "loading message: ${message.text}")

                adapter.add(ChatItem(message, message.fromId == currentUser.uid))
            }

            //scroll down
            val lastItemPos = adapter.itemCount -1
            findViewById<RecyclerView>(R.id.recycleview_chat_log).scrollToPosition(lastItemPos)
        }

        ChatMessageViewModel.messages.observe(this, groupMessageObserver)
    }
}

class ChatItem(val message: ChatMessage, val me: Boolean) : Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        if (me) {
            return R.layout.chat_from_me_row
        } else {
            return R.layout.chat_from_other_row
        }

    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.text_gchat_message).text = message.text
        viewHolder.itemView.findViewById<TextView>(R.id.text_chat_timestamp).text = "11:35" //todo get day from timestamp
        viewHolder.itemView.findViewById<TextView>(R.id.text_chat_date).text = "21st March"

        if(!me){
            viewHolder.itemView.findViewById<TextView>(R.id.text_chat_user_other).text = message.fromName
        }

    }
}