package com.github.epfl.meili.database

import android.util.Log
import com.google.firebase.firestore.*

class FirestoreDatabase<T: Any>(private val path: String, private val ofClass: Class<T>) : Database<T>(path), EventListener<QuerySnapshot> {

    companion object {
        private const val TAG: String = "FirestoreDatabase"

        private val DEFAULT_DATABASE = { FirebaseFirestore.getInstance() }

        var databaseProvider: () -> FirebaseFirestore = DEFAULT_DATABASE
    }

    override var elements: Map<String, T> = HashMap()

    private val registration: ListenerRegistration

    private val ref: CollectionReference = databaseProvider().collection(path)

    init {
        registration = ref.addSnapshotListener(this)
    }

    override fun addElement(uid: String, element: T?) {
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
}