package com.github.epfl.meili.posts.feed

import com.github.epfl.meili.database.FirestoreDatabase
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

    override fun onSuccessPoiReceived(poiList: List<PointOfInterest>) {
        super.onSuccessPoiReceived(poiList)
        super.database.onDestroy()
        super.initDatabase(FirestoreDatabase("forum", Post::class.java) { collectionReference ->
            val nearestPoiKeys = poiList.sortedBy {
                computeDistanceBetween(it.getLatLng(), lastUserLocation!!)
            }.take(MAX_EQUALITY_CLAUSES).map { it.uid }

            collectionReference.whereIn(Post.POI_KEY_FIELD, nearestPoiKeys)
        })
    }
}