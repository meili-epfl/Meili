package com.github.epfl.meili.review

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.epfl.meili.models.Review
import java.util.*

class ReviewsActivityViewModel(private val reviewService: ReviewService): ViewModel(), Observer {
    companion object {
        private const val TAG: String = "ReviewViewModel"
    }

    private val mReviews: MutableLiveData<List<Review>> = MutableLiveData()

    init {
        mReviews.value = reviewService.reviews
        reviewService.addObserver(this)
    }

    fun getReviews(): LiveData<List<Review>> = mReviews

    fun addReview(review: Review) = reviewService.addReview(review)

    override fun update(o: Observable?, arg: Any?) {
        mReviews.value = reviewService.reviews
        Log.e(TAG, "${mReviews.value}")
    }
}