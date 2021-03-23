package com.github.epfl.meili.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Review (
    var rating: Float = 0f,
    var title: String = "",
    var summary: String = "",
): Parcelable {
    companion object {
        private const val TAG = "Review"
        fun averageRating(reviews: List<Review>): Float {
            return if (reviews.isEmpty()) {
                0f
            } else {
                reviews.map {r -> r.rating}.reduce {a,b -> a + b} / reviews.size
            }
        }
    }
}
