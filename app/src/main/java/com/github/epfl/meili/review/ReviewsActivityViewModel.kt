package com.github.epfl.meili.review

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.epfl.meili.models.Review

class ReviewsActivityViewModel(): ViewModel() {
    private lateinit var mReviews: MutableLiveData<List<Review>>
    private lateinit var reviewService: ReviewService
    private val mIsUpdating: MutableLiveData<Boolean> = MutableLiveData()

    fun init(poi_id: String) {
        // TODO instantiate review service
        // TODO initialize mReviews using service
    }

    fun getReviews(): LiveData<List<Review>> = mReviews
    fun getIsUpdating(): LiveData<Boolean> = mIsUpdating
}