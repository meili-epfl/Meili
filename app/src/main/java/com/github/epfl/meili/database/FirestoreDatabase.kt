package com.github.epfl.meili.database

import android.util.Log
import com.google.firebase.firestore.*

class FirestoreDatabase<T: Any>(path: String, val ofClass: Class<T>) : Database<T>(path), EventListener<QuerySnapshot> {

    companion object {
        private const val TAG: String = "FirestoreReviewService"

        private val DEFAULT_DATABASE = { FirebaseFirestore.getInstance() }

        var databaseProvider: () -> FirebaseFirestore = DEFAULT_DATABASE
    }

    override var values: Map<String, T> = HashMap()

    private val registration: ListenerRegistration

    private val ref: CollectionReference = databaseProvider().collection(path)

    init {
        registration = ref.addSnapshotListener(this)
    }

    override fun addElement(uid: String, element: T) {
        ref.document(uid).set(element!!)
    }

    override fun onDestroy() {
        registration.remove()
    }

    override fun onEvent(snapshot: QuerySnapshot?, error: FirebaseFirestoreException?) {
        if (error != null) {
            Log.e(TAG, "Firestore event error", error)
        }

        if (snapshot != null) {
            val rs: MutableMap<String, T> = HashMap()

            for (document in snapshot.documents) {
                rs[document.id] = document.toObject(ofClass)!!
            }

            values = rs

            this.notifyObservers()
        } else {
            Log.e(TAG, "Received null snapshot from Firestore")
        }
    }
}