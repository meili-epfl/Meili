package com.github.epfl.meili.posts.feed

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.github.epfl.meili.R
import com.github.epfl.meili.util.LocationService.isLocationPermissionGranted
import com.github.epfl.meili.util.LocationService.listenToLocationChanges
import com.github.epfl.meili.util.NavigableActivity

class FeedActivity : NavigableActivity(R.layout.activity_feed, R.id.feed) {
    private lateinit var viewModel: FeedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initViewModel()

        if (isLocationPermissionGranted(this)) {
            listenToLocationChanges(applicationContext, viewModel)
        }
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this).get(FeedViewModel::class.java)
        viewModel.getElements().observe(this) {
//            it.to
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (isLocationPermissionGranted(this)) {
            listenToLocationChanges(applicationContext, viewModel)
        }
    }
}