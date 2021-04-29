package com.github.epfl.meili.database

import android.util.Log
import com.github.epfl.meili.models.Post
import com.google.firebase.firestore.*

class AtomicPostFirestoreDatabase(path: String, private val ofClass: Class<Post>) : Database<Post>(), EventListener<QuerySnapshot> {

    companion object {
        private const val TAG: String = "AtomicFirestoreDatabase"

        private val DEFAULT_DATABASE = { FirebaseFirestore.getInstance() }

        var databaseProvider: () -> FirebaseFirestore = DEFAULT_DATABASE
    }

    override var elements: Map<String, Post> = HashMap()

    private val registration: ListenerRegistration

    private val ref: CollectionReference = databaseProvider().collection(path)

    init {
        registration = ref.addSnapshotListener(this)
    }

    override fun addElement(key: String, element: Post?) {
        ref.document(key).set(element!!)
    }

     fun upvote(key: String, uid: String) {
         val sfDocRef = ref.document(key)
         databaseProvider().runTransaction { transaction ->
             val snapshot = transaction.get(sfDocRef)

             var upvoters: ArrayList<String> = (snapshot.get("upvoters")!! as ArrayList<String>)
             if(upvoters.contains(uid)){
                 upvoters.remove(uid)
             }else{
                 upvoters.add(uid)
             }
             //update to new upvoters list
             transaction.update(sfDocRef, "upvoters", upvoters)
         }
    }

    fun downvote(key: String, uid: String) {
        val sfDocRef = ref.document(key)
        databaseProvider().runTransaction { transaction ->
            val snapshot = transaction.get(sfDocRef)

            var downvoters: ArrayList<String> = (snapshot.get("downvoters")!! as ArrayList<String>)
            if(downvoters.contains(uid)){
                downvoters.remove(uid)
            }else{
                downvoters.add(uid)
            }
            //update to new upvoters list
            transaction.update(sfDocRef, "downvoters", downvoters)
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
            val vs: MutableMap<String, Post> = HashMap()

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