package com.github.epfl.meili

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.github.epfl.meili.messages.ChatMessageViewModel
import com.github.epfl.meili.messages.FirebaseMessageDatabaseAdapter
import com.github.epfl.meili.models.ChatMessage
import com.google.android.gms.maps.model.PointOfInterest
import com.google.firebase.auth.FirebaseAuth
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


        val poi = intent.getParcelableExtra<PointOfInterest>("POI_KEY")
        supportActionBar?.title = poi?.name


        val groupId : String = poi?.placeId!!
        val myId: String = FirebaseAuth.getInstance().uid!!
        ChatMessageViewModel.database = FirebaseMessageDatabaseAdapter("POI/${poi.placeId}")

        listenForMessages(groupId, myId)

        findViewById<Button>(R.id.button_chat_log).setOnClickListener {
            performSendMessage(groupId, myId)
        }


    }

    private fun performSendMessage(groupId: String, myId: String) {
        val text = findViewById<EditText>(R.id.edit_text_chat_log).text.toString()
        findViewById<EditText>(R.id.edit_text_chat_log).text.clear()


        ChatMessageViewModel.addMessage(text, myId, groupId, System.currentTimeMillis() / 1000)
    }

    private fun listenForMessages(groupId: String, myId: String) {

        val groupMessageObserver = Observer<List<ChatMessage>?> { list ->
            list.forEach { message ->
                Log.d(TAG, "loading message: ${message.text}")
                if (message.fromId == myId) {
                    adapter.add(ChatItem(message.text, true))
                } else {
                    adapter.add(ChatItem(message.text, false))
                }
            }
        }

        ChatMessageViewModel.messages.observe(this, groupMessageObserver)
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