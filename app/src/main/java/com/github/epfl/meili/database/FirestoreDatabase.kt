package com.github.epfl.meili.database

import android.util.Log
import com.google.firebase.firestore.*

open class FirestoreDatabase<T: Any>(path: String, private val ofClass: Class<T>) : Database<T>(), EventListener<QuerySnapshot> {

    companion object {
        private const val TAG: String = "FirestoreDatabase"

        var databaseProvider: () -> FirebaseFirestore = { FirebaseFirestore.getInstance() }
    }

    override var elements: Map<String, T> = HashMap()

    private val registration: ListenerRegistration

    val ref: CollectionReference = databaseProvider().collection(path)

    init {
        registration = ref.addSnapshotListener(this)
    }

    override fun addElement(key: String, element: T?) {
        ref.document(key).set(element!!)
    }

    override fun updateElement(key: String, element: T?) {
        ref.document(key).delete().addOnSuccessListener {  }
            .addOnCanceledListener {  }
        ref.document(key).delete().addOnSuccessListener {
            addElement(key, element)
        }
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