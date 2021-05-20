package com.github.epfl.meili.posts.feed

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.github.epfl.meili.R
import com.github.epfl.meili.poi.PoiServiceCached
import com.github.epfl.meili.posts.PostListActivity
import com.github.epfl.meili.posts.PostListRecyclerAdapter
import com.github.epfl.meili.posts.PostListViewModel
import com.github.epfl.meili.util.LocationService.isLocationPermissionGranted
import com.github.epfl.meili.util.LocationService.listenToLocationChanges
import com.github.epfl.meili.util.NavigableActivity

class FeedActivity : NavigableActivity(R.layout.activity_feed, R.id.feed), PostListActivity {

    override lateinit var recyclerAdapter: PostListRecyclerAdapter
    override lateinit var viewModel: PostListViewModel

    override fun getActivity(): AppCompatActivity = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initActivity(
                FeedViewModel::class.java,
                findViewById(R.id.feed_recycler_view),
                findViewById(R.id.sort_spinner)
        )

        listenToNearbyPosts()
    }

    fun onClick(view: View) {
        startActivity(getPostActivityIntent(view.findViewById(R.id.post_id)))
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
            listenToLocationChanges(applicationContext, viewModel as FeedViewModel)
            (viewModel as FeedViewModel).initPoiService(PoiServiceCached())
        }
    }
}