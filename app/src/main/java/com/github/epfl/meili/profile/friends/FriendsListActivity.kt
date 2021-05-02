package com.github.epfl.meili.profile.friends

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.epfl.meili.BuildConfig
import com.github.epfl.meili.R
import com.github.epfl.meili.database.FirestoreDatabase
import com.github.epfl.meili.home.Auth
import com.github.epfl.meili.models.Friend
import com.github.epfl.meili.nearby.NearbyActivity
import com.github.epfl.meili.util.MeiliViewModel
import com.github.epfl.meili.util.TopSpacingItemDecoration

class FriendsListActivity : AppCompatActivity() {
    companion object {
        private const val FRIENDS_PADDING: Int = 15
    }

    private lateinit var recyclerAdapter: FriendsListRecyclerAdapter
    private lateinit var viewModel: MeiliViewModel<Friend>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends_list)


        initViewModel()
        initRecyclerView()
    }

    private fun initViewModel() {
        if (Auth.getCurrentUser() == null) {
            if (BuildConfig.DEBUG) {
                error("User not logged in trying to access friends list activity")
            }
        } else {
            Log.d("FriendListActivity", Auth.getCurrentUser()!!.uid)
            @Suppress("UNCHECKED_CAST")
            viewModel = ViewModelProvider(this).get(MeiliViewModel::class.java) as MeiliViewModel<Friend>

            viewModel.setDatabase(FirestoreDatabase("friends/" + Auth.getCurrentUser()!!.uid + "/friends", Friend::class.java))
            viewModel.getElements().observe(this) { map ->
                recyclerAdapter.submitList(map.toList())
                recyclerAdapter.notifyDataSetChanged()
            }
        }
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

    fun onAddFriendButtonClicked(view: View) {
        val intent = Intent(this, NearbyActivity::class.java)
        startActivity(intent)
    }
}