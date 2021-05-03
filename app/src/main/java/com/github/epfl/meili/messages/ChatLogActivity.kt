package com.github.epfl.meili.messages

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.github.epfl.meili.R
import com.github.epfl.meili.home.Auth
import com.github.epfl.meili.home.RequiresLoginActivity
import com.github.epfl.meili.map.MapActivity
import com.github.epfl.meili.models.ChatMessage
import com.github.epfl.meili.models.Friend
import com.github.epfl.meili.models.PointOfInterest
import com.github.epfl.meili.models.User
import com.github.epfl.meili.profile.friends.FriendsListActivity.Companion.FRIEND_KEY
import com.github.epfl.meili.util.DateAuxiliary
import com.github.epfl.meili.util.MenuActivity
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item

class ChatLogActivity : MenuActivity(R.menu.nav_chat_menu) {

    companion object {
        private const val TAG: String = "ChatLogActivity"
    }

    private val adapter = GroupAdapter<GroupieViewHolder>()

    private var currentUser: User? = null
    private lateinit var groupId: String
    private var messageSet = HashSet<ChatMessage>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        findViewById<RecyclerView>(R.id.recycleview_chat_log).adapter = adapter

        Auth.isLoggedIn.observe(this) {
            Log.d(TAG, "value received $it")
            verifyAndUpdateUserIsLoggedIn(it)
        }

        verifyAndUpdateUserIsLoggedIn(Auth.isLoggedIn.value!!)
    }

    fun verifyAndUpdateUserIsLoggedIn(isLoggedIn: Boolean) {
        if (isLoggedIn) {
            currentUser = Auth.getCurrentUser()

            //TODO: differentiate between group chat, value received will be POI
            // And Friend, for which it will be and individual chat
            // I would keep UI the same, and for the moment just change support action bar
            // And FirebaseMessageDatabaseAdapter path
            // TODO: If I have time, don't write name of other before each message if you are in friend chat


            val poi = intent.getParcelableExtra<PointOfInterest>(MapActivity.POI_KEY)
            val friend = intent.getParcelableExtra<Friend>(FRIEND_KEY)
            val databasePath: String

            if(poi != null){

                supportActionBar?.title = poi.name
                //TODO: change name to something more general (also for friend chat) as toUid
                groupId = poi.uid

                Log.d(TAG, "Starting chat group at ${poi.name} and has id ${poi.uid}")

                databasePath = "POI/${poi.uid}"
            }else{
                supportActionBar?.title = friend?.friendUid
                //TODO: change name to something more general (also for friend chat) as toUid
                groupId = friend?.friendUid!!

                Log.d(TAG, "Starting friend chat with ${friend?.friendUid}")

                databasePath = "POI/${friend?.friendUid}" //TODO: set some database structure where you can find the messages (maybe sort uids and concatenate)
            }

            ChatMessageViewModel.setMessageDatabase(FirebaseMessageDatabaseAdapter(databasePath))

            listenForMessages()

            findViewById<Button>(R.id.button_chat_log).setOnClickListener {
                performSendMessage()
            }

        } else {
            currentUser = null
            supportActionBar?.title = "Not Signed In"
            Auth.signIn(this)
        }
    }


    private fun performSendMessage() {
        val text = findViewById<EditText>(R.id.edit_text_chat_log).text.toString()
        findViewById<EditText>(R.id.edit_text_chat_log).text.clear()

        ChatMessageViewModel.addMessage(
            text,
            currentUser!!.uid,
            groupId,
            System.currentTimeMillis() / 1000,
            currentUser!!.username
        )
    }

    private fun listenForMessages() {

        val groupMessageObserver = Observer<List<ChatMessage>?> { list ->
            val newMessages = list.minus(messageSet)

            newMessages.forEach { message ->
                Log.d(TAG, "loading message: ${message.text}")

                adapter.add(ChatItem(message, message.fromId == currentUser!!.uid))
            }

            messageSet.addAll(newMessages)

            //scroll down
            val lastItemPos = adapter.itemCount - 1
            findViewById<RecyclerView>(R.id.recycleview_chat_log).scrollToPosition(lastItemPos)
        }

        ChatMessageViewModel.messages.observe(this, groupMessageObserver)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Auth.onActivityResult(this, requestCode, resultCode, data)
    }

}

class ChatItem(private val message: ChatMessage, private val isChatMessageFromCurrentUser: Boolean) :
    Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return if (isChatMessageFromCurrentUser) {
            R.layout.chat_from_me_row
        } else {
            R.layout.chat_from_other_row
        }
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.text_gchat_message).text = message.text
        val date = DateAuxiliary.getDateFromTimestamp(message.timestamp)
        viewHolder.itemView.findViewById<TextView>(R.id.text_chat_timestamp).text =
            DateAuxiliary.getTime(date)
        viewHolder.itemView.findViewById<TextView>(R.id.text_chat_date).text =
            DateAuxiliary.getDay(date)

        if (!isChatMessageFromCurrentUser) {
            viewHolder.itemView.findViewById<TextView>(R.id.text_chat_user_other).text =
                message.fromName
        }
    }
}