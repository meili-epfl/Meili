package com.github.epfl.meili.review

import android.util.Log
import com.github.epfl.meili.BuildConfig
import com.github.epfl.meili.models.Review
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase

class FirestoreReviewService(private val poiKey: String) : ReviewService(), EventListener<QuerySnapshot> {

    companion object {
        private const val TAG: String = "FirestoreReviewService"

        private val DEFAULT_DATABASE = { FirebaseFirestore.getInstance() }

        var databaseProvider: () -> FirebaseFirestore = DEFAULT_DATABASE
    }

    override var reviews: Map<String, Review> = HashMap()
    override var averageRating: Float = 0f

    private val ref: CollectionReference = databaseProvider().collection("reviews/$poiKey/poi_reviews")

    init {
        ref.addSnapshotListener(this)
    }

    override fun addReview(uid: String, review: Review) {
        ref.document(uid).set(review)
    }

    override fun onEvent(snapshot: QuerySnapshot?, error: FirebaseFirestoreException?) {
        if (error != null) {
            Log.e(TAG, "Firestore event error", error)
        }

        if (snapshot != null) {
            val rs: MutableMap<String, Review> = HashMap()

            for (document in snapshot.documents) {
                rs[document.id] = document.toObject(Review::class.java)!!
            }

            reviews = rs
            averageRating = Review.averageRating(reviews)

            this.notifyObservers()
        } else {
            Log.e(TAG, "Received null snapshot from Firestore")
        }
    }
}
