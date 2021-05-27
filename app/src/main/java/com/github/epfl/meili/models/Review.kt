package com.github.epfl.meili.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Review(
    var authorUid: String = "",
    var poiKey: String = "",
    var rating: Float = 0f,
    var title: String = "",
    var summary: String = ""
) : Parcelable {
    companion object {
        const val POI_KEY_FIELD = "poiKey"

        fun averageRating(reviews: Map<String, Review>): Float {
            return if (reviews.isEmpty()) {
                0f
            } else {
                reviews.values.map { r -> r.rating }.reduce { a, b -> a + b } / reviews.size
            }
        }
    }
}
