package com.github.epfl.meili.review

import android.util.Log
import com.github.epfl.meili.models.Review
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase

class FirestoreReviewSerivce(poiKey: String) : ReviewService(poiKey), EventListener<QuerySnapshot> {
    companion object {
        private const val TAG: String = "FirestoreReviewService"
    }

    override var reviews: List<Review> = ArrayList()
    private val ref: CollectionReference = FirebaseFirestore.getInstance()
            .collection("reviews")
            .document(poiKey)
            .collection("poi_reviews")

    init {
        ref.addSnapshotListener(this)
    }

    override fun addReview(review: Review) {
        if (Firebase.auth.uid != null) {
            ref.document(Firebase.auth.uid!!).set(review)
        } else {
            ref.add(review)
        }
    }

    override fun onEvent(snapshot: QuerySnapshot?, error: FirebaseFirestoreException?) {
        if (error != null) {
            Log.e(TAG, "Firestore event error", error)
        }

        if (snapshot != null) {
            reviews = snapshot.map {doc -> doc.toObject(Review::class.java)}.toList()
            Log.e(TAG, "$reviews")
            this.notifyObservers()
        } else {
            Log.e(TAG, "Received null snapshot from Firestore")
        }
    }
}
