package com.github.epfl.meili.posts.forum

import com.github.epfl.meili.database.Database
import com.github.epfl.meili.models.PointOfInterest
import com.github.epfl.meili.posts.PostListViewModel

class ForumViewModel : PostListViewModel() {
    private lateinit var favoritePoisDatabase: Database<PointOfInterest>

    fun initFavoritePoisDatabase(database: Database<PointOfInterest>) {
        this.favoritePoisDatabase = database
    }

    fun addFavoritePoi(poi: PointOfInterest) {
        favoritePoisDatabase.addElement(poi.uid, poi)
    }
}