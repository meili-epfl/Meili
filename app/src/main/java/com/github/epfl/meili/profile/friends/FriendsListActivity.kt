package com.github.epfl.meili.profile.friends

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.epfl.meili.BuildConfig
import com.github.epfl.meili.R
import com.github.epfl.meili.database.FirestoreDatabase
import com.github.epfl.meili.home.Auth
import com.github.epfl.meili.messages.ChatLogActivity
import com.github.epfl.meili.models.Friend
import com.github.epfl.meili.models.User
import com.github.epfl.meili.profile.ProfileActivity
import com.github.epfl.meili.util.ClickListener
import com.github.epfl.meili.util.MeiliViewModel
import com.github.epfl.meili.util.TopSpacingItemDecoration

class FriendsListActivity : AppCompatActivity(), ClickListener {
    companion object {
        private const val TAG: String = "FriendListActivity"
        private const val TITLE: String = "My Friends"
        private const val DEFAULT_MEILI_FRIEND_UID = "OP7VVymi3ZOfTr0akvMnh5HEa2a2"
        const val FRIEND_KEY = "FRIEND_KEY"

        var serviceProvider: () -> UserInfoService = { UserInfoService() }
        var getFriendsDatabasePath: (String) -> String = { uid -> "friends/$uid/friends" }
    }

    private lateinit var recyclerAdapter: FriendsListRecyclerAdapter
    private lateinit var viewModel: MeiliViewModel<Friend>

    private var usersMap: HashMap<String, User> = HashMap()
    private lateinit var addFriendsButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends_list)


        initViewModel()
        initRecyclerView()
        initViews()

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

    private fun initRecyclerView() {
        recyclerAdapter = FriendsListRecyclerAdapter(this)
        val recyclerView: RecyclerView = findViewById(R.id.friends_list_recycler_view)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@FriendsListActivity)
            addItemDecoration(TopSpacingItemDecoration())
            adapter = recyclerAdapter
        }
    }

    private fun onFriendsUpdateReceived(map: Map<String, Friend>) {
        addDefaultFriend(map)

        val newFriendsList = map.keys.toList().minus(usersMap.keys.toList())

        serviceProvider().getUserInformation(newFriendsList, { onFriendsInfoReceived(it) },
                { Log.d(TAG, "Error when fetching friends information") })
    }

    private fun onFriendsInfoReceived(users: Map<String, User>) {
        usersMap = HashMap(users)
        recyclerAdapter.submitList(users.toList())
        recyclerAdapter.notifyDataSetChanged()
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

    private fun openFriendProfile(friendUid: String) {
        val intent =
            Intent(this, ProfileActivity::class.java).putExtra(ProfileActivity.USER_KEY, friendUid)
        startActivity(intent)
    }

    override fun onClicked(buttonId: Int, info: String) {
        when (buttonId) {
            R.id.friend_chat_button -> openFriendChat(info)
            R.id.friend_card -> openFriendProfile(info)
        }
    }
}