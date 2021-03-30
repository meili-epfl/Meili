package com.github.epfl.meili.review

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.epfl.meili.models.Review
import java.util.*

class ReviewsActivityViewModel: ViewModel(), Observer {

    companion object {
        private const val TAG: String = "ReviewViewModel"
    }

    private val mReviews: MutableLiveData<Map<String, Review>> = MutableLiveData()
    private val mAverageRating: MutableLiveData<Float> = MutableLiveData()

    private lateinit var service: ReviewService

    fun setReviewService(service: ReviewService) {
        this.service = service
        service.addObserver(this)
    }

    fun getReviews(): LiveData<Map<String, Review>> = mReviews
    fun getAverageRating(): LiveData<Float> = mAverageRating

    fun addReview(review: Review) = service.addReview(review)

    override fun update(o: Observable?, arg: Any?) {
        mReviews.value = service.reviews
        mAverageRating.value = service.averageRating
    }
}