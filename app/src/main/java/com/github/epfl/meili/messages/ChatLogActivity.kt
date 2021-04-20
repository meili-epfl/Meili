package com.github.epfl.meili.messages

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.github.epfl.meili.R
import com.github.epfl.meili.forum.ForumActivity
import com.github.epfl.meili.home.Auth
import com.github.epfl.meili.map.MapActivity
import com.github.epfl.meili.models.ChatMessage
import com.github.epfl.meili.models.User
import com.github.epfl.meili.review.ReviewsActivity
import com.github.epfl.meili.util.DateAuxiliary
import com.github.epfl.meili.poi.PointOfInterest
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item

class ChatLogActivity : AppCompatActivity() {

    companion object {
        private const val TAG: String = "ChatLogActivity"
    }

    private val adapter = GroupAdapter<GroupieViewHolder>()

    private var currentUser: User? = null
    private lateinit var groupId: String
    private var messsageSet = HashSet<ChatMessage>()

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
            val poi = intent.getParcelableExtra<PointOfInterest>(MapActivity.POI_KEY)
            supportActionBar?.title = poi?.name

            groupId = poi?.uid!!

            Log.d(TAG, "the poi is ${poi.name} and has id ${poi.uid}")
            ChatMessageViewModel.setMessageDatabase(FirebaseMessageDatabaseAdapter("POI/${poi.uid}"))

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
            val newMessages = list.minus(messsageSet)

            newMessages.forEach { message ->
                Log.d(TAG, "loading message: ${message.text}")

                adapter.add(ChatItem(message, message.fromId == currentUser!!.uid))
            }

            messsageSet.addAll(newMessages)

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //Inflate menu to enable adding chat and review buttons on the top
        menuInflater.inflate(R.menu.nav_chat_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // get the POI
        val poi = intent.getParcelableExtra<PointOfInterest>(MapActivity.POI_KEY)!!
        //Now that the buttons are added at the top control what each menu buttons does
        val intent: Intent = when (item?.itemId) {
            R.id.menu_reviews_from_chat -> {
                Intent(this, ReviewsActivity::class.java)
                    .putExtra(MapActivity.POI_KEY, poi)
            }
            R.id.menu_forum_from_chat-> {
                Intent(this, ForumActivity::class.java)
                    .putExtra(MapActivity.POI_KEY, poi)
            }
            else -> {
                Intent(this, ChatLogActivity::class.java)
            }
        }
        //clear the older intents so that the back button works correctly
        //intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        return super.onOptionsItemSelected(item)
    }
}

class ChatItem(private val message: ChatMessage, private val isChatMessageFromCurrentUser: Boolean) :
    Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        if (isChatMessageFromCurrentUser) {
            return R.layout.chat_from_me_row
        } else {
            return R.layout.chat_from_other_row
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