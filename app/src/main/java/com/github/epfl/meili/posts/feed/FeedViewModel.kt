package com.github.epfl.meili.posts.feed

import com.github.epfl.meili.database.AtomicPostFirestoreDatabase
import com.github.epfl.meili.database.FirestoreDatabase.Companion.MAX_EQUALITY_CLAUSES
import com.github.epfl.meili.models.PointOfInterest
import com.github.epfl.meili.models.Post
import com.github.epfl.meili.poi.PoiService
import com.github.epfl.meili.posts.PostListViewModel
import com.github.epfl.meili.util.PoiServiceViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil.computeDistanceBetween

class FeedViewModel: PostListViewModel(), PoiServiceViewModel {
    override var poiService: PoiService? = null
    override var nbCurrentRequests: Int = 0
    override var lastUserLocation: LatLng? = null

    private var databaseInitialized = false

    override fun onSuccessPoiReceived(poiList: List<PointOfInterest>) {
        super.onSuccessPoiReceived(poiList)

        val nearestPoiKeys = poiList.sortedBy {
            computeDistanceBetween(it.getLatLng(), lastUserLocation!!)
        }.take(MAX_EQUALITY_CLAUSES).map { it.uid }

        if (nearestPoiKeys.isNotEmpty()) {
            if (databaseInitialized) {
                super.database.onDestroy()
            }

            super.initDatabase(AtomicPostFirestoreDatabase("forum") {
                it.whereIn(Post.POI_KEY_FIELD, nearestPoiKeys)
            })
            databaseInitialized = true
        }
    }
}