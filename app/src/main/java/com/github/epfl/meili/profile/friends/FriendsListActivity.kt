package com.github.epfl.meili.profile.friends

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.github.epfl.meili.BuildConfig
import com.github.epfl.meili.R
import com.github.epfl.meili.auth.Auth
import com.github.epfl.meili.database.FirestoreDatabase
import com.github.epfl.meili.messages.ChatLogActivity
import com.github.epfl.meili.models.Friend
import com.github.epfl.meili.models.User
import com.github.epfl.meili.profile.UserProfileLinker
import com.github.epfl.meili.util.ClickListener
import com.github.epfl.meili.util.MeiliRecyclerAdapter
import com.github.epfl.meili.util.MeiliViewModel
import com.github.epfl.meili.util.RecyclerViewInitializer.initRecyclerView

class FriendsListActivity : AppCompatActivity(), ClickListener, UserProfileLinker<Friend> {
    companion object {
        private const val TAG: String = "FriendListActivity"
        private const val TITLE: String = "My Friends"
        private const val DEFAULT_MEILI_FRIEND_UID = "OP7VVymi3ZOfTr0akvMnh5HEa2a2"
        const val FRIEND_KEY = "FRIEND_KEY"

        var serviceProvider: () -> UserInfoService = { UserInfoService() }
        var getFriendsDatabasePath: (String) -> String = { uid -> "friends/$uid/friends" }
    }

    override lateinit var recyclerAdapter: MeiliRecyclerAdapter<Pair<Friend, User>>
    private lateinit var viewModel: MeiliViewModel<Friend>

    override var usersMap: Map<String, User> = HashMap()
    private lateinit var addFriendsButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends_list)

        initViews()

        initViewModel()
        recyclerAdapter = FriendsListRecyclerAdapter(this)

        initRecyclerView(
            recyclerAdapter,
            findViewById(R.id.friends_list_recycler_view),
            this
        )

        supportActionBar?.title = TITLE
    }

    private fun initViewModel() {
        if (BuildConfig.DEBUG && Auth.getCurrentUser() == null) {
            error("$TAG: User trying to access friends list activity without logging in")
        }

        @Suppress("UNCHECKED_CAST")
        viewModel =

            ViewModelProvider(this).get(MeiliViewModel::class.java) as MeiliViewModel<Friend>

        viewModel.initDatabase(
            FirestoreDatabase(
                getFriendsDatabasePath(Auth.getCurrentUser()!!.uid),
                Friend::class.java
            )
        )
        viewModel.getElements().observe(this) { map ->
            onFriendsUpdateReceived(map)
        }
    }

    private fun initViews() {
        addFriendsButton = findViewById(R.id.add_friend_button)
    }

    private fun onFriendsUpdateReceived(map: Map<String, Friend>) {
        addDefaultFriend(map)

        val newFriendsList = map.keys.toList().minus(usersMap.keys.toList())

        serviceProvider().getUserInformation(newFriendsList) { onUsersInfoReceived(it, map) }
    }

    fun onFriendsListButtonClicked(view: View) {
        when (view) {
            addFriendsButton -> showAddFriends()
        }
    }

    private fun addDefaultFriend(friendsMap: Map<String, Friend>) {
        if (friendsMap.isEmpty()) {
            viewModel.addElement(DEFAULT_MEILI_FRIEND_UID, Friend(DEFAULT_MEILI_FRIEND_UID))
        }
    }

    private fun showAddFriends() {
        val intent = Intent(this, NearbyActivity::class.java)
        startActivity(intent)
    }

    private fun openFriendChat(friendUid: String) {
        val intent =
            Intent(this, ChatLogActivity::class.java).putExtra(FRIEND_KEY, usersMap[friendUid])
        startActivity(intent)
    }

    override fun onClicked(buttonId: Int, info: String) {
        when (buttonId) {
            R.id.friend_chat_button -> openFriendChat(info)
            R.id.userName -> openUserProfile(info)
        }
    }
}