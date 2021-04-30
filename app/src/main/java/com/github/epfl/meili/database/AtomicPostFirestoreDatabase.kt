package com.github.epfl.meili.database

import android.util.Log
import com.github.epfl.meili.models.Post
import com.google.firebase.firestore.*

class AtomicPostFirestoreDatabase(path: String, ofClass: Class<Post>) :
    FirestoreDatabase<Post>(path, ofClass) {


    override var elements: Map<String, Post> = HashMap()

    fun updownvote(key: String, uid: String, isUpvote: Boolean) {
        val sfDocRef = ref.document(key)
        databaseProvider().runTransaction { transaction ->
            val snapshot = transaction.get(sfDocRef)
            val fieldName: String = if(isUpvote) "upvoters" else "downvoters"
            val otherFieldName: String = if(isUpvote) "downvoters" else "upvoters"
            var voters: ArrayList<String> = (snapshot.get(fieldName)!! as ArrayList<String>)
            var otherVoters: ArrayList<String> = (snapshot.get(otherFieldName)!! as ArrayList<String>)
            if (voters.contains(uid)) {
                voters.remove(uid)
            } else {
                if(otherVoters.contains(uid)) otherVoters.remove(uid)
                voters.add(uid)
            }
            //update to new upvoters list
            transaction.update(sfDocRef, fieldName, voters)
            transaction.update(sfDocRef, otherFieldName, otherVoters)
        }
    }

}