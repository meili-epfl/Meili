package com.github.epfl.meili.profile.friends

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
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
import com.github.epfl.meili.nearby.NearbyActivity
import com.github.epfl.meili.util.MeiliViewModel
import com.github.epfl.meili.util.TopSpacingItemDecoration

class FriendsListActivity : AppCompatActivity() {
    companion object {
        private const val FRIENDS_PADDING: Int = 15
        private const val TAG: String = "FriendListActivity"
        private const val TITLE: String = "My Friends"
        const val FRIEND_KEY = "FRIEND_KEY"
        private const val DEFAULT_MEILI_FRIEND_UID = "OP7VVymi3ZOfTr0akvMnh5HEa2a2"
        private const val DEFAULT_MEILI_FRIEND_USERNAME = "Meili"
    }

    private lateinit var recyclerAdapter: FriendsListRecyclerAdapter
    private lateinit var viewModel: MeiliViewModel<Friend>

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
        viewModel = ViewModelProvider(this).get(MeiliViewModel::class.java) as MeiliViewModel<Friend>

        viewModel.setDatabase(FirestoreDatabase("friends/" + Auth.getCurrentUser()!!.uid + "/friends", Friend::class.java))
        viewModel.getElements().observe(this) { map ->
            addDefaultFriend(map)
            recyclerAdapter.submitList(map.toList())
            recyclerAdapter.notifyDataSetChanged()
        }
    }

    private fun initViews() {
        addFriendsButton = findViewById(R.id.add_friend_button)
    }

    private fun initRecyclerView() {
        recyclerAdapter = FriendsListRecyclerAdapter()
        val recyclerView: RecyclerView = findViewById(R.id.friends_list_recycler_view)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@FriendsListActivity)
            addItemDecoration(TopSpacingItemDecoration(FRIENDS_PADDING))
            adapter = recyclerAdapter
        }
    }

    fun onFriendsListButtonClicked(view: View) {
        when (view) {
            addFriendsButton -> showAddFriends()
            else -> openFriendChat((view as TextView).text as String)

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
        val intent = Intent(this, ChatLogActivity::class.java).putExtra(FRIEND_KEY, Friend(friendUid))
        startActivity(intent)
    }
}