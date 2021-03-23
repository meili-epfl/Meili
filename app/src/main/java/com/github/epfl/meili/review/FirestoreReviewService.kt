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

    override var reviews: Map<String, Review> = HashMap()
    override var averageRating: Float = 0f

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
            reviews = snapshot.associateBy ({ it.id }, {it.toObject(Review::class.java)!! })
            averageRating = Review.averageRating(reviews)

            this.notifyObservers()
        } else {
            Log.e(TAG, "Received null snapshot from Firestore")
        }
    }
}
