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
import com.github.epfl.meili.auth.Auth
import com.github.epfl.meili.map.MapActivity
import com.github.epfl.meili.models.ChatMessage
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
    private lateinit var chatId: String
    private var messageSet = HashSet<ChatMessage>()

    private var isGroupChat = false
    private var poi: PointOfInterest? = null

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

    /**
     * Depending on `isLoggedIn`, starts a group/private chat or not
     */
    fun verifyAndUpdateUserIsLoggedIn(isLoggedIn: Boolean) {
        if (isLoggedIn) {
            currentUser = Auth.getCurrentUser()

            val poi = intent.getParcelableExtra<PointOfInterest>(MapActivity.POI_KEY)
            val friend = intent.getParcelableExtra<User>(FRIEND_KEY)
            val databasePath: String

            if (poi != null) {
                this.poi = poi

                supportActionBar?.title = poi.name
                chatId = poi.uid

                setGroupChat(true)

                Log.d(TAG, "Starting chat group at ${poi.name} and has id ${poi.uid}")

                databasePath = "POI/${chatId}"
            } else {
                supportActionBar?.title = friend?.username
                val friendUid: String = friend?.uid!!
                val currentUid: String = currentUser!!.uid

                // The friend chat document in the database is saved under the key with value
                // of the two user ids concatenated in sorted order
                chatId =
                    if (friendUid < currentUid) "$friendUid;$currentUid" else "$currentUid;$friendUid"

                setGroupChat(false)

                Log.d(TAG, "Starting friend chat with ${friend.uid}")

                databasePath = "FriendChat/${chatId}"
            }

            ChatMessageViewModel.setMessageDatabase(FirebaseMessageDatabaseAdapter(databasePath))
            listenForMessages(chatId)

            findViewById<Button>(R.id.button_chat_log).setOnClickListener {
                performSendMessage()
            }

        } else {
            currentUser = null
            supportActionBar?.title = getString(R.string.not_signed_in)
            Auth.signInIntent(this)
        }
    }

    private fun setGroupChat(isGroupChat: Boolean) {
        this.isGroupChat = isGroupChat
        if (!isGroupChat) {
            hideMenuButtons()
        }
    }

    private fun hideMenuButtons() {
        setShowMenu(false)
        invalidateOptionsMenu()
    }


    private fun performSendMessage() {
        val text = findViewById<EditText>(R.id.edit_text_chat_log).text.toString()
        findViewById<EditText>(R.id.edit_text_chat_log).text.clear()

        ChatMessageViewModel.addMessage(
            text,
            currentUser!!.uid,
            chatId,
            System.currentTimeMillis() / 1000,
            currentUser!!.username
        )
    }

    private fun listenForMessages(chatID: String) {

        val groupMessageObserver = Observer<List<ChatMessage>?> { list ->
            val newMessages = list.minus(messageSet)
            newMessages.filter { message -> message.toId == chatID }.forEach { message ->
                Log.d(TAG, "loading message: ${message.text}")
                adapter.add(ChatItem(message, message.fromId == currentUser!!.uid, isGroupChat))
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

        Auth.onActivityResult(this, requestCode, resultCode, data) {}
    }
}

class ChatItem(
    private val message: ChatMessage,
    private val isChatMessageFromCurrentUser: Boolean,
    private val isGroupChat: Boolean
) :
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
                if (isGroupChat) message.fromName else ""
        }
    }
}