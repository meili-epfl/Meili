package com.github.epfl.meili

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.github.epfl.meili.messages.ChatMessageViewModel
import com.github.epfl.meili.models.ChatMessage
import com.github.epfl.meili.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item

class ChatLogActivity : AppCompatActivity() {


    companion object {
        val TAG = "ChatLog"
    }

    val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        findViewById<RecyclerView>(R.id.recycleview_chat_log).adapter = adapter

        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        supportActionBar?.title = user?.username



        listenForMessages()

        findViewById<Button>(R.id.button_chat_log).setOnClickListener {
            performSendMessage()
        }


    }

    private fun performSendMessage() {
        val text = findViewById<EditText>(R.id.edit_text_chat_log).text.toString()

        findViewById<EditText>(R.id.edit_text_chat_log).text.clear()

        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)

        val otherId : String = user?.uid!!
        val myId: String = FirebaseAuth.getInstance().uid!!

        val viewModel = ChatMessageViewModel(myId, otherId)

        viewModel.addMessage(text, myId, otherId, System.currentTimeMillis() / 1000)
    }

    private fun listenForMessages() {
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)

        val otherId : String = user?.uid!!
        val myId: String = FirebaseAuth.getInstance().uid!!
        val sentMessagesViewModel = ChatMessageViewModel(myId, otherId)
        val receivedMessageViewModel = ChatMessageViewModel(otherId, myId)
        val sentObserver = Observer<List<ChatMessage>?> { list ->
            list.forEach{ message ->
                Log.d(TAG, "loading message: ${message.text}")
                adapter.add(ChatItem(message.text, true))
            }
        }
        val receivedObserver = Observer<List<ChatMessage>?> { list ->
            list.forEach{ message ->
                Log.d(TAG, "loading message: ${message.text}")
                adapter.add(ChatItem(message.text, false))
            }
        }
        sentMessagesViewModel.messages.observe(this, sentObserver)
        receivedMessageViewModel.messages.observe(this, receivedObserver)
    }
}

class ChatItem(val text: String, val from: Boolean) : Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        if (from) {
            return R.layout.chat_from_row
        } else {
            return R.layout.chat_to_row
        }

    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.chat_textview).text = text
    }

}