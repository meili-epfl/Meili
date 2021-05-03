package com.github.epfl.meili.database

import com.github.epfl.meili.models.Post

class AtomicPostFirestoreDatabase(path: String) : FirestoreDatabase<Post>(path, Post::class.java) {
    companion object {
        private const val upVoters = "upvoters"
        private const val downVoters = "downvoters"
    }

    fun upDownVote(key: String, uid: String, isUpvote: Boolean) {
        val sfDocRef = ref.document(key)
        databaseProvider().runTransaction { transaction ->
            val snapshot = transaction.get(sfDocRef)
            val fieldName: String = if(isUpvote) upVoters else downVoters
            val otherFieldName: String = if(isUpvote) downVoters else upVoters
            val voters: ArrayList<String> = (snapshot.get(fieldName)!! as ArrayList<String>)
            val otherVoters: ArrayList<String> = (snapshot.get(otherFieldName)!! as ArrayList<String>)
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