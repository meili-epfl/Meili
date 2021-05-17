package com.github.epfl.meili.posts.feed

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.epfl.meili.R
import com.github.epfl.meili.home.Auth
import com.github.epfl.meili.map.MapActivity
import com.github.epfl.meili.models.Post
import com.github.epfl.meili.poi.PoiServiceCached
import com.github.epfl.meili.posts.PostActivity
import com.github.epfl.meili.posts.PostListRecyclerAdapter
import com.github.epfl.meili.util.LocationService.isLocationPermissionGranted
import com.github.epfl.meili.util.LocationService.listenToLocationChanges
import com.github.epfl.meili.util.NavigableActivity
import com.github.epfl.meili.util.TopSpacingItemDecoration

class FeedActivity : NavigableActivity(R.layout.activity_feed, R.id.feed) {

    private lateinit var recyclerAdapter: PostListRecyclerAdapter
    private lateinit var viewModel: FeedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initViewModel()
        initRecyclerView()
        initLoggedInListener()

        listenToNearbyPosts()
    }

    fun onClick(view: View) {
        openPost(view.findViewById(R.id.post_id))
    }

    private fun openPost(view: View) {
        val postId: String = (view as TextView).text.toString()
        val intent: Intent = Intent(this, PostActivity::class.java)
            .putExtra(Post.TAG, viewModel.getElements().value?.get(postId))
            .putExtra(PostActivity.POST_ID, postId)
        startActivity(intent)
    }

    private fun initRecyclerView() {
        recyclerAdapter = PostListRecyclerAdapter(viewModel)
        val recyclerView: RecyclerView = findViewById(R.id.feed_recycler_view)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@FeedActivity)
            addItemDecoration(TopSpacingItemDecoration())
            adapter = recyclerAdapter
        }
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this).get(FeedViewModel::class.java)
        viewModel.getElements().observe(this) {
            recyclerAdapter.submitList(it.toList())
            recyclerAdapter.notifyDataSetChanged()
        }
    }

    private fun initLoggedInListener() {
        Auth.isLoggedIn.observe(this, { loggedIn ->
            if (loggedIn && Auth.getCurrentUser() != null) {
                recyclerAdapter.submitUserInfo(Auth.getCurrentUser()!!.uid)
                recyclerAdapter.notifyDataSetChanged()
            }
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        listenToNearbyPosts()
    }

    private fun listenToNearbyPosts() {
        if (isLocationPermissionGranted(this)) {
            listenToLocationChanges(applicationContext, viewModel)
            viewModel.initPoiService(PoiServiceCached())
        }
    }
}