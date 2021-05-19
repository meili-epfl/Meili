package com.github.epfl.meili.database

import android.util.Log
import com.google.firebase.firestore.*

open class FirestoreDatabase<T : Any>(
    path: String,
    private val ofClass: Class<T>,
    query: (CollectionReference) -> Query = { it }
) : Database<T>(), EventListener<QuerySnapshot> {

    companion object {
        private const val TAG: String = "FirestoreDatabase"

        const val MAX_EQUALITY_CLAUSES = 10

        var databaseProvider: () -> FirebaseFirestore = { FirebaseFirestore.getInstance() }
    }

    override var elements: Map<String, T> = HashMap()

    protected val collectionReference = databaseProvider().collection(path)

    private val registration = query(collectionReference).addSnapshotListener(this)

    override fun addElement(key: String, element: T?) {
        collectionReference.document(key).set(element!!)
    }

    override fun onEvent(snapshot: QuerySnapshot?, error: FirebaseFirestoreException?) {
        if (error != null) {
            Log.e(TAG, "Firestore event error", error)
        }

        if (snapshot != null) {
            val vs: MutableMap<String, T> = HashMap()

            for (document in snapshot.documents) {
                vs[document.id] = document.toObject(ofClass)!!
            }

            elements = vs

            this.notifyObservers()
        } else {
            Log.e(TAG, "Received null snapshot from Firestore")
        }
    }

    override fun onDestroy() {
        registration.remove()
    }
}