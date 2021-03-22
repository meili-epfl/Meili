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
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import java.util.*
import kotlin.collections.HashSet

class ChatLogActivity : AppCompatActivity() {

    companion object {
        val TAG = "ChatLog"
    }

    private val adapter = GroupAdapter<GroupieViewHolder>()

    private lateinit var authService: AuthenticationService
    private lateinit var currentUser: User
    private lateinit var groupId: String
    private var messsageSet = HashSet<ChatMessage>()
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
            var newMessages = list.minus(messsageSet)

            newMessages.forEach { message ->
                Log.d(TAG, "loading message: ${message.text}")

                adapter.add(ChatItem(message, message.fromId == currentUser.uid))
            }

            messsageSet.addAll(newMessages)

            //scroll down
            val lastItemPos = adapter.itemCount - 1
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
        var date = Date(message.timestamp * 1000)
        viewHolder.itemView.findViewById<TextView>(R.id.text_chat_timestamp).text = getTime(date)
        viewHolder.itemView.findViewById<TextView>(R.id.text_chat_date).text = getDay(date)

        if (!me) {
            viewHolder.itemView.findViewById<TextView>(R.id.text_chat_user_other).text = message.fromName
        }
    }

    private fun getDay(date: Date): String {
        var res = date.toString()
        var splitted_res = res.split(" ")
        return splitted_res[MONTH] + " " + splitted_res[DAY_OF_WEEK] + " " + splitted_res[DAY_OF_MONTH]
    }

    private fun getTime(date: Date): String {
        var res = date.toString()
        var splitted_res = res.split(" ")

        // Return only hours:minutes without seconds (originally hh:mm:ss)
        return splitted_res[TIME_OF_DAY].substring(0, splitted_res[TIME_OF_DAY].length - 3)
    }

    companion object {
        private const val MONTH = 0
        private const val DAY_OF_WEEK = 1
        private const val DAY_OF_MONTH = 2
        private const val TIME_OF_DAY = 3
    }
}