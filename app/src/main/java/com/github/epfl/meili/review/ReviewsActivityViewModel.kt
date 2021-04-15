package com.github.epfl.meili.review

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.epfl.meili.database.Database
import com.github.epfl.meili.models.Review
import java.util.*

class ReviewsActivityViewModel: ViewModel(), Observer {

    companion object {
        private const val TAG: String = "ReviewViewModel"
    }

    private val mReviews: MutableLiveData<Map<String, Review>> = MutableLiveData()
    private val mAverageRating: MutableLiveData<Float> = MutableLiveData()

    private lateinit var database: Database<Review>

    fun setReviewService(database: Database<Review>) {
        this.database = database
        database.addObserver(this)
    }

    fun getReviews(): LiveData<Map<String, Review>> = mReviews
    fun getAverageRating(): LiveData<Float> = mAverageRating

    fun addReview(uid: String, review: Review) = database.addElement(uid, review)

    override fun update(o: Observable?, arg: Any?) {
        val reviews: Map<String, Review> = database.elements

        mReviews.postValue(reviews)
        mAverageRating.postValue(Review.averageRating(reviews))
    }

    fun onDestroy() {
        database.onDestroy()
    }
}