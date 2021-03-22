package com.github.epfl.meili.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ReviewsActivityViewModelFactory(private val reviewService: ReviewService): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReviewsActivityViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReviewsActivityViewModel(reviewService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
