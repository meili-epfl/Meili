package com.github.epfl.meili.review

import androidx.lifecycle.MutableLiveData
import com.github.epfl.meili.models.Review

object FirestoreReviewSerivce: ReviewService {
    override fun getReviews(poi_id: String): MutableLiveData<List<Review>> {
        TODO("Not yet implemented")
    }
}