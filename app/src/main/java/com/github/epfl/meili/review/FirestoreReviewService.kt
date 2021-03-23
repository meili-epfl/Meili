package com.github.epfl.meili.review

import android.util.Log
import com.github.epfl.meili.BuildConfig
import com.github.epfl.meili.models.Review
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase

class FirestoreReviewService(poiKey: String) : ReviewService(poiKey), EventListener<QuerySnapshot> {
    companion object {
        private const val TAG: String = "FirestoreReviewService"
    }

    override var reviews: List<Review> = ArrayList()
    override var averageRating: Float = 0f
    override var currentUserHasReviewed: Boolean = false

    private val ref: CollectionReference = FirebaseFirestore.getInstance().collection("reviews/$poiKey/poi_reviews")

    init {
        ref.addSnapshotListener(this)
    }

    override fun addReview(review: Review) {
        if (BuildConfig.DEBUG && Firebase.auth.uid == null) {
            Log.e(TAG, "Only authenticated users can review")
        }
        ref.document(Firebase.auth.uid!!).set(review)
    }

    override fun onEvent(snapshot: QuerySnapshot?, error: FirebaseFirestoreException?) {
        if (error != null) {
            Log.e(TAG, "Firestore event error", error)
        }

        if (snapshot != null) {
            reviews = snapshot.toObjects(Review::class.java)
            averageRating = Review.averageRating(reviews)
            currentUserHasReviewed = snapshot.map {s -> s.id}.contains(Firebase.auth.uid!!)

            this.notifyObservers()
        } else {
            Log.e(TAG, "Received null snapshot from Firestore")
        }
    }
}
