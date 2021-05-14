package com.github.epfl.meili.forum

import com.github.epfl.meili.database.AtomicPostFirestoreDatabase
import com.github.epfl.meili.database.Database
import com.github.epfl.meili.database.FirestoreDatabase
import com.github.epfl.meili.models.FavoritePointOfInterest
import com.github.epfl.meili.models.PointOfInterest
import com.github.epfl.meili.models.Post
import com.github.epfl.meili.util.MeiliViewModel

class ForumViewModel : MeiliViewModel<Post>() {
    private lateinit var favoritePoisDatabase: Database<FavoritePointOfInterest>

    fun initFavoritePoisDatabase(database: Database<FavoritePointOfInterest>) {
        this.favoritePoisDatabase = database
        database.addObserver(this)
    }

    fun addFavoritePoi(poi: PointOfInterest) {
        favoritePoisDatabase.addElement(poi.uid, FavoritePointOfInterest(poi))
    }

    fun upvote(key: String, uid: String) =
        (database as AtomicPostFirestoreDatabase).upDownVote(key, uid, true)

    fun downvote(key: String, uid: String) =
        (database as AtomicPostFirestoreDatabase).upDownVote(key, uid, false)
}