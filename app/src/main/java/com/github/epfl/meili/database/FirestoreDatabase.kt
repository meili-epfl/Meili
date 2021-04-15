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
<<<<<<< HEAD
                Log.d(TAG, document.toString())
                rs[document.id] = document.toObject(ofClass)!!
=======
                vs[document.id] = document.toObject(ofClass)!!
>>>>>>> 8edff669cec91d93862df81ac0d59c6bec5245a5
            }

            elements = vs

            this.notifyObservers()
        } else {
            Log.e(TAG, "Received null snapshot from Firestore")
        }
    }
}